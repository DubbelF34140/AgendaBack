package dubbelf.fr.agendabackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameSettingDTO {
    private String key;
    private String valueType;
    private String defaultValue;
}
