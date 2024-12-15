package itcast.blog.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class VelogHttpClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public VelogHttpClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("accept", "*/*")
                .defaultHeader("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36")
                .defaultHeader("content-type", "application/json")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String fetchTrendingPosts(String query, String variables) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("query", query);
            payload.put("variables", objectMapper.readValue(variables, Map.class));

            String body = objectMapper.writeValueAsString(payload);

            return webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        log.error("Error Status: {}", clientResponse.statusCode());
                        return clientResponse.createException().flatMap(Mono::error);
                    })
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error response: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to fetch trending posts", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }
}

