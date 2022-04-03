package name.bobov.wallet.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity for {@code Transaction}.
 */
@Entity
@Data
@NoArgsConstructor
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(unique = true)
  private String transactionId;

  @NotBlank
  private String playerId;

  @NotNull
  private TransactionType type;

  @Min(0)
  @NotNull
  private BigDecimal amount;

  public Transaction(String transactionId, String playerId, TransactionType type,
                     BigDecimal amount) {
    this.transactionId = transactionId;
    this.playerId = playerId;
    this.type = type;
    this.amount = amount;
  }
}
