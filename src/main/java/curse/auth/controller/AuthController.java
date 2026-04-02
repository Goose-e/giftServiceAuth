package curse.auth.controller;

import curse.auth.dto.auth.AuthResponse;
import curse.auth.dto.auth.LoginRequest;
import curse.auth.dto.auth.RegisterRequest;
import curse.auth.dto.jwt.RefreshRequestDTO;
import curse.auth.dto.jwt.RefreshResponseDTO;
import curse.auth.httpResponse.HttpResponseBody;
import curse.auth.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/register")
    public HttpResponseBody<AuthResponse> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public HttpResponseBody<AuthResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public HttpResponseBody<RefreshResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        return authService.refresh(request);
    }
}
