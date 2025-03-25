package dubbelf.fr.agendabackend.api;

import dubbelf.fr.agendabackend.bll.AuthService;
import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.dto.LoginRequest;
import dubbelf.fr.agendabackend.dto.UserRegistrationDto;
import dubbelf.fr.agendabackend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto userRegistrationDto) {
        // Vérifier si l'email ou le pseudo existent déjà
        if (authService.existEmail(userRegistrationDto.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email déjà utilisé");
        }
        if (authService.existexistsPseudo(userRegistrationDto.getPseudo())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pseudo déjà utilisé");
        }

        // Enregistrer l'utilisateur
        User user = authService.register(userRegistrationDto.getEmail(), userRegistrationDto.getPseudo(), userRegistrationDto.getPassword());

        if (user != null) {
            // Authentifier l'utilisateur immédiatement après l'enregistrement
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getPseudo(), userRegistrationDto.getPassword()));  // Utiliser le mot de passe en clair

            // Après l'authentification, définir l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Générer le JWT token après l'authentification réussie
            String jwt = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok(new LoginResponse(jwt));  // Retourner le JWT dans la réponse
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering user");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Essayez de récupérer l'utilisateur
            User user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

            // Authentifier l'utilisateur avec son pseudo et le mot de passe en clair
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getPseudo(), loginRequest.getPassword()));  // Utiliser le mot de passe en clair

            // Si l'authentification réussie, mettre l'utilisateur dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Générer le JWT après l'authentification réussie
            String jwt = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok(new LoginResponse(jwt));  // Retourner le JWT dans la réponse
        } catch (BadCredentialsException e) {
            // Si les identifiants sont incorrects
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            // Capturer d'autres erreurs génériques
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }


    public static class LoginResponse {
        private String token;

        public LoginResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}
