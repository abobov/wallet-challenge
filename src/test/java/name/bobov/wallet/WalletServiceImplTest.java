package name.bobov.wallet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import name.bobov.wallet.api.dto.BalanceDto;
import name.bobov.wallet.api.dto.TransactionDto;
import name.bobov.wallet.api.dto.TransactionListRequest;
import name.bobov.wallet.api.dto.TransactionRequest;
import name.bobov.wallet.balance.BalanceService;
import name.bobov.wallet.exceptions.TransactionAlreadyExists;
import name.bobov.wallet.model.Transaction;
import name.bobov.wallet.model.TransactionType;
import name.bobov.wallet.transactions.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

  private static final String PLAYER_ID = "player-id";

  @Mock
  private BalanceService balanceService;

  @Mock
  private TransactionService transactionService;

  @InjectMocks
  private WalletServiceImpl walletService;

  @Mock
  private TransactionRequest transactionRequest;

  @Test
  void shouldDelegateBalanceRequestToBalanceService() {
    var balance = mock(BalanceDto.class);
    when(balanceService.currentBalanceFor(PLAYER_ID)).thenReturn(balance);

    assertThat(walletService.currentBalanceFor(PLAYER_ID))
      .isEqualTo(balance);
  }

  @Test
  void shouldDelegateAddTransactionToTransactionService() {
    walletService.addTransaction(transactionRequest);

    verify(transactionService).add(transactionRequest);
  }

  @Test
  void shouldRetryAddTransactionOnOptimisticLockingFailure() {
    doThrow(ObjectOptimisticLockingFailureException.class)
      .doNothing()
      .when(transactionService).add(any());

    walletService.addTransaction(transactionRequest);

    verify(transactionService, times(2)).add(transactionRequest);
  }

  @Test
  void shouldFailToAddTransactionOnDataIntegrityViolation() {
    doThrow(DataIntegrityViolationException.class)
      .when(transactionService).add(any());

    assertThatExceptionOfType(TransactionAlreadyExists.class)
      .isThrownBy(() -> walletService.addTransaction(transactionRequest));
  }

  @Test
  void shouldReturnListOfTransactionsForPlayer() {
    var transaction = new Transaction("tx", PLAYER_ID, TransactionType.Credit, BigDecimal.TEN);
    var request = new TransactionListRequest(PLAYER_ID);
    when(transactionService.listFor(PLAYER_ID))
      .thenReturn(List.of(transaction));

    var transactions = walletService.listOfTransactionsFor(request);

    assertThat(transactions)
      .hasSize(1)
      .contains(new TransactionDto("tx", TransactionType.Credit, BigDecimal.TEN));
  }
}
