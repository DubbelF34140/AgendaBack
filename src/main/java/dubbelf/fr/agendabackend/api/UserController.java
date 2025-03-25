package dubbelf.fr.agendabackend.api;

import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.bll.UserService;
import dubbelf.fr.agendabackend.dto.UserRespond;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public UserRespond getUser(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return userService.getCurrentUser(token);
        } else {
            throw new RuntimeException("Authorization token is missing or invalid");
        }
    }
}
