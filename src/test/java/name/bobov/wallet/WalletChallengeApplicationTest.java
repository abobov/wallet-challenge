package name.bobov.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import name.bobov.wallet.api.dto.BalanceDto;
import name.bobov.wallet.api.dto.ErrorMessage;
import name.bobov.wallet.api.dto.TransactionRequest;
import name.bobov.wallet.exceptions.InsufficientBalanceException;
import name.bobov.wallet.exceptions.TransactionAlreadyExists;
import name.bobov.wallet.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

/**
 * Integration test of wallet service functionality.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class WalletChallengeApplicationTest {

  private static final String PLAYERS_RESOURCE = "/players/{id}";

  private static final String TRANSACTIONS_RESOURCE = "/players/{id}/transactions";

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private TestServiceTemplate service;

  private String playerId;

  @BeforeEach
  void setUp() {
    service = new TestServiceTemplate(restTemplate, port);
    playerId = UUID.randomUUID().toString();
  }

  @Test
  void shouldUpdatePlayerBalanceWithCreditTransaction() {
    var creditTransaction = TransactionRequest.credit(playerId, BigDecimal.TEN);

    var creditResponse = service.post(TRANSACTIONS_RESOURCE, creditTransaction,
                                      ErrorMessage.class, playerId);
    var balanceResponse = service.get(PLAYERS_RESOURCE, BalanceDto.class, playerId);

    assertThat(creditResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(balanceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(balanceResponse.getBody())
      .extracting(BalanceDto::getCurrentBalance)
      .usingComparator(BigDecimal::compareTo)
      .isEqualTo(BigDecimal.TEN);
  }

  @Test
  void shouldUpdatePlayerBalanceWithDebitTransaction() {
    var creditTransaction = TransactionRequest.credit(playerId, BigDecimal.TEN);
    var debitTransaction = TransactionRequest.debit(playerId, BigDecimal.valueOf(5));
    service.post(TRANSACTIONS_RESOURCE, creditTransaction, ErrorMessage.class, playerId);

    var debitResponse = service.post(TRANSACTIONS_RESOURCE, debitTransaction,
                                     ErrorMessage.class, playerId);

    var balanceResponse = service.get(PLAYERS_RESOURCE, BalanceDto.class, playerId);

    assertThat(debitResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(balanceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(balanceResponse.getBody())
      .extracting(BalanceDto::getCurrentBalance)
      .usingComparator(BigDecimal::compareTo)
      .isEqualTo(BigDecimal.valueOf(5));
  }

  @Test
  void shouldFailTransactionIfPlayerBalanceTooLow() {
    var transaction = TransactionRequest.debit(playerId, BigDecimal.TEN);

    var response = service.post(TRANSACTIONS_RESOURCE, transaction, ErrorMessage.class, playerId);

    var expectedError = new ErrorMessage(new InsufficientBalanceException());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    assertThat(response.getBody()).isEqualTo(expectedError);
  }

  @Test
  void shouldFailTransactionIfNotUniqueTransactionId() {
    var creditTransaction = TransactionRequest.credit(playerId, BigDecimal.TEN);
    service.post(TRANSACTIONS_RESOURCE, creditTransaction, ErrorMessage.class, playerId);

    var response = service.post(TRANSACTIONS_RESOURCE, creditTransaction,
                                ErrorMessage.class, playerId);

    var expectedError = new ErrorMessage(new TransactionAlreadyExists(creditTransaction.getId()));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isEqualTo(expectedError);
  }

  @Test
  void shouldReturnListOfPlayerTransactions() {
    var transactions = List.of(
      TransactionRequest.credit(playerId, BigDecimal.valueOf(1)),
      TransactionRequest.credit(playerId, BigDecimal.valueOf(2)),
      TransactionRequest.credit(playerId, BigDecimal.valueOf(3))
    );
    for (TransactionRequest transaction : transactions) {
      service.post(TRANSACTIONS_RESOURCE, transaction, ErrorMessage.class, playerId);
    }

    var list = service.getList(TRANSACTIONS_RESOURCE, playerId);

    assertThat(list.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(list.getBody()).hasSize(3);
  }

  @Test
  void shouldFailTransactionWithNegativeAmount() {
    var transaction = TransactionRequest.credit(playerId, BigDecimal.ONE.negate());

    var response = service.post(TRANSACTIONS_RESOURCE, transaction,
                                ErrorMessage.class, playerId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDetail())
      .isEqualTo("Transaction amount must be greater than or equal to 0");
  }

  @ParameterizedTest
  @MethodSource("invalidTransactions")
  void shouldFailTransactionWithEmptyFields(String id, TransactionType type, BigDecimal amount) {
    var transaction = new TransactionRequest(id, playerId, type, amount);

    var response = service.post(TRANSACTIONS_RESOURCE, transaction,
                                ErrorMessage.class, playerId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDetail()).matches("Transaction \\w+ is required");
  }

  public static Stream<Arguments> invalidTransactions() {
    return Stream.of(
      Arguments.of(null, TransactionType.Credit, BigDecimal.TEN),
      Arguments.of(UUID.randomUUID().toString(), null, BigDecimal.TEN),
      Arguments.of(UUID.randomUUID().toString(), TransactionType.Credit, null)
    );
  }

}
