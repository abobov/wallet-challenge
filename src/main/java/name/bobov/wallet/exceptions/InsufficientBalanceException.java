package name.bobov.wallet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when player has insufficient for transaction.
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class InsufficientBalanceException extends WalletException {

  public InsufficientBalanceException() {
    super("Insufficient player balance.");
  }
}
