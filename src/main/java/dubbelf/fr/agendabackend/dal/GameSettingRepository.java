package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.GameSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameSettingRepository extends JpaRepository<GameSetting, UUID> {
}