package name.bobov.wallet.model;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity for {@code Player}.
 */
@Entity
@Data
@NoArgsConstructor
public class Player {

  @Id
  private String id;

  /**
   * Player current balance.
   */
  @Min(0)
  @NotNull
  private BigDecimal balance = BigDecimal.ZERO;

  /**
   * Player version for optimistic locking.
   */
  @Version
  private long version;

  /**
   * Builds new player with default balance.
   *
   * @param id player ID
   */
  public Player(String id) {
    this.id = id;
  }

  public Player(String id, BigDecimal balance) {
    this(id);
    this.balance = balance;
  }

  public void addToBalance(BigDecimal amount) {
    balance = balance.add(amount);
  }

  public boolean balanceLessThen(BigDecimal amount) {
    return balance.compareTo(amount) < 0;
  }
}
