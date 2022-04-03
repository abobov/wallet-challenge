package name.bobov.wallet.balance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import name.bobov.wallet.model.Player;
import name.bobov.wallet.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

  private static final String PLAYER_ID = "player";

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private BalanceServiceImpl balanceService;

  @Test
  void shouldReturnZeroBalanceForNotExistentPlayer() {
    var balance = balanceService.currentBalanceFor(PLAYER_ID);

    assertThat(balance.getCurrentBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    verify(playerRepository).findById(PLAYER_ID);
  }

  @Test
  void shouldReturnBalanceForPlayer() {
    when(playerRepository.findById(PLAYER_ID))
      .thenReturn(Optional.of(new Player(PLAYER_ID, BigDecimal.TEN)));

    var balance = balanceService.currentBalanceFor(PLAYER_ID);

    assertThat(balance.getCurrentBalance()).isEqualByComparingTo(BigDecimal.TEN);
  }

}
