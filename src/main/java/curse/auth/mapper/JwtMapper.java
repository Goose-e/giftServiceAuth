package curse.auth.mapper;

import curse.auth.dto.jwt.RefreshResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class JwtMapper {
    public RefreshResponseDTO mapNewAccessTokenToRefreshResponseDTO(List<OAuth2AccessToken> newAccessToken) {
        long timeAccess = newAccessToken.getFirst().getExpiresAt().getEpochSecond() - newAccessToken.getFirst().getIssuedAt().getEpochSecond();
        long timeRefresh =  newAccessToken.getLast().getExpiresAt().getEpochSecond() - newAccessToken.getLast().getIssuedAt().getEpochSecond();

        return new RefreshResponseDTO(newAccessToken.getFirst().getTokenValue(),timeAccess, newAccessToken.get(1).getTokenValue(),timeRefresh);
    }
}
