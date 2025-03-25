package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.dal.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;



    public User register(String email, String username, String password) {
        User user = new User();
        user.setAdministrateur(false);
        user.setAvatarUrl(null);
        user.setEmail(email);
        user.setPseudo(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public Boolean existEmail(String email){
        if (userRepository.existsByEmail(email)) {
            return true;
        }else {
            return false;
        }
    }

    public Boolean existexistsPseudo(String pseudo){
        if (userRepository.existsByPseudo(pseudo)) {
            return true;
        }else {
            return false;
        }
    }

    // Authentifier un utilisateur et générer un token
    public User login(String email, String password) {
        // Trouver l'utilisateur par son email
        User user = userRepository.findByEmail(email);

        // Vérifier si l'utilisateur existe et si le mot de passe correspond
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        // Si l'utilisateur est invalide ou le mot de passe incorrect, lancer une exception
        throw new RuntimeException("Mot de passe invalide");
    }
}
