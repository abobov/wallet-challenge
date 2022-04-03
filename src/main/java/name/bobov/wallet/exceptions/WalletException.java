package name.bobov.wallet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * {@code WalletException} is the superclass of those exceptions that can be thrown during the
 * operation of wallet service.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class WalletException extends RuntimeException {

  public WalletException(String message) {
    super(message);
  }

  public WalletException(String message, Throwable cause) {
    super(message, cause);
  }

}
