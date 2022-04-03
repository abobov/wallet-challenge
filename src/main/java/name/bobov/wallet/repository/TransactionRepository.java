package name.bobov.wallet.repository;

import java.util.List;
import name.bobov.wallet.model.Transaction;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link Transaction} entity;
 */
public interface TransactionRepository extends CrudRepository<Transaction, String> {

  /**
   * Find all transaction per player.
   *
   * @param playerId player id
   * @return list of transactions
   */
  List<Transaction> findByPlayerId(String playerId);

}
