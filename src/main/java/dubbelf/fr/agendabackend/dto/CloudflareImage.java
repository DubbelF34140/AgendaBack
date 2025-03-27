package dubbelf.fr.agendabackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudflareImage {
    @JsonProperty("id")
    private String id;

    @JsonProperty("variants")
    private List<String> variants;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getVariants() {
        return variants;
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
    }
}

