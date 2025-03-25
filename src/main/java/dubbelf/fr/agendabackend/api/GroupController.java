package dubbelf.fr.agendabackend.api;

import dubbelf.fr.agendabackend.bo.Group;
import dubbelf.fr.agendabackend.bll.GroupService;
import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.dto.GroupDTO;
import dubbelf.fr.agendabackend.dto.MemberRespond;
import dubbelf.fr.agendabackend.dto.UserRespond;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/groups")
@CrossOrigin(origins = "*")
public class GroupController {

    @Autowired
    private GroupService groupService;

    // Méthode pour extraire le jeton JWT depuis l'en-tête de la requête
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Retourne le token sans "Bearer "
        }
        return null; // Retourne null si le token n'est pas présent
    }

    // Récupérer les groupes dont l'utilisateur est membre ou propriétaire
    @GetMapping
    public ResponseEntity<?> getUserGroups(HttpServletRequest request) {
        // Utilisez parseJwt pour extraire le JWT
        String jwtToken = parseJwt(request);

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token is missing or invalid");
        }

        List<GroupDTO> groups = groupService.getUserGroups(jwtToken);
        return ResponseEntity.ok(groups);
    }

    // Créer un nouveau groupe
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Group group, HttpServletRequest request) {
        // Utilisez parseJwt pour extraire le JWT
        String jwtToken = parseJwt(request);

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token is missing or invalid");
        }

        Group createdGroup = groupService.createGroup(jwtToken, group.getName(), group.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    // Modifier un groupe existant
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable UUID groupId, @RequestBody Group group, HttpServletRequest request) {
        // Utilisez parseJwt pour extraire le JWT
        String jwtToken = parseJwt(request);

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token is missing or invalid");
        }

        Group updatedGroup = groupService.updateGroup(groupId, group.getName(), group.getDescription());
        return ResponseEntity.ok(updatedGroup);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable UUID groupId, HttpServletRequest request) {
        // Utilisez parseJwt pour extraire le JWT
        String jwtToken = parseJwt(request);

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token is missing or invalid");
        }

        Group updatedGroup = groupService.getGroup(groupId, jwtToken);
        return ResponseEntity.ok(updatedGroup);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getMembersGroup(@PathVariable UUID groupId, HttpServletRequest request) {
        // Utilisez parseJwt pour extraire le JWT
        String jwtToken = parseJwt(request);

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token is missing or invalid");
        }

        List<MemberRespond> userRespondList = groupService.getMembersGroup(groupId, jwtToken);
        return ResponseEntity.ok(userRespondList);
    }

    // Supprimer un groupe
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable UUID groupId, HttpServletRequest request) {
        // Utilisez parseJwt pour extraire le JWT
        String jwtToken = parseJwt(request);

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("JWT token is missing or invalid");
        }

        groupService.deleteGroup(groupId);
        return ResponseEntity.noContent().build();
    }
}