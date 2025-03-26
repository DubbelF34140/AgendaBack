package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.*;
import dubbelf.fr.agendabackend.dal.*;
import dubbelf.fr.agendabackend.dto.EventDTO;
import dubbelf.fr.agendabackend.dto.EventSettingDTO;
import dubbelf.fr.agendabackend.security.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventSettingRepository eventSettingRepository;
    private final GameRepository gameRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final JwtUtils jwtUtils;

    public EventService(EventRepository eventRepository, EventSettingRepository eventSettingRepository, GameRepository gameRepository, GroupRepository groupRepository, GroupMemberRepository groupMemberRepository, JwtUtils jwtUtils) {
        this.eventRepository = eventRepository;
        this.eventSettingRepository = eventSettingRepository;
        this.gameRepository = gameRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public Event createEvent(EventDTO eventDTO, String jwtToken) {
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(eventDTO.getGroupId(), currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

        Group group = groupRepository.findById(eventDTO.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Game game = gameRepository.findById(eventDTO.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setColor(eventDTO.getColor());
        event.setGroup(group);
        event.setGame(game);

        Event savedEvent = eventRepository.save(event);

        List<EventSetting> eventSettings = eventDTO.getSettings().stream().map(settingDTO -> {
            EventSetting setting = new EventSetting();
            setting.setEvent(savedEvent);
            setting.setKey(settingDTO.getKey());
            setting.setValue(settingDTO.getValue());
            return setting;
        }).collect(Collectors.toList());

        eventSettingRepository.saveAll(eventSettings);
        savedEvent.setEventSettings(eventSettings);

        return savedEvent;
    }

    @Transactional
    public void deleteEvent(UUID eventId, String jwtToken) {
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(event.getGroup().getId(), currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

        // Vérifier si l'utilisateur a les droits pour supprimer cet événement
        if (!isUserAuthorizedToModifyGroup(currentUserGroupMember)) {
            throw new RuntimeException("User is not authorized to delete this event");
        }

        // Supprimer les paramètres associés
        eventSettingRepository.deleteByEventId(eventId);

        // Supprimer l'événement
        eventRepository.delete(event);
    }

    // Modifier un événement
    @Transactional
    public Event updateEvent(UUID eventId, EventDTO eventDTO, String jwtToken) {
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(event.getGroup().getId(), currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

        // Vérifier si l'utilisateur a les droits pour modifier cet événement
        if (!isUserAuthorizedToModifyGroup(currentUserGroupMember)) {
            throw new RuntimeException("User is not authorized to modify this event");
        }

        // Mettre à jour les informations de l'événement
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setColor(eventDTO.getColor());

        // Si le jeu ou le groupe a changé
        if (eventDTO.getGameId() != null) {
            Game game = gameRepository.findById(eventDTO.getGameId())
                    .orElseThrow(() -> new RuntimeException("Game not found"));
            event.setGame(game);
        }
        if (eventDTO.getGroupId() != null) {
            Group group = groupRepository.findById(eventDTO.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            event.setGroup(group);
        }

        // Sauvegarder les nouvelles informations
        Event updatedEvent = eventRepository.save(event);

        // Supprimer et recréer les paramètres d'événement
        eventSettingRepository.deleteByEventId(eventId);
        List<EventSetting> eventSettings = eventDTO.getSettings().stream().map(settingDTO -> {
            EventSetting setting = new EventSetting();
            setting.setEvent(updatedEvent);
            setting.setKey(settingDTO.getKey());
            setting.setValue(settingDTO.getValue());
            return setting;
        }).collect(Collectors.toList());

        eventSettingRepository.saveAll(eventSettings);
        updatedEvent.setEventSettings(eventSettings);

        return updatedEvent;
    }

    public boolean isUserAuthorizedToModifyGroup(GroupMember groupMember) {
        Role role = groupMember.getRole();
        return role == Role.OWNER || role == Role.ADMIN || role == Role.MODERATOR;
    }

}
