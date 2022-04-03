package name.bobov.wallet.repository;

import name.bobov.wallet.model.Player;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link Player} entity;
 */
public interface PlayerRepository extends CrudRepository<Player, String> {

}
