package name.bobov.wallet.balance;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import name.bobov.wallet.api.dto.BalanceDto;
import name.bobov.wallet.model.Player;
import name.bobov.wallet.repository.PlayerRepository;
import org.springframework.stereotype.Service;

/**
 * Player balance implementation with players stored in {@link PlayerRepository}.
 */
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

  private final PlayerRepository playerRepository;

  @Override
  public BalanceDto currentBalanceFor(String playerId) {
    return playerRepository.findById(playerId)
      .map(Player::getBalance)
      .map(BalanceDto::new)
      .orElseGet(() -> new BalanceDto(BigDecimal.ZERO));
  }

}
