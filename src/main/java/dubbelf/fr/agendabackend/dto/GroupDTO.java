package dubbelf.fr.agendabackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class GroupDTO {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private UserRespond createdBy;
    private Set<UserRespond> users;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserRespond getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserRespond createdBy) {
        this.createdBy = createdBy;
    }

    public Set<UserRespond> getUsers() {
        return users;
    }

    public void setUsers(Set<UserRespond> users) {
        this.users = users;
    }
}
