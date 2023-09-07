package cn.lyc.authentication;

import cn.lyc.authentication.AuthenticationConfiguration.AuthenticationProperties;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.*;

public class JwtService {
    private final static String REAL_NAME_KEY = "realName";
    private final static String USERNAME_TYPE_KEY = "userType";
    private final static String USERNAME_KEY = "username";
    private final static String ROLES_KEY = "roles";
    private final static String URLS_KEY = "urls";
    final SecretKey secretKey;

    public JwtService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * jwt说明
     */
    private String subject;
    /**
     * 签发者信息
     */
    private String issuer;

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String createToken(UserDetails details) {
        long time = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(time))
                .compressWith(CompressionCodecs.GZIP)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setExpiration(new Date(time + AuthenticationProperties.timeout * 1000));
        if (issuer != null) builder.setIssuer(issuer);
        if (subject != null) builder.setSubject(subject);
        Map<String, Object> claimMaps = new HashMap<>();
        claimMaps.put(REAL_NAME_KEY, details.getRealName());
        claimMaps.put(USERNAME_KEY, details.getUsername());
        claimMaps.put(USERNAME_TYPE_KEY, details.getUserType());
        if (details.getRoles() != null) claimMaps.put(ROLES_KEY, details.getRoles());
        if (details.getPermissionUrls() != null) claimMaps.put(URLS_KEY, details.getPermissionUrls());
        return builder.addClaims(claimMaps).compact();
    }

    public UserDetails parserToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        UserDetailsEntity entity = new UserDetailsEntity();
        entity.setUsername(claims.get(USERNAME_KEY, String.class));
        entity.setRealName(claims.get(REAL_NAME_KEY, String.class));
        entity.setRoles(claims.get(ROLES_KEY, Collection.class));
        entity.setUserType(claims.get(USERNAME_TYPE_KEY, Integer.class));
        entity.setPermissionUrls(claims.get(ROLES_KEY, Collection.class));
        return entity;
    }
}
