package dubbelf.fr.agendabackend.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationDTO {
    private UUID eventId;
    private UUID userId;
    private String team;
}
