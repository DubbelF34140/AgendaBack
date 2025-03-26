package dubbelf.fr.agendabackend.dto;

import dubbelf.fr.agendabackend.bo.Game;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespondEventDTO {
    private UUID id;
    private UUID groupId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Game game;
    private String color;
    private List<RespondEventSettingDTO> eventSettings;
}