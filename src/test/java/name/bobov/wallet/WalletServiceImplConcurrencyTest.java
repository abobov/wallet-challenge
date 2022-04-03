package name.bobov.wallet;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import name.bobov.wallet.api.dto.BalanceDto;
import name.bobov.wallet.api.dto.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test of concurrent transactions.
 */
@SpringBootTest
class WalletServiceImplConcurrencyTest {

  @Autowired
  private WalletServiceImpl walletService;

  @Test
  void shouldUpdateBalanceConcurrently() throws InterruptedException {
    var playerId = UUID.randomUUID().toString();
    walletService.addTransaction(TransactionRequest.credit(playerId, BigDecimal.ZERO));

    var transactions = IntStream.range(0, 10)
      .mapToObj(x -> TransactionRequest.credit(playerId, BigDecimal.valueOf(10)))
      .collect(Collectors.toList());

    var executorService = Executors.newFixedThreadPool(transactions.size());
    for (TransactionRequest transaction : transactions) {
      executorService.execute(() -> walletService.addTransaction(transaction));
    }
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    BalanceDto balanceDto = walletService.currentBalanceFor(playerId);
    assertThat(balanceDto.getCurrentBalance())
      .isEqualByComparingTo(BigDecimal.valueOf(100));
  }

}
