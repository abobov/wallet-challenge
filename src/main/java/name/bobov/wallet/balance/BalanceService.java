package name.bobov.wallet.balance;

import name.bobov.wallet.api.dto.BalanceDto;

/**
 * Implementing this interface allows to work with player balance.
 */
public interface BalanceService {

  /**
   * Query current balance for player.
   *
   * @param playerId player id
   * @return current balance for player or balance with zero amount if player not exists
   */
  BalanceDto currentBalanceFor(String playerId);
}
