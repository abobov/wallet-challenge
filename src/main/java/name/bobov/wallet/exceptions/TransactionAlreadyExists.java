package name.bobov.wallet.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when transaction with same ID already exists.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class TransactionAlreadyExists extends WalletException {

  public TransactionAlreadyExists(String transactionId) {
    this(transactionId, null);
  }

  public TransactionAlreadyExists(String transactionId, Throwable cause) {
    super("Transaction with ID '" + transactionId + "' already exists.", cause);
  }
}
