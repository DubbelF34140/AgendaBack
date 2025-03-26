package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.EventSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventSettingRepository extends JpaRepository<EventSetting, UUID> {
    List<EventSetting> findByEventId(UUID id);

    void deleteByEventId(UUID eventId);
}
