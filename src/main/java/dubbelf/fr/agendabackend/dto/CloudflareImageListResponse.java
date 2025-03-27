package dubbelf.fr.agendabackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudflareImageListResponse {
    @JsonProperty("result")
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("images")
        private List<CloudflareImage> images;

        public List<CloudflareImage> getImages() {
            return images;
        }

        public void setImages(List<CloudflareImage> images) {
            this.images = images;
        }
    }
}