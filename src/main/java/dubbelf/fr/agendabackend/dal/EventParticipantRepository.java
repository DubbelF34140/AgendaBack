package dubbelf.fr.agendabackend.dal;

import dubbelf.fr.agendabackend.bo.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {
    List<EventParticipant> findByEventId(UUID eventId);

    Optional<EventParticipant> findByEventIdAndUserId(UUID id, UUID id1);
}
