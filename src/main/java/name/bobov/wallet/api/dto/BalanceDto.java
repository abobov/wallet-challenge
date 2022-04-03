package name.bobov.wallet.api.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for presenting player balance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {

  private BigDecimal currentBalance;

}
