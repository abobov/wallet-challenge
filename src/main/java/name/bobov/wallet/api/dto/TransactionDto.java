package name.bobov.wallet.api.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import name.bobov.wallet.model.TransactionType;

/**
 * DTO presenting single player transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

  private String id;

  private TransactionType type;

  private BigDecimal amount;

}
