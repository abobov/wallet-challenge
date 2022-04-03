package name.bobov.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import name.bobov.wallet.api.dto.ErrorMessage;
import name.bobov.wallet.api.dto.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Integration test of idempotent requests.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class WalletChallengeIdempotencyTest {

  private final String idempotencyKey = UUID.randomUUID().toString();

  private final String transactionId = UUID.randomUUID().toString();

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private String serviceUrl;

  @BeforeEach
  void setUp() {
    serviceUrl = "http://localhost:" + port + "/players/{id}/transactions";
  }

  @Test
  void shouldRespectIdempotencyHeaderOnTransactionRequest() {
    var playerId = UUID.randomUUID().toString();

    var firstResponse = postTransaction(playerId);
    var secondResponse = postTransaction(playerId);

    assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void shouldDistinctSameIdempotencyHeaderOnDifferentPlayers() {
    var firstResponse = postTransaction(UUID.randomUUID().toString());
    var secondResponse = postTransaction(UUID.randomUUID().toString());

    assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  private ResponseEntity<ErrorMessage> postTransaction(String playerId) {
    var transaction = TransactionRequest.credit(transactionId, playerId, BigDecimal.TEN);
    var headers = new HttpHeaders();
    headers.add(WalletConstants.IDEMPOTENCY_KEY_HEADER, idempotencyKey);
    return restTemplate.postForEntity(serviceUrl, new HttpEntity<>(transaction, headers),
                                      ErrorMessage.class, playerId);
  }

}
