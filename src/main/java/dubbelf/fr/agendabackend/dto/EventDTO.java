package dubbelf.fr.agendabackend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String color;
    private UUID groupId;
    private UUID gameId;
    private List<EventSettingDTO> settings;
}
