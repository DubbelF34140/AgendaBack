package dubbelf.fr.agendabackend.bo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "player_event_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlayerEventSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String key; // Ex : "team1Players", "map", "serverIp"

    @Column(nullable = false, length = 1000)
    private String value; // Valeur spécifique pour cet événement (ex : "5 joueurs équipe 1")

    @ManyToOne
    @JoinColumn(name = "event_participant_id", nullable = true)
    private EventParticipant eventParticipant;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EventParticipant getEventParticipant() {
        return eventParticipant;
    }

    public void setEventParticipant(EventParticipant eventParticipant) {
        this.eventParticipant = eventParticipant;
    }
}