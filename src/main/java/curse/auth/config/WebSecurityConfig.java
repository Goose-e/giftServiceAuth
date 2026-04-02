package curse.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import curse.auth.jwt.CustomJwtAuthenticationConverter;
import curse.auth.jwt.security.CustomAuthenticationEntryPoint;
import curse.auth.service.AppUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static curse.auth.jwt.security.KeyLoader.loadPrivateKey;
import static curse.auth.jwt.security.KeyLoader.loadPublicKey;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${app.secretKey}")
    private String secretKey;
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint entryPoint) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(entryPoint))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/register", "/auth/login", "/auth/refresh").permitAll()
                        .requestMatchers("/.well-known/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter()))
                        .authenticationEntryPoint(entryPoint))
                .logout(LogoutConfigurer::permitAll)
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder,
                                                       AppUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        logger.warn("Using static RSA keys from configuration class");

        RSAPublicKey publicKey = loadPublicKey("-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA83ER2xKBgPqzbsA44CML\n" +
                "xS6wRBQAd5+wY/Q3QuOf2AxxNJhMbZUWapOdVxcd2iunG+pBDjnFC/8vUs9IMbTK\n" +
                "yST796ECPrB8F8QNsaVQlpiySb5XV62qhC/3taSXrynSlj69OaxBlRn3c7Sd2VDq\n" +
                "cfsCoTU+Zx7NHinTYgmDz63Q4l2arR3hz88mtb5yMYhP2kB8/ZTWI5bpFbepZYqD\n" +
                "yVMBFN3YrDYftpM+mSBi1F95K47P7UGlsFlzEbxmLeMuQlqyMPmvO57SGZC/zQLD\n" +
                "C9ZrKJ58nTK8hY9qRLtJc1X3KD23TXY4JL6E5AFlB751nbLnxMaAZBMwtOIPsVsa\n" +
                "rwIDAQAB\n" +
                "-----END PUBLIC KEY-----\n");
        RSAPrivateKey privateKey = loadPrivateKey("-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDzcRHbEoGA+rNu\n" +
                "wDjgIwvFLrBEFAB3n7Bj9DdC45/YDHE0mExtlRZqk51XFx3aK6cb6kEOOcUL/y9S\n" +
                "z0gxtMrJJPv3oQI+sHwXxA2xpVCWmLJJvldXraqEL/e1pJevKdKWPr05rEGVGfdz\n" +
                "tJ3ZUOpx+wKhNT5nHs0eKdNiCYPPrdDiXZqtHeHPzya1vnIxiE/aQHz9lNYjlukV\n" +
                "t6llioPJUwEU3disNh+2kz6ZIGLUX3krjs/tQaWwWXMRvGYt4y5CWrIw+a87ntIZ\n" +
                "kL/NAsML1msonnydMryFj2pEu0lzVfcoPbdNdjgkvoTkAWUHvnWdsufExoBkEzC0\n" +
                "4g+xWxqvAgMBAAECggEABmfgxOd5QegW5aCoPnmdmywt1jTCiGNK1G5+L8PmllxJ\n" +
                "dE1XoYCNTPOibcK9ddq1NH5XBOVPQpAN8jtjFm+/8EgJW0xAz158fXGavKRxeOz+\n" +
                "sAMSsEtxE7ZhacWxehn7IRn4h7Z5KU+8G67Sgn/nE2W7/Xr1r+f3FMab+dXvQn0u\n" +
                "wLSkE7Zprl5wJUFAEuzTrLEt/laRdjTny69WI1H0ARa534aQ9fCJbDVTYBEYNkMC\n" +
                "GIat+sb5xK7mLTSF3Ux52YjLEmz8N+8bPPwc4U9fbmCHPFIzyCNutCoVe2kgXFuF\n" +
                "ZGX9ApzHpZIVAZpyC2dS3ZuAnYC0csqP13xjgY1RNQKBgQD9cvgeWgbHfBg3jnqA\n" +
                "55pSSlWdzpEmfgu7mmouhk1WYnI7LTLdN8bYvSNCDp/qvs2vTkVa+RcivtHDj220\n" +
                "LubRZxjf8wDpleVhUx+xYx1yTkypBGfFx6xuqjlKBRBtxXj1q2FsDaem9kJyXKga\n" +
                "vcNPKJ4nKU/N0ErSGmLj3sefUwKBgQD15FDPLkbFTFlCxpEDTJ3QNU+kGS7qGD12\n" +
                "4E1umgxdHzefDFgAWk7ckwuMZ2Xt+VTfFmYbTA3tp5PhE0/yYAVSY6kNN816UlJ3\n" +
                "OBnv2DrcPsTTA0RuMSj0B+cQg5EP3H5oJu0oDUqOwR49xiZZrQQXZmd6wudNofVB\n" +
                "bRyHM50XtQKBgQDTyH0yh5HAoZ5u1hzhmCZo+vp96j16B7twIUD27Xy0YYEIl+U3\n" +
                "20LKm4lZynSwqI2hHmtv8FL30rHfkvtdpA92DYtIn3s0tO++msIR7TuO54TBew/n\n" +
                "wckBlDZY9tBambdQucunieV866xseaZy6fzJKay2Fpw75ACRfkECHod6RwKBgDBr\n" +
                "QgJaAXOOYjFE+n8k0hqStNkCYHuWRFwpNPYnfXHE2OYHvzhqkAbdKbP/FFI0+6Jm\n" +
                "Pbr4UfHPQs5aSC7Y+5NKrpo8t8hzd2ukb28MLCL8dTN7/Diak47iE3zr+1+dMoL9\n" +
                "z0tIurq7imHtp9WWvSyCbLP+tx9RWgMFIk35OuDVAoGBAItb2We+FoseehnWuCiT\n" +
                "PRLT2jhLkDwYdPayJwFm1VfM8Oat2ZpZX339SvGX5bsz4awZtPfxsaT7nuHZKFDh\n" +
                "i62QJHtEBkiqJ1v2AMockCvSnNt5CWDYBcHx9p51XHrOgiS3y1KbETddmDYhFAkf\n" +
                "SJfVzsUI4mmUP5SfZH1izDE5\n" +
                "-----END PRIVATE KEY-----");

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(secretKey)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().issuer("http://localhost:9000").build();
    }
}
