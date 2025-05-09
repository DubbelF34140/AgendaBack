package dubbelf.fr.agendabackend.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationDTO {
    private List<PlayerGameSettingDTO> playersetting;
}
