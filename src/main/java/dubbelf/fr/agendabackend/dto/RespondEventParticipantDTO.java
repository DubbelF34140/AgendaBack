package dubbelf.fr.agendabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RespondEventParticipantDTO {
    private String username;
    private List<PlayerGameSettingDTO> playersetting;

}
