package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.EventParticipant;
import dubbelf.fr.agendabackend.bo.PlayerEventSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerEventSettingRepository extends JpaRepository<PlayerEventSetting, UUID> {

    List<PlayerEventSetting> findAllByEventParticipant(EventParticipant eventParticipant);
}