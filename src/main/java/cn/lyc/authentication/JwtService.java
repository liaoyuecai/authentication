package cn.lyc.authentication;

import cn.lyc.authentication.AuthenticationConfiguration.AuthenticationProperties;
import io.jsonwebtoken.*;

import java.util.*;

public class JwtService {
    private final static String USERNAME_KEY = "user";
    private final static String USERNAME_TYPE_KEY = "userType";
    private final static String ACCOUNT_KEY = "account";
    private final static String ROLES_KEY = "roles";
    private final static String URLS_KEY = "urls";

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

    public String createToken(UserCache cache) {
        long time = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(time))
                .compressWith(CompressionCodecs.GZIP)
                .signWith(SignatureAlgorithm.HS512, AuthenticationProperties.secretKey)
                .setExpiration(new Date(time + AuthenticationProperties.timeout * 1000));
        if (issuer != null) builder.setIssuer(issuer);
        if (subject != null) builder.setSubject(subject);
        Map<String, Object> claimMaps = new HashMap<>();
        claimMaps.put(USERNAME_KEY, cache.username());
        claimMaps.put(ACCOUNT_KEY, cache.getAccount());
        claimMaps.put(USERNAME_TYPE_KEY, cache.userType());
        claimMaps.put(ROLES_KEY, cache.roles());
        claimMaps.put(URLS_KEY, cache.permissionUrls());
        return builder.addClaims(claimMaps).compact();
    }

    public UserCache parserToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(AuthenticationProperties.secretKey)
                .parseClaimsJws(token)
                .getBody();
        UserCache cache = new UserCache();
        cache.setAccount(claims.get(ACCOUNT_KEY, String.class));
        cache.setUsername(claims.get(USERNAME_KEY, String.class));
        cache.setRoles(claims.get(ROLES_KEY, Collection.class));
        cache.setUserType(claims.get(USERNAME_TYPE_KEY, Integer.class));
        cache.setPermissionUrls(claims.get(ROLES_KEY, Collection.class));
        return cache;
    }
}
