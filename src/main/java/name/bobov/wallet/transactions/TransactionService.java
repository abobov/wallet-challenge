package name.bobov.wallet.transactions;

import java.util.List;
import name.bobov.wallet.api.dto.TransactionRequest;
import name.bobov.wallet.exceptions.InsufficientBalanceException;
import name.bobov.wallet.model.Transaction;

/**
 * Implementing this interface allows to work with player transactions.
 */
public interface TransactionService {

  /**
   * List transactions per player.
   *
   * @param playerId player id
   * @return list of transactions
   */
  List<Transaction> listFor(String playerId);

  /**
   * Save transaction and updates player current balance.
   *
   * @param request new transaction
   * @throws InsufficientBalanceException if transaction cannot be performed due player balance
   */
  void add(TransactionRequest request);

}
