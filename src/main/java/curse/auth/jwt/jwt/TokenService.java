package curse.auth.jwt.jwt;

import curse.auth.dto.jwt.RefreshRequestDTO;
import curse.auth.dto.jwt.RefreshResponseDTO;
import curse.auth.mapper.JwtMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TokenService implements IJwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtMapper jwtMapper;

    private boolean isRefreshTokenValid(Jwt jwt) {
        try {
            Instant expiresAt = jwt.getExpiresAt();
            String tokenType = jwt.getClaim("tokenType");
            if (!"refresh".equals(tokenType)) {
                return false;
            }
            return expiresAt != null && !expiresAt.isBefore(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractUserCodeFromJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return "";
        }
        return jwt.getClaim("userCode");
    }

    @Override
    public List<OAuth2AccessToken> createTokens(Long userId, String login) {
        return generateNewAccessToken(userId, login);
    }

    private List<OAuth2AccessToken> generateNewAccessToken(Long userId, String login) {
        Instant now = Instant.now();
        Instant accessTokenExpiresAt = now.plusSeconds(3600);
        Instant refreshTokenExpiresAt = now.plusSeconds(86400);

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("http://localhost:9000")
                .issuedAt(now)
                .expiresAt(accessTokenExpiresAt)
                .subject(login)
                .claim("userCode", login)
                .claim("userId", userId)
                .claim("tokenType", "access")
                .build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();

        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("http://localhost:9000")
                .issuedAt(now)
                .expiresAt(refreshTokenExpiresAt)
                .subject(login)
                .claim("userCode", login)
                .claim("userId", userId)
                .claim("tokenType", "refresh")
                .build();
        String newRefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();

        final List<OAuth2AccessToken> tokens = new ArrayList<>();
        tokens.add(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, now, accessTokenExpiresAt));
        tokens.add(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, newRefreshToken, now, refreshTokenExpiresAt));
        return tokens;
    }

    @Override
    public RefreshResponseDTO refreshToken(RefreshRequestDTO refreshRequestDTO) {
        Jwt jwt = jwtDecoder.decode(refreshRequestDTO.getRefreshToken());
        if (!isRefreshTokenValid(jwt)) {
            throw new JwtException("Invalid refresh token");
        }

        Long userId = jwt.getClaim("userId");
        String login = jwt.getSubject();
        List<OAuth2AccessToken> newTokens = generateNewAccessToken(userId, login);
        return jwtMapper.mapNewAccessTokenToRefreshResponseDTO(newTokens);
    }
}
