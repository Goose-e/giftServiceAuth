package curse.auth.exceptions;

import org.springframework.security.core.AuthenticationException;

public class CustomJwtAuthenticationException extends AuthenticationException {
    public CustomJwtAuthenticationException(String message) {
        super(message);
    }
}
