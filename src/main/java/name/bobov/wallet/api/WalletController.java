package name.bobov.wallet.api;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import name.bobov.wallet.WalletService;
import name.bobov.wallet.api.dto.BalanceDto;
import name.bobov.wallet.api.dto.TransactionDto;
import name.bobov.wallet.api.dto.TransactionListRequest;
import name.bobov.wallet.api.dto.TransactionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API endpoint for wallet service.
 */
@RestController
@RequestMapping("/players/{playerId}")
@RequiredArgsConstructor
public class WalletController {

  private final WalletService walletService;

  @GetMapping
  public BalanceDto currentBalanceFor(@PathVariable String playerId) {
    return walletService.currentBalanceFor(playerId);
  }

  @GetMapping("/transactions")
  public List<TransactionDto> listOfTransactions(@PathVariable String playerId) {
    TransactionListRequest request = new TransactionListRequest();
    request.setPlayerId(playerId);
    return walletService.listOfTransactionsFor(request);
  }

  @PostMapping("/transactions")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addTransaction(@PathVariable String playerId,
                             @Valid @RequestBody TransactionRequest request) {
    request.setPlayerId(playerId);
    walletService.addTransaction(request);
  }

}
