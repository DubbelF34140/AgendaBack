package dubbelf.fr.agendabackend.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespondGameSettingDTO {
    private UUID id;
    private String key;
    private String valueType;
    private String defaultValue;
}
