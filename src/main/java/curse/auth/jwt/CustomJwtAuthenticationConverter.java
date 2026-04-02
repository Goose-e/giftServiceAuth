package curse.auth.jwt;

import curse.auth.exceptions.CustomJwtAuthenticationException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken>  {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String tokenType = jwt.getClaimAsString("tokenType");

        if ("refresh".equals(tokenType)) {
            throw new CustomJwtAuthenticationException("No No No refresh token, u need to use iobany access token");
        }

        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {

        Object roleClaim = jwt.getClaim("role");

        if (roleClaim instanceof String role) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        }

        if (roleClaim instanceof Collection<?> roles) {
            return roles.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }

        return List.of();
    }

}
