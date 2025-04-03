package dubbelf.fr.agendabackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateDTO {
    private String name;
    private String description;
    private String avatarUrl;
    private List<GameSettingDTO> settings;
    private List<CreatePlayerGameSettingDTO> playersettings;
}
