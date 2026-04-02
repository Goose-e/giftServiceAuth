package curse.auth.service;

import curse.auth.dto.auth.AuthResponse;
import curse.auth.dto.auth.LoginRequest;
import curse.auth.dto.auth.RegisterRequest;
import curse.auth.dto.jwt.RefreshRequestDTO;
import curse.auth.dto.jwt.RefreshResponseDTO;
import curse.auth.jwt.jwt.IJwtService;
import curse.auth.models.User;
import curse.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new IllegalArgumentException("Login already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);
        List<OAuth2AccessToken> tokens = jwtService.createTokens(saved.getUserId(), saved.getLogin());

        return new AuthResponse(
                saved.getUserId(),
                saved.getUsername(),
                saved.getLogin(),
                tokens.getFirst().getTokenValue(),
                tokens.getFirst().getExpiresAt().getEpochSecond() - tokens.getFirst().getIssuedAt().getEpochSecond(),
                tokens.getLast().getTokenValue(),
                tokens.getLast().getExpiresAt().getEpochSecond() - tokens.getLast().getIssuedAt().getEpochSecond(),
                "Bearer"
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<OAuth2AccessToken> tokens = jwtService.createTokens(user.getUserId(), user.getLogin());

        return new AuthResponse(
                user.getUserId(),
                user.getUsername(),
                user.getLogin(),
                tokens.getFirst().getTokenValue(),
                tokens.getFirst().getExpiresAt().getEpochSecond() - tokens.getFirst().getIssuedAt().getEpochSecond(),
                tokens.getLast().getTokenValue(),
                tokens.getLast().getExpiresAt().getEpochSecond() - tokens.getLast().getIssuedAt().getEpochSecond(),
                "Bearer"
        );
    }

    public RefreshResponseDTO refresh(RefreshRequestDTO requestDTO) {
        return jwtService.refreshToken(requestDTO);
    }
}
