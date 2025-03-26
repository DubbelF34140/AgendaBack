package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByGroupId(UUID groupId);
}
