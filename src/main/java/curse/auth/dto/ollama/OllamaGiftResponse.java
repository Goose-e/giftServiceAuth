package curse.auth.dto.ollama;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OllamaGiftResponse {
    private List<String> tags;
    private List<GiftIdea> ideas;
    private String reason;

    @Getter
    @Setter
    public static class GiftIdea {
        private String name;
        private Long price;
    }
}