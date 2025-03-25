package dubbelf.fr.agendabackend.mapper;

import dubbelf.fr.agendabackend.bo.Group;
import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.dto.GroupDTO;
import dubbelf.fr.agendabackend.dto.UserRespond;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupMapper {

    public static GroupDTO toDTO(Group group) {
        if (group == null) {
            return null;
        }

        GroupDTO dto = new GroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setCreatedBy(toUserDTO(group.getCreatedBy()));

        // Mapper les utilisateurs en DTO
        dto.setUsers(group.getUsers().stream().map(GroupMapper::toUserDTO).collect(Collectors.toSet()));

        return dto;
    }

    private static UserRespond toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserRespond dto = new UserRespond();
        dto.setId(user.getId());
        dto.setPseudo(user.getPseudo());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }
}
