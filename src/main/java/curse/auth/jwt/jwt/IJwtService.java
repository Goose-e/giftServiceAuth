package curse.auth.jwt.jwt;

import curse.auth.dto.jwt.RefreshRequestDTO;
import curse.auth.dto.jwt.RefreshResponseDTO;
import curse.auth.httpResponse.HttpResponseBody;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.List;

public interface IJwtService {
    String extractUserCodeFromJwt();

    List<OAuth2AccessToken> createTokens(Long userId, String login);

    HttpResponseBody<RefreshResponseDTO> refreshToken(RefreshRequestDTO refreshRequestDTO);
}
