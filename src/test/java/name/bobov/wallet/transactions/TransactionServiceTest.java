package name.bobov.wallet.transactions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import name.bobov.wallet.api.dto.TransactionRequest;
import name.bobov.wallet.exceptions.InsufficientBalanceException;
import name.bobov.wallet.model.Player;
import name.bobov.wallet.model.Transaction;
import name.bobov.wallet.model.TransactionType;
import name.bobov.wallet.repository.PlayerRepository;
import name.bobov.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  private static final BigDecimal SOME_AMOUNT = BigDecimal.valueOf(5);

  private static final String TRANSACTION_ID = "transaction-id";

  private static final String PLAYER_ID = "player-id";

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private TransactionServiceImpl transactionService;

  private final Player player = new Player();

  @Test
  void shouldReturnEmptyListOfTransactionsForUnknownPlayer() {
    var transactions = transactionService.listFor(PLAYER_ID);

    assertThat(transactions).isEmpty();
    verify(transactionRepository).findByPlayerId(PLAYER_ID);
  }

  @Test
  void shouldReturnListOfPlayerTransactions() {
    Transaction transaction = new Transaction();
    when(transactionRepository.findByPlayerId(PLAYER_ID)).thenReturn(List.of(transaction));

    var transactions = transactionService.listFor(PLAYER_ID);

    assertThat(transactions).containsExactly(transaction);
  }

  @Test
  void shouldCreatePlayerOnTransactionIfNotExists() {
    when(playerRepository.save(any())).thenReturn(player);

    transactionService.add(TransactionRequest.credit(PLAYER_ID, SOME_AMOUNT));

    InOrder inOrder = inOrder(playerRepository);
    inOrder.verify(playerRepository).findById(PLAYER_ID);
    inOrder.verify(playerRepository).save(new Player(PLAYER_ID));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void shouldAddNewTransaction() {
    givenPlayer();
    var transactionRequest = TransactionRequest.credit(TRANSACTION_ID, PLAYER_ID, SOME_AMOUNT);

    transactionService.add(transactionRequest);

    var expectedTransaction = new Transaction();
    expectedTransaction.setTransactionId(TRANSACTION_ID);
    expectedTransaction.setAmount(SOME_AMOUNT);
    expectedTransaction.setType(TransactionType.Credit);
    expectedTransaction.setPlayerId(PLAYER_ID);
    verify(transactionRepository).save(expectedTransaction);
  }

  @Test
  void shouldUpdatePlayerBalanceOnCreditTransaction() {
    givenPlayer();

    transactionService.add(TransactionRequest.credit(PLAYER_ID, SOME_AMOUNT));

    assertThat(player.getBalance()).isEqualByComparingTo(SOME_AMOUNT);
  }

  @Test
  void shouldUpdatePlayerBalanceOnDebitTransaction() {
    player.setBalance(BigDecimal.valueOf(10));
    givenPlayer();

    transactionService.add(TransactionRequest.debit(PLAYER_ID, BigDecimal.valueOf(5)));

    assertThat(player.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(5));
  }

  @Test
  void shouldFailToWithdrawIfPlayerBalanceIsInsufficient() {
    givenPlayer();
    var transaction = TransactionRequest.debit(TRANSACTION_ID, PLAYER_ID, SOME_AMOUNT);

    assertThatExceptionOfType(InsufficientBalanceException.class)
      .isThrownBy(() -> transactionService.add(transaction));
  }

  private void givenPlayer() {
    when(playerRepository.findById(PLAYER_ID)).thenReturn(Optional.of(player));
  }

}
