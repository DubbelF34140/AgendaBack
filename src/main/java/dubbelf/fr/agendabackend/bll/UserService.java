package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.dal.UserRepository;
import dubbelf.fr.agendabackend.dto.UserRespond;
import dubbelf.fr.agendabackend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
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
}
