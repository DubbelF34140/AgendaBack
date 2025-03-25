package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.GroupMember;
import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.dal.GroupMemberRepository;
import dubbelf.fr.agendabackend.dal.UserRepository;
import dubbelf.fr.agendabackend.dto.MemberRespond;
import dubbelf.fr.agendabackend.dto.UserRespond;
import dubbelf.fr.agendabackend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private JwtUtils jwtUtils;

    public UserRespond getCurrentUser(String token) {
        User user = userRepository.findBypseudo(jwtUtils.getUserNameFromJwtToken(token));

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        UserRespond userRespond = new UserRespond();
        userRespond.setAvatarUrl(user.getAvatarUrl());
        userRespond.setEmail(user.getEmail());
        userRespond.setPseudo(user.getPseudo());
        userRespond.setId(user.getId());
        userRespond.setCreatedAt(user.getCreatedAt());
        return userRespond;
    }

    public User getUserByUsernameOrEmail(String pseudo) {
        User user = userRepository.findBypseudo(pseudo);
        if (user == null) {
            user = userRepository.findByEmail(pseudo);
        }
        return user;
    }

    public List<UserRespond> getAllUser(UUID groupID) {
        List<UserRespond> list = new ArrayList<>();
        List<User> users = userRepository.findAll(); // Liste de tous les utilisateurs
        List<User> usersgroup = groupMemberRepository.findUsersByGroupId(groupID); // Membres du groupe

        // Créer une liste des IDs des utilisateurs déjà présents dans le groupe
        Set<UUID> groupUserIds = new HashSet<>();
        for (User user : usersgroup) {
            groupUserIds.add(user.getId()); // Ajouter chaque ID de membre de groupe à un set
        }

        // Ajouter à la liste seulement les utilisateurs qui ne sont pas dans le groupe
        for (User user : users) {
            if (!groupUserIds.contains(user.getId())) { // Vérifie si l'utilisateur n'est pas dans le groupe
                UserRespond userRespond = new UserRespond();
                userRespond.setId(user.getId());
                userRespond.setCreatedAt(user.getCreatedAt());
                userRespond.setEmail(user.getEmail());
                userRespond.setPseudo(user.getPseudo());
                userRespond.setAvatarUrl(user.getAvatarUrl());
                list.add(userRespond); // Ajouter l'utilisateur à la liste
            }
        }

        return list;
    }
}
