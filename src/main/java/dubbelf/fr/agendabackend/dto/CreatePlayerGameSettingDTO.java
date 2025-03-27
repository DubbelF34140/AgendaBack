package dubbelf.fr.agendabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlayerGameSettingDTO {
    private String key;
    private String valueType;
    private String defaultValue;
}
