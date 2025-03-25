package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
}
