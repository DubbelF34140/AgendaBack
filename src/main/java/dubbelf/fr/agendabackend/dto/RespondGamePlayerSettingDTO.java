package dubbelf.fr.agendabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespondGamePlayerSettingDTO {
    private UUID id;
    private String key;
    private String valueType;
    private String defaultValue;
}
