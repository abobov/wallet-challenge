package name.bobov.wallet;

import java.util.List;
import name.bobov.wallet.api.dto.BalanceDto;
import name.bobov.wallet.api.dto.TransactionDto;
import name.bobov.wallet.api.dto.TransactionListRequest;
import name.bobov.wallet.api.dto.TransactionRequest;
import name.bobov.wallet.exceptions.InsufficientBalanceException;
import name.bobov.wallet.exceptions.TransactionAlreadyExists;

/**
 * Facade for wallet service.
 */
public interface WalletService {

  /**
   * Query player current balance.
   *
   * @param playerId player ID
   * @return current balance
   * @see name.bobov.wallet.balance.BalanceService#currentBalanceFor(String)
   */
  BalanceDto currentBalanceFor(String playerId);

  /**
   * Add new transaction per player.
   *
   * @param request transaction request
   * @throws InsufficientBalanceException if transaction cannot be performed due player balance
   * @throws TransactionAlreadyExists if transaction id is not unique
   * @see name.bobov.wallet.transactions.TransactionService#add(TransactionRequest)
   */
  void addTransaction(TransactionRequest request);

  /**
   * List transaction per player
   *
   * @param request list query
   * @return list of transactions
   * @see name.bobov.wallet.transactions.TransactionService#listFor(String)
   */
  List<TransactionDto> listOfTransactionsFor(TransactionListRequest request);
}
