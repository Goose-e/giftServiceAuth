package curse.auth.service;

import curse.auth.dto.ollama.OllamaGiftResponse;
import curse.auth.models.Victim;

public interface IOllamaService {
    public OllamaGiftResponse analyzeVictim(Victim victim);
}
