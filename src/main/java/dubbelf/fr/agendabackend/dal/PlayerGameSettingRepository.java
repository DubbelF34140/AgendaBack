package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.PlayerGameSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerGameSettingRepository extends JpaRepository<PlayerGameSetting, UUID> {
}