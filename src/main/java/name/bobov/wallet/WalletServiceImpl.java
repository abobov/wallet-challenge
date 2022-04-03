package name.bobov.wallet;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import name.bobov.wallet.api.dto.BalanceDto;
import name.bobov.wallet.api.dto.TransactionDto;
import name.bobov.wallet.api.dto.TransactionListRequest;
import name.bobov.wallet.api.dto.TransactionRequest;
import name.bobov.wallet.balance.BalanceService;
import name.bobov.wallet.exceptions.TransactionAlreadyExists;
import name.bobov.wallet.transactions.TransactionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

/**
 * Implementation of wallet service functions.
 */
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final BalanceService balanceService;

  private final TransactionService transactionService;

  @Override
  public BalanceDto currentBalanceFor(String playerId) {
    return balanceService.currentBalanceFor(playerId);
  }

  @Override
  public void addTransaction(@Valid TransactionRequest request) {
    try {
      transactionService.add(request);
    } catch (DataIntegrityViolationException e) {
      throw new TransactionAlreadyExists(request.getId(), e);
    } catch (ObjectOptimisticLockingFailureException e) {
      addTransaction(request);
    }
  }

  @Override
  public List<TransactionDto> listOfTransactionsFor(TransactionListRequest request) {
    return transactionService.listFor(request.getPlayerId())
      .stream()
      .map(tx -> new TransactionDto(tx.getTransactionId(), tx.getType(), tx.getAmount()))
      .collect(Collectors.toList());
  }
}
