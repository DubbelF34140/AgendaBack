package dubbelf.fr.agendabackend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserUpdate {
    private String pseudo;
    private String avatarUrl;
    private String password;

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
