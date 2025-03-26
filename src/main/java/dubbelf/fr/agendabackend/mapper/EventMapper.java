package dubbelf.fr.agendabackend.mapper;

import dubbelf.fr.agendabackend.bo.Event;
import dubbelf.fr.agendabackend.bo.EventSetting;
import dubbelf.fr.agendabackend.dto.EventDTO;
import dubbelf.fr.agendabackend.dto.EventSettingDTO;
import dubbelf.fr.agendabackend.dto.RespondEventDTO;
import dubbelf.fr.agendabackend.dto.RespondEventSettingDTO;

import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {
    public static RespondEventDTO toDTO(Event event) {
        return RespondEventDTO.builder()
                .id(event.getId())
                .groupId(event.getGroup().getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .color(event.getColor())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .game(event.getGame())
                .eventSettings(event.getEventSettings().stream()
                        .map(EventMapper::toEventSettingDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private static RespondEventSettingDTO toEventSettingDTO(EventSetting setting) {
        return RespondEventSettingDTO.builder()
                .id(setting.getId())
                .key(setting.getKey())
                .value(setting.getValue())
                .build();
    }
}
