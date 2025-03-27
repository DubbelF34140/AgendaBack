package dubbelf.fr.agendabackend.api;

import dubbelf.fr.agendabackend.bll.EventParticipantService;
import dubbelf.fr.agendabackend.bo.Event;
import dubbelf.fr.agendabackend.bo.EventParticipant;
import dubbelf.fr.agendabackend.dto.EventDTO;
import dubbelf.fr.agendabackend.bll.EventService;
import dubbelf.fr.agendabackend.dto.EventParticipationDTO;
import dubbelf.fr.agendabackend.dto.PlayerGameSettingDTO;
import dubbelf.fr.agendabackend.dto.RespondEventParticipantDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static dubbelf.fr.agendabackend.security.JwtUtils.parseJwt;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    private final EventParticipantService eventParticipantService;

    public EventController(EventService eventService, EventParticipantService eventParticipantService) {
        this.eventService = eventService;
        this.eventParticipantService = eventParticipantService;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO, HttpServletRequest request) {
        String jwtToken = parseJwt(request);
        Event createdEvent = eventService.createEvent(eventDTO, jwtToken);
        return ResponseEntity.ok(createdEvent);
    }

    // Supprimer un événement
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId, HttpServletRequest request) {
        String jwtToken = parseJwt(request);
        eventService.deleteEvent(eventId, jwtToken);
        return ResponseEntity.noContent().build();
    }

    // Modifier un événement
    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable UUID eventId, @RequestBody EventDTO eventDTO, HttpServletRequest request) {
        String jwtToken = parseJwt(request);
        Event updatedEvent = eventService.updateEvent(eventId, eventDTO, jwtToken);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<RespondEventParticipantDTO>> getParticipants(@PathVariable UUID eventId) {
        List<RespondEventParticipantDTO> participants = eventParticipantService.getParticipantsForEvent(eventId);
        return ResponseEntity.ok(participants);
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<EventParticipant> registerForEvent(@PathVariable UUID eventId, HttpServletRequest request, @RequestBody EventParticipationDTO eventParticipationDTO) {
        // Extraire le JWT de la requête
        String jwtToken = parseJwt(request);

        // Appel du service pour enregistrer l'utilisateur à l'événement
        EventParticipant participant = eventParticipantService.registerForEvent(eventId, jwtToken, eventParticipationDTO);

        return ResponseEntity.ok(participant);
    }

    // Route pour se désenregistrer d'un événement
    @PostMapping("/{eventId}/unregister")
    public ResponseEntity<Void> unregisterFromEvent(@PathVariable UUID eventId, HttpServletRequest request) {
        // Extraire le JWT de la requête
        String jwtToken = parseJwt(request);

        // Appel du service pour désenregistrer l'utilisateur de l'événement
        eventParticipantService.unregisterFromEvent(eventId, jwtToken);

        return ResponseEntity.noContent().build();
    }
}
