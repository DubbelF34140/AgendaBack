package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.*;
import dubbelf.fr.agendabackend.dto.EventParticipationDTO;
import dubbelf.fr.agendabackend.dal.*;
import dubbelf.fr.agendabackend.dto.PlayerGameSettingDTO;
import dubbelf.fr.agendabackend.dto.RespondEventParticipantDTO;
import dubbelf.fr.agendabackend.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventSettingRepository eventSettingRepository;
    private final GroupMemberRepository groupMemberRepository;

    private final PlayerEventSettingRepository playerEventSettingRepository;

    private final JwtUtils jwtUtils;

    public EventParticipantService(EventParticipantRepository eventParticipantRepository, EventRepository eventRepository, UserRepository userRepository, EventSettingRepository eventSettingRepository, GroupMemberRepository groupMemberRepository, PlayerEventSettingRepository playerEventSettingRepository, JwtUtils jwtUtils) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventSettingRepository = eventSettingRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.playerEventSettingRepository = playerEventSettingRepository;
        this.jwtUtils = jwtUtils;
    }

    public List<RespondEventParticipantDTO> getParticipantsForEvent(UUID eventId) {
        // Récupérer les participants pour l'événement
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);

        // Transformer chaque participant en DTO en récupérant les PlayerEventSettings et le pseudo de l'utilisateur
        return participants.stream()
                .map(participant -> {
                    // Récupérer les PlayerEventSettings pour chaque participant
                    List<PlayerEventSetting> playerEventSettings = playerEventSettingRepository.findAllByEventParticipant(participant);

                    // Transformer chaque PlayerEventSetting en PlayerGameSettingDTO
                    List<PlayerGameSettingDTO> playerGameSettingsDTO = playerEventSettings.stream()
                            .map(setting -> new PlayerGameSettingDTO(setting.getKey(), setting.getValue()))
                            .collect(Collectors.toList());

                    // Retourner le DTO avec le pseudo de l'utilisateur et les PlayerGameSettingDTO associés
                    return new RespondEventParticipantDTO(participant.getUser().getPseudo(), playerGameSettingsDTO);
                })
                .collect(Collectors.toList());
    }



    @Transactional
    public EventParticipant registerForEvent(UUID eventID, String jwtToken, EventParticipationDTO eventParticipationDTO) {
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(event.getGroup().getId(), currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

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

        // Créer les PlayerEventSetting et les associer à l'EventParticipant

        // Vérifier le nombre max de joueurs par équipe
        long currentPlayersInTeam = eventParticipantRepository.findByEventId(event.getId()).stream()
                .filter(p -> p.getTeam().equals("Base"))
                .count();

        if (maxPlayersPerTeam > 0 && currentPlayersInTeam >= maxPlayersPerTeam) {
            throw new RuntimeException("This team is already full.");
        }

        // Créer l'EventParticipant
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        participant.setTeam("Base");

        List<PlayerEventSetting> playersettings = eventParticipationDTO.getPlayersetting().stream().map(dto -> {
            PlayerEventSetting setting = new PlayerEventSetting();
            setting.setKey(dto.getKey());
            setting.setValue(dto.getValue());
            setting.setEventParticipant(participant);
            return setting;
        }).collect(Collectors.toList());

        participant.setPlayereventSettings(playersettings);
        // Sauvegarder l'EventParticipant et ses PlayerEventSettings
        return eventParticipantRepository.save(participant);
    }
    public void unregisterFromEvent(UUID eventID, String jwtToken) {
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        // Récupérer l'événement
        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Récupérer l'utilisateur
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier si l'utilisateur est bien inscrit à l'événement
        EventParticipant participant = eventParticipantRepository.findByEventIdAndUserId(event.getId(), user.getId())
                .orElseThrow(() -> new RuntimeException("User is not registered for this event"));

        // Supprimer l'enregistrement de l'utilisateur dans cet événement
        eventParticipantRepository.delete(participant);

    }

}
