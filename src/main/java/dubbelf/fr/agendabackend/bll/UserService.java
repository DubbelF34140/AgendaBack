package dubbelf.fr.agendabackend.bll;

import dubbelf.fr.agendabackend.bo.GroupMember;
import dubbelf.fr.agendabackend.bo.User;
import dubbelf.fr.agendabackend.dal.GroupMemberRepository;
import dubbelf.fr.agendabackend.dal.UserRepository;
import dubbelf.fr.agendabackend.dto.CloudflareImage;
import dubbelf.fr.agendabackend.dto.CloudflareImageListResponse;
import dubbelf.fr.agendabackend.dto.MemberRespond;
import dubbelf.fr.agendabackend.dto.UserRespond;
import dubbelf.fr.agendabackend.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Value("${cloudflare.account.id}")
    private String cloudflareAccountId;

    @Value("${cloudflare.api.token}")
    private String cloudflareApiToken;

    private final RestTemplate restTemplate = new RestTemplate();

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
        userRespond.setAdministrateur(user.isAdministrateur());
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


    @Transactional
    public UserRespond updateUser(UUID userId, String newPseudo, String newPassword, String newAvatarUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Update the fields if they are not null
        if (newPseudo != null && !newPseudo.isEmpty()) {
            user.setPseudo(newPseudo);
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword)); // Encode the new password
        }
        if (newAvatarUrl != null && !newAvatarUrl.isEmpty()) {
            String imageDeBase = user.getAvatarUrl();
            String imageId = findImageIdByUrl(imageDeBase);
            if (imageId != null) {
                deleteImageFromCloudflare(imageId);
            }
            user.setAvatarUrl(newAvatarUrl);
        }

        // Save the updated user in the database
        userRepository.save(user);

        // Prepare the response
        UserRespond userRespond = new UserRespond();
        userRespond.setId(user.getId());
        userRespond.setEmail(user.getEmail());
        userRespond.setPseudo(user.getPseudo());
        userRespond.setAvatarUrl(user.getAvatarUrl());
        userRespond.setCreatedAt(user.getCreatedAt());
        userRespond.setAdministrateur(user.isAdministrateur());

        return userRespond;
    }

    private String findImageIdByUrl(String imageUrl) {
        String url = "https://api.cloudflare.com/client/v4/accounts/" + cloudflareAccountId + "/images/v1";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + cloudflareApiToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CloudflareImageListResponse> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, CloudflareImageListResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            for (CloudflareImage image : response.getBody().getResult().getImages()) {
                if (image.getVariants().contains(imageUrl)) {
                    return image.getId();
                }
            }
        }
        return null; // return null if image not found
    }

    private void deleteImageFromCloudflare(String imageId) {
        String url = "https://api.cloudflare.com/client/v4/accounts/" + cloudflareAccountId + "/images/v1/" + imageId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + cloudflareApiToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to delete image from Cloudflare");
        }
    }
}
