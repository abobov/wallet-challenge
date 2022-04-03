package name.bobov.wallet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for query player transactions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionListRequest {

  private String playerId;

}
