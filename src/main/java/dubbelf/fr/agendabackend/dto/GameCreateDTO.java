package dubbelf.fr.agendabackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateDTO {
    private String name;
    private String description;
    private List<GameSettingDTO> settings;

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

    public List<GameSettingDTO> getSettings() {
        return settings;
    }

    public void setSettings(List<GameSettingDTO> settings) {
        this.settings = settings;
    }
}
