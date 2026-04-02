package curse.auth.dto.jwt;

import curse.auth.httpResponse.ResponseDto;
import lombok.Data;

@Data
public class RefreshResponseDTO implements ResponseDto {

    private String refreshToken;
    private String accessToken;
    private long accessExpiresIn;
    private long refreshExpiresIn;
    private String tokenType;

    public RefreshResponseDTO(String accessToken, long accessExpiresIn, String refreshToken, long refreshExpiresIn) {
        this.accessToken = accessToken;
        this.accessExpiresIn = accessExpiresIn;
        this.refreshToken = refreshToken;
        this.refreshExpiresIn = refreshExpiresIn;
        this.tokenType = "Bearer";
    }
}
