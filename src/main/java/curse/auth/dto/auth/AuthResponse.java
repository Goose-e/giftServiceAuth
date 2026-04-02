package curse.auth.dto.auth;

import curse.auth.httpResponse.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse implements ResponseDto {
    private Long userId;
    private String username;
    private String login;
    private String accessToken;
    private long accessExpiresIn;
    private String refreshToken;
    private long refreshExpiresIn;
    private String tokenType;
}
