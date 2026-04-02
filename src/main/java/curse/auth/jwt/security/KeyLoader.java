package curse.auth.jwt.security;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyLoader {

    static {
        java.security.Security.addProvider(new BouncyCastleProvider());
    }

    public static RSAPrivateKey loadPrivateKey(String privateKeyPem) throws Exception {
        try {
            String privateKeyContent = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] encoded = Base64.getDecoder().decode(privateKeyContent);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке приватного ключа: " + e.getMessage());
            throw e;
        }
    }

    public static RSAPublicKey loadPublicKey(String publicKeyPem) throws Exception {
        try {
            String publicKeyContent = publicKeyPem.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] encoded = Base64.getDecoder().decode(publicKeyContent);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке публичного ключа: " + e.getMessage());
            throw e;  // Пробрасываем исключение дальше
        }
    }

}
