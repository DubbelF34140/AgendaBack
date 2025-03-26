package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.*;
import dubbelf.fr.agendabackend.dto.EventParticipationDTO;
import dubbelf.fr.agendabackend.dal.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventSettingRepository eventSettingRepository;

    public EventParticipantService(EventParticipantRepository eventParticipantRepository, EventRepository eventRepository, UserRepository userRepository, EventSettingRepository eventSettingRepository) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventSettingRepository = eventSettingRepository;
    }

    public EventParticipant registerForEvent(EventParticipationDTO dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Récupérer les règles du jeu
        List<EventSetting> settings = eventSettingRepository.findByEventId(event.getId());
        int nbTeams = settings.stream()
                .filter(s -> s.getKey().equals("nbTeams"))
                .mapToInt(s -> Integer.parseInt(s.getValue()))
                .findFirst()
                .orElse(0);

        int maxPlayersPerTeam = settings.stream()
                .filter(s -> s.getKey().equals("maxPlayersPerTeam"))
                .mapToInt(s -> Integer.parseInt(s.getValue()))
                .findFirst()
                .orElse(0);

        // Vérifier si l’équipe existe
        if (nbTeams > 0 && (dto.getTeam() == null || dto.getTeam().isBlank())) {
            throw new RuntimeException("This event requires team selection.");
        }

        // Vérifier le nombre max de joueurs par équipe
        long currentPlayersInTeam = eventParticipantRepository.findByEventId(event.getId()).stream()
                .filter(p -> p.getTeam().equals(dto.getTeam()))
                .count();

        if (maxPlayersPerTeam > 0 && currentPlayersInTeam >= maxPlayersPerTeam) {
            throw new RuntimeException("This team is already full.");
        }

        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        participant.setTeam(dto.getTeam());

        return eventParticipantRepository.save(participant);
    }
}
