package dubbelf.fr.agendabackend.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespondEventSettingDTO {
    private UUID id;
    private String key;
    private String value;
}
