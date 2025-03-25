package dubbelf.fr.agendabackend.security;

import dubbelf.fr.agendabackend.bll.UserService;
import dubbelf.fr.agendabackend.bo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class MyUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserService userService;

    @Autowired
    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Comment est-ce qu'on va chercher un utilisateur Spring Security à partir d'un pseudo?
     * => à partir du service utilisateurService
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User utilisateur = userService.getUserByUsernameOrEmail(username);

        // Si l'utilisateur n'est pas trouvé : je lance une exception afin que Spring Security affiche une erreur dans le formulaire
        if (utilisateur == null) {
            throw new UsernameNotFoundException(username);
        }

        Collection<? extends GrantedAuthority> authorities;
        if (utilisateur.isAdministrateur()) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Si on a trouvé un utilisateur, je retourne un objet UserDetails Spring Security qui englobe les informations de l'utilisateur
        return new UtilisateurSpringSecurity(utilisateur.getId(), utilisateur.getPseudo(), utilisateur.getEmail(), utilisateur.getPassword(), authorities);
    }
}