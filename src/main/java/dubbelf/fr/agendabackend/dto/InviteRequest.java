package dubbelf.fr.agendabackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class InviteRequest {
    private UUID userId;
}
