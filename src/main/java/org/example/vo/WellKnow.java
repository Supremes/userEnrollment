package org.example.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WellKnow {
    @JsonProperty("Servers")
    private List<AvailableServer> servers;

    @Data
    @Builder
    public static class AvailableServer {
        @JsonProperty("BaseURL")
        private String baseURL;
        @JsonProperty("Version")
        private String version;
    }
}
