package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.*;
import dubbelf.fr.agendabackend.dal.EventRepository;
import dubbelf.fr.agendabackend.dal.GroupMemberRepository;
import dubbelf.fr.agendabackend.dal.GroupRepository;
import dubbelf.fr.agendabackend.dal.UserRepository;
import dubbelf.fr.agendabackend.dto.EventDTO;
import dubbelf.fr.agendabackend.dto.GroupDTO;
import dubbelf.fr.agendabackend.dto.MemberRespond;
import dubbelf.fr.agendabackend.dto.RespondEventDTO;
import dubbelf.fr.agendabackend.mapper.EventMapper;
import dubbelf.fr.agendabackend.mapper.GroupMapper;
import dubbelf.fr.agendabackend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private EventRepository eventRepository;

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
        List<Group> Groups = groupRepository.findByUsersContains(userOpt);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        if (userOpt.get().isAdministrateur()){
            List<Group> AdminGroups = groupRepository.findAll();
            return AdminGroups.stream().map(GroupMapper::toDTO).collect(Collectors.toList());
        }
        return Groups.stream().map(GroupMapper::toDTO).collect(Collectors.toList());
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
    public void deleteGroup(UUID groupId, String jwtToken) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Group not found");
        }
        if (userRepository.findUserById(jwtUtils.getIDFromJwtToken(jwtToken)).getId() != groupOpt.get().getCreatedBy().getId()){
            throw new RuntimeException("Only Owner can delete group");
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
            userRespond.setId(member.getId());
            userRespondList.add(userRespond);
        }

        return userRespondList;
    }

    public void inviteUserToGroup(UUID groupId, UUID userId, String jwtToken) {
        // Vérifier si le groupe existe
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);

        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

        // Vérifier si l'utilisateur actuel est le propriétaire, admin ou modérateur
        if (!isUserAuthorizedToInviteUserInGroup(currentUserGroupMember)) {
            throw new RuntimeException("User does not have permission to remove this member");
        }

        // Vérifier si l'utilisateur existe
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier si l'utilisateur n'est pas déjà membre du groupe
        boolean isAlreadyMember = group.getUsers().stream().anyMatch(member -> member.getId().equals(userId));
        if (isAlreadyMember) {
            throw new RuntimeException("User is already a member of the group");
        }

        GroupMember groupMember = new GroupMember(user, group);
        GroupMemberId groupMemberId = new GroupMemberId(group.getId(), user.getId());
        groupMember.setId(groupMemberId);
        groupMemberRepository.save(groupMember);
    }

    public void removeUserFromGroup(UUID groupId, UUID userId, String jwtToken) {
        // Vérifier si l'utilisateur qui veut supprimer a les droits nécessaires
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

        // Vérifier si l'utilisateur actuel est le propriétaire, admin ou modérateur
        if (!isUserAuthorizedToModifyGroup(currentUserGroupMember)) {
            throw new RuntimeException("User does not have permission to remove this member");
        }

        // Vérifier si l'utilisateur à supprimer est dans le groupe
        GroupMember groupMemberToRemove = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("User not in this group"));

        // Supprimer le membre du groupe
        groupMemberRepository.delete(groupMemberToRemove);
    }

    public boolean isUserAuthorizedToModifyGroup(GroupMember groupMember) {
        Role role = groupMember.getRole();
        return role == Role.OWNER || role == Role.ADMIN || role == Role.MODERATOR;
    }

    public boolean isUserAuthorizedToInviteUserInGroup(GroupMember groupMember) {
        Role role = groupMember.getRole();
        return role == Role.OWNER || role == Role.ADMIN || role == Role.MODERATOR || role == Role.MEMBER;
    }

    public List<RespondEventDTO> getGroupEvents(UUID groupId, String jwtToken) {
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);
        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

        List<Event> events = eventRepository.findAllByGroupId(groupId);

        return events.stream().map(EventMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void promoteMember(UUID groupId, UUID userId, String jwtToken) {
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);

        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

        GroupMember memberToPromote = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("User not in this group"));

        Role currentRole = memberToPromote.getRole();
        Role newRole = getNextHigherRole(currentRole, currentUserGroupMember.getRole());

        if (newRole == null) {
            throw new RuntimeException("User is already at the highest role or cannot be promoted.");
        }

        memberToPromote.setRole(newRole);
        groupMemberRepository.save(memberToPromote);
    }

    @Transactional
    public void demoteMember(UUID groupId, UUID userId, String jwtToken) {
        UUID currentUserId = jwtUtils.getIDFromJwtToken(jwtToken);

        GroupMember currentUserGroupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User not a member of this group"));

        GroupMember memberToDemote = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("User not in this group"));

        if (memberToDemote.getRole() == Role.OWNER) {
            throw new RuntimeException("You cannot demote the OWNER.");
        }

        Role currentRole = memberToDemote.getRole();
        Role newRole = getNextLowerRole(currentRole);

        if (newRole == null) {
            throw new RuntimeException("User is already at the lowest role.");
        }

        memberToDemote.setRole(newRole);
        groupMemberRepository.save(memberToDemote);
    }

    private Role getNextHigherRole(Role currentRole, Role promotingUserRole) {
        if (promotingUserRole == Role.OWNER && currentRole == Role.ADMIN) {
            return Role.OWNER; // Seul l'OWNER peut promouvoir un ADMIN en OWNER
        }

        return switch (currentRole) {
            case GUEST -> Role.MEMBER;
            case MEMBER -> Role.MODERATOR;
            case MODERATOR -> Role.OWNER;
            case OWNER -> Role.ADMIN;
            case ADMIN -> null;
        };
    }

    private Role getNextLowerRole(Role currentRole) {
        return switch (currentRole) {
            case ADMIN -> Role.OWNER;
            case OWNER -> Role.MODERATOR;
            case MODERATOR -> Role.MEMBER;
            case MEMBER  -> Role.GUEST; // MEMBER est déjà le plus bas et OWNER ne peut pas être rétrogradé
            case GUEST -> null;
        };
    }


}
