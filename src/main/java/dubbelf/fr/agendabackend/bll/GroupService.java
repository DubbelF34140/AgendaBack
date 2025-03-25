package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.*;
import dubbelf.fr.agendabackend.dal.GroupMemberRepository;
import dubbelf.fr.agendabackend.dal.GroupRepository;
import dubbelf.fr.agendabackend.dal.UserRepository;
import dubbelf.fr.agendabackend.dto.GroupDTO;
import dubbelf.fr.agendabackend.dto.MemberRespond;
import dubbelf.fr.agendabackend.mapper.GroupMapper;
import dubbelf.fr.agendabackend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    // Récupérer tous les groupes auxquels l'utilisateur appartient ou est propriétaire
    public List<GroupDTO> getUserGroups(String jwtToken) {
        UUID userId = jwtUtils.getIDFromJwtToken(jwtToken);
        Optional<User> userOpt = userRepository.findById(userId);
        List<Group> createdGroups = groupRepository.findByUsersContains(userOpt);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return createdGroups.stream().map(GroupMapper::toDTO).collect(Collectors.toList());
    }

    // Créer un nouveau groupe
    @Transactional
    public Group createGroup(String jwtToken, String name, String description) {
        UUID userId = jwtUtils.getIDFromJwtToken(jwtToken);
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Création du groupe
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedBy(user);

        // Sauvegarde du groupe
        group = groupRepository.save(group);

        // Création de la relation GroupMember
        GroupMemberId groupMemberId = new GroupMemberId(group.getId(), user.getId());
        GroupMember groupMember = new GroupMember(user, group);
        groupMember.setRole(Role.OWNER);
        groupMember.setId(groupMemberId);

        groupMemberRepository.save(groupMember);
        return group;
    }

    // Modifier un groupe existant
    public Group updateGroup(UUID groupId, String name, String description) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Group not found");
        }
        Group group = groupOpt.get();
        group.setName(name);
        group.setDescription(description);
        return groupRepository.save(group);
    }

    // Supprimer un groupe
    public void deleteGroup(UUID groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Group not found");
        }
        groupRepository.delete(groupOpt.get());
    }

    public Group getGroup(UUID groupId, String jwtToken) {
        Group group = groupRepository.findGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group not found");
        }

        UUID userId = jwtUtils.getIDFromJwtToken(jwtToken);
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (group.getCreatedBy().equals(user) || group.getUsers().contains(user)) {
            return group;
        } else {
            throw new RuntimeException("User not in this group");
        }
    }

    public List<MemberRespond> getMembersGroup(UUID groupId, String jwtToken) {
        // Vérification et récupération du groupe
        Group group = groupRepository.findGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Group not found");
        }

        // Vérification et récupération de l'utilisateur depuis le JWT
        UUID userId = jwtUtils.getIDFromJwtToken(jwtToken);
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();

        // Vérifier si l'utilisateur est le créateur du groupe ou un membre
        boolean isAuthorized = group.getCreatedBy().equals(user) ||
                group.getUsers().stream().anyMatch(member -> member.equals(user));
        if (!isAuthorized) {
            throw new RuntimeException("User not in this group");
        }

        // Construction de la liste des membres avec leur rôle
        List<MemberRespond> userRespondList = new ArrayList<>();
        List<GroupMember> groupMembers = groupMemberRepository.findByGroup(group);

        for (GroupMember groupMember : groupMembers) {
            User member = groupMember.getUser();
            MemberRespond userRespond = new MemberRespond();
            userRespond.setPseudo(member.getPseudo());
            userRespond.setEmail(member.getEmail());
            userRespond.setAvatarUrl(member.getAvatarUrl());
            userRespond.setCreatedAt(member.getCreatedAt());
            userRespond.setRole(groupMember.getRole());
            userRespondList.add(userRespond);
        }

        return userRespondList;
    }

}