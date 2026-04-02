package curse.auth.service;

import curse.auth.dto.auth.AuthResponse;
import curse.auth.dto.auth.LoginRequest;
import curse.auth.dto.auth.RegisterRequest;
import curse.auth.dto.jwt.RefreshRequestDTO;
import curse.auth.dto.jwt.RefreshResponseDTO;
import curse.auth.httpResponse.HttpResponseBody;

public interface IAuthService {
    HttpResponseBody<AuthResponse> register(RegisterRequest request);

    HttpResponseBody<AuthResponse> login(LoginRequest request);

    HttpResponseBody<RefreshResponseDTO> refresh(RefreshRequestDTO requestDTO);
}
