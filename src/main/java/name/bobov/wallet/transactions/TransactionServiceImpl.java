package name.bobov.wallet.transactions;

import java.util.List;
import lombok.RequiredArgsConstructor;
import name.bobov.wallet.api.dto.TransactionRequest;
import name.bobov.wallet.exceptions.InsufficientBalanceException;
import name.bobov.wallet.model.Player;
import name.bobov.wallet.model.Transaction;
import name.bobov.wallet.repository.PlayerRepository;
import name.bobov.wallet.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transaction balance implementation.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final PlayerRepository playerRepository;

  private final TransactionRepository transactionRepository;

  @Override
  public List<Transaction> listFor(String playerId) {
    return transactionRepository.findByPlayerId(playerId);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void add(TransactionRequest request) {
    var player = findPlayerOrCreateNew(request.getPlayerId());
    updatePlayerBalance(player, request);
    saveTransaction(request);
  }

  private Player findPlayerOrCreateNew(String playerId) {
    return playerRepository.findById(playerId)
      .orElseGet(() -> playerRepository.save(new Player(playerId)));
  }

  private void updatePlayerBalance(Player player, TransactionRequest request) {
    if (request.isDebit() && player.balanceLessThen(request.getAmount())) {
      throw new InsufficientBalanceException();
    }
    player.addToBalance(request.getAmountForAddition());
  }

  private void saveTransaction(TransactionRequest request) {
    transactionRepository.save(mapToTransaction(request));
  }

  private Transaction mapToTransaction(TransactionRequest request) {
    var transaction = new Transaction();
    transaction.setTransactionId(request.getId());
    transaction.setPlayerId(request.getPlayerId());
    transaction.setType(request.getType());
    transaction.setAmount(request.getAmount());
    return transaction;
  }
}
