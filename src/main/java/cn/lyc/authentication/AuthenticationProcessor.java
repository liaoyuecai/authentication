package cn.lyc.authentication;


import cn.lyc.authentication.AuthenticationConfiguration.AuthenticationProperties;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationProcessor {
    public enum UrlType {
        login, logout, register, noAuth, auth
    }

    @Autowired
    private ApplicationContext context;

    private JwtService jwtService;

    public AuthenticationProcessor initJwtService(SecretKey secretKey) {
        jwtService = new JwtService(secretKey);
        return this;
    }

    private Map<String, UserDetails> userDetailsMap = new ConcurrentHashMap<>();

    private AuthenticationCacheService cacheService;

    public AuthenticationProcessor cacheService(AuthenticationCacheService cacheService) {
        this.cacheService = cacheService;
        return this;
    }

    public AuthenticationProcessor addUser(String username, String password) {
        userDetailsMap.put(username, new UserDetails() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }

        });
        return this;
    }

    public AuthenticationProcessor addUser(String username, String password, Integer userType) {
        userDetailsMap.put(username, new UserDetails() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public int getUserType() {
                return userType;
            }
        });
        return this;
    }


    private final Set<String> authorizeUrlPrefix = new HashSet<>();
    private final Set<String> authorizeUrlHalf = new HashSet<>();
    private final Set<String> authorizeUrlAll = new HashSet<>();


    public Object login(UserDetails userDetails) {
        if (userDetails.isNotNull()) {
            UserDetails user = checkUserDetails(userDetails);
            if (user != null) {
                String token = buildToken(user);
                if (cacheService != null)
                    cacheService.setUserDetails(token, user, AuthenticationProperties.timeout);
                LoginResponse re = new LoginResponse(token);
                return re;
            }
        }
        throw new LoginException();
    }

    private UserDetails checkUserDetails(UserDetails userDetails) {
        try {
            UserDetailService userService = context.getBean(UserDetailService.class);
            return userService.findUserDetails(userDetails);
        } catch (Exception e) {
            UserDetails user = userDetailsMap.get(userDetails.getUsername());
            if (user != null && user.equals(userDetails)) {
                return new UserDetailsEntity(user);
            }
            return null;
        }
    }

    protected String buildToken(UserDetails user) {
        if (cacheService != null) return UUID.randomUUID().toString();
        return jwtService.createToken(user);
    }

    public boolean register(UserDetails user) {
        return true;
    }

    public Object logout(String token) {
        if (token != null && cacheService != null) return cacheService.removeUserDetails(token);
        return null;
    }

    public UserDetails auth(AuthenticationToken token) {
        if (StringUtils.hasText(token.getToken())) {
            UserDetails details;
            try {
                details = getUserDetails(token.getToken());
            } catch (JwtException e) {
                details = null;
            }
            if (details != null) {
                if (AuthenticationProperties.rootAccount != null &&
                        AuthenticationProperties.rootAccount.equals(details.getUsername())) {
                    details.setRoot(true);
                    return details;
                } else {
                    details.setRoot(false);
                }
                if (token.getAuthentication() != null && !token.getAuthentication().isRouteAuth())
                    return details;
                boolean flag = switch (AuthenticationProperties.authLevel) {
                    case none -> true;
                    case role ->
                            token.getAuthentication() != null && authRole(details.getRoles(), Arrays.asList(token.getAuthentication().roles()));
                    case url -> authUrl(details.getPermissionUrls(), token.getUrl());
                    case roleAndUrl -> token.getAuthentication() != null &&
                            authRole(details.getRoles(), Arrays.asList(token.getAuthentication().roles()))
                            && authUrl(details.getPermissionUrls(), token.getUrl());
                };
                if (flag) {
                    return details;
                }
            }
        }
        throw new AuthException();
    }


    boolean authRole(Collection<String> userRoles, Collection<String> authRoles) {
        if (userRoles == null || userRoles.isEmpty() || authRoles == null || authRoles.isEmpty())
            return false;
        for (String role : userRoles) {
            if (authRoles.contains(role))
                return true;
        }
        return false;
    }

    boolean authUrl(Collection<String> userUrls, String url) {
        if (userUrls != null) {
            for (String str : userUrls) {
                if (str.endsWith("/**")) {
                    str = str.replace("**", "");
                    if (url.contains(str)) {
                        return true;
                    }
                } else if (str.endsWith("/*")) {
                    str = str.replace("*", "");
                    url = url.replace(str, "");
                    if (!url.contains("/")) {
                        return true;
                    }
                } else {
                    return str.equals(url);
                }
            }
        }
        return false;
    }


    /**
     * 不需要认证url
     *
     * @return
     */
    public AuthenticationProcessor addAuthorizeUrl(String url) {
        if (url.equals("/**")) {
            authorizeUrlPrefix.add(url.replace("/**", ""));
        } else if (url.equals("/*")) {
            authorizeUrlHalf.add(url.replace("/*", ""));
        } else {
            authorizeUrlAll.add(url);
        }
        return this;
    }

    public UrlType uriType(String url) {
        if (AuthenticationProperties.loginUrl.equals(url)) {
            return UrlType.login;
        } else if (AuthenticationProperties.logoutUrl.equals(url)) {
            return UrlType.logout;
        } else if (AuthenticationProperties.registerUrl != null && AuthenticationProperties.registerUrl.equals(url)) {
            return UrlType.register;
        } else if (checkUrl(url)) {
            return UrlType.noAuth;
        } else {
            return UrlType.auth;
        }
    }


    /**
     * 校验url是否无需校验
     *
     * @param url
     * @return
     */
    boolean checkUrl(String url) {
        if (authorizeUrlAll.contains(url)) {
            return true;
        }
        for (String str : authorizeUrlHalf) {
            str = str.replace("*", "");
            url = url.replace(str, "");
            if (!url.contains("/")) {
                return true;
            }
        }
        for (String str : authorizeUrlPrefix) {
            str = str.replace("**", "");
            if (url.contains(str)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取用户登录后的缓存
     *
     * @param token
     * @return
     */
    protected UserDetails getUserDetails(String token) {
        if (cacheService == null) return jwtService.parserToken(token);
        return cacheService.getUserDetails(token);
    }


}