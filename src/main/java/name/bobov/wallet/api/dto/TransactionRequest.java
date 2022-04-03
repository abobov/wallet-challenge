package name.bobov.wallet.api.dto;

import java.math.BigDecimal;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.bobov.wallet.model.TransactionType;

/**
 * DTO for new transaction request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

  @NotBlank(message = "Transaction id is required")
  private String id;

  private String playerId;

  @NotNull(message = "Transaction type is required")
  private TransactionType type;

  @NotNull(message = "Transaction amount is required")
  @Min(value = 0, message = "Transaction amount must be greater than or equal to 0")
  private BigDecimal amount;

  /**
   * Creates new transaction of type {@link TransactionType#Credit}.
   *
   * @param playerId player id
   * @param amount transaction amount
   * @return new transaction request
   */
  public static TransactionRequest credit(String playerId, BigDecimal amount) {
    return credit(UUID.randomUUID().toString(), playerId, amount);
  }

  /**
   * Creates new transaction of type {@link TransactionType#Credit}.
   *
   * @param transactionId transaction id
   * @param playerId player id
   * @param amount transaction amount
   * @return new transaction request
   */
  public static TransactionRequest credit(String transactionId, String playerId,
                                          BigDecimal amount) {
    return new TransactionRequest(transactionId, playerId, TransactionType.Credit, amount);
  }

  /**
   * Creates new transaction of type {@link TransactionType#Debit}.
   *
   * @param playerId player id
   * @param amount transaction amount
   * @return new transaction request
   */
  public static TransactionRequest debit(String playerId, BigDecimal amount) {
    return debit(UUID.randomUUID().toString(), playerId, amount);
  }

  /**
   * Creates new transaction of type {@link TransactionType#Debit}.
   *
   * @param transactionId transaction id
   * @param playerId player id
   * @param amount transaction amount
   * @return new transaction request
   */
  public static TransactionRequest debit(String transactionId, String playerId, BigDecimal amount) {
    return new TransactionRequest(transactionId, playerId, TransactionType.Debit, amount);
  }

  /**
   * Return amount of transaction with sign based on transaction type.
   *
   * @return transaction amount
   */
  public BigDecimal getAmountForAddition() {
    if (type == TransactionType.Debit) {
      return amount.negate();
    }
    return amount;
  }

  /**
   * Check if transaction is {@link TransactionType#Debit}.
   *
   * @return true - if transaction is {@link TransactionType#Debit}, false - otherwise
   */
  public boolean isDebit() {
    return type == TransactionType.Debit;
  }
}
