package curse.auth.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String login;
    private String password;
}
