package dubbelf.fr.agendabackend.api;

import dubbelf.fr.agendabackend.bo.Event;
import dubbelf.fr.agendabackend.dto.EventDTO;
import dubbelf.fr.agendabackend.bll.EventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static dubbelf.fr.agendabackend.security.JwtUtils.parseJwt;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
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
}
