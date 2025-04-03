package dubbelf.fr.agendabackend.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespondGameDTO {
    private UUID id;
    private String name;
    private String description;
    private String avatarUrl;
    private List<RespondGameSettingDTO> settings;
    private List<RespondGamePlayerSettingDTO> playersettings;
}
