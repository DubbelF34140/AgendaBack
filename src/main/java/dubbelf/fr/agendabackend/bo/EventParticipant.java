package dubbelf.fr.agendabackend.bo;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "event_participants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String team;

    @OneToMany(mappedBy = "eventParticipant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Column(nullable = true)
    private List<PlayerEventSetting> playereventSettings;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public List<PlayerEventSetting> getPlayereventSettings() {
        return playereventSettings;
    }

    public void setPlayereventSettings(List<PlayerEventSetting> playereventSettings) {
        this.playereventSettings = playereventSettings;
    }
}
