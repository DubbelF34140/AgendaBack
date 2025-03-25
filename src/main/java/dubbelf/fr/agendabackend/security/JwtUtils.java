package dubbelf.fr.agendabackend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.dal.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Component
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private UserRepository userRepository;

    public static String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }

    public String generateJwtToken(Authentication authentication) {
        UtilisateurSpringSecurity userPrincipal = (UtilisateurSpringSecurity) authentication.getPrincipal();

        User user = userRepository.findBypseudo(userPrincipal.getUsername());

        return JWT.create()
                .withClaim("username", userPrincipal.getUsername())
                .withClaim("admin", user.isAdministrateur())
                .sign(Algorithm.HMAC256(jwtSecret));
    }
    public String getUserNameFromJwtToken(String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(token).getClaim("username").toString().replaceAll("\"", "");
    }

    public UUID getIDFromJwtToken(String token) {
        String username = JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(token).getClaim("username").toString().replaceAll("\"", "");
        User user = userRepository.findBypseudo(username);
        return  user.getId();
    }

    public Boolean getRoleFromJwtToken(String token) {
        String data = JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(token).getClaim("admin").toString().replaceAll("\"", "");
        if (data == "true"){
            return true;
        }else {
            return false;
        }
    }


    public boolean validateJwtToken(String authToken) {
        try {
            JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(authToken);
            return true;
        } catch (Exception e) {
            System.out.println("error : " + e.getStackTrace());
        }
        return false;
    }
}
