package cn.lyc.authentication;


import cn.lyc.authentication.AuthenticationConfiguration.AuthenticationProperties;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;


public class AuthenticationProcessor {
    public enum UrlType {
        login, logout, register, noAuth, auth
    }

    final Log log = LogFactory.getLog(AuthenticationProcessor.class);


    private final UserDetailService userService;

    public AuthenticationProcessor(
            @Autowired UserDetailService userService) {

        this.userService = userService;
        if (!StringUtils.hasText(AuthenticationProperties.secretKey)) {
            StringBuilder str = new StringBuilder();
            Random random = new Random();
            while (str.length() < 64) {
                int nextInt = random.nextInt(36);
                if (nextInt < 10) {
                    str.append(nextInt);
                } else {
                    str.append((nextInt - 10) + 'a');
                }
            }
            this.jwtService = new JwtService(new SecretKeySpec(str.toString().getBytes(), SignatureAlgorithm.HS256.getJcaName()));
            log.warn("jwt secretKey not set, the random secretKey will be used");
        } else {
            this.jwtService = new JwtService(new SecretKeySpec(AuthenticationProperties.secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName()));
        }
    }

    private JwtService jwtService;


    public AuthenticationProcessor addUser(String username, String password) {
        if (userService instanceof UserDetailServiceImpl)
            ((UserDetailServiceImpl) userService).addUser(username, password, 0);
        return this;
    }

    public AuthenticationProcessor addUser(String username, String password, Integer userType) {
        if (userService instanceof UserDetailServiceImpl)
            ((UserDetailServiceImpl) userService).addUser(username, password, userType);
        return this;
    }


    public Object login(UserDetails userDetails) {
        if (userDetails.isNotNull()) {
            UserDetails user = userService.findUserDetails(userDetails);
            if (user != null && user.equals(userDetails)) {
                user.setRoles(userService.getRoles(user));
                user.setPermissionUrls(userService.getPermissionUrls(user));
                String token = buildToken(user);
                return new LoginResponse(token);
            }
        }
        throw new LoginException();
    }


    private UserDetails checkUserDetails(UserDetails userDetails) {
        return userService.findUserDetails(userDetails);
    }

    protected String buildToken(UserDetails user) {
        return jwtService.createToken(user);
    }

    boolean register(UserDetails user) {
        return true;
    }

    protected Object logout(String token) {
        return null;
    }

    public UserDetails auth(AuthenticationToken token) {
        if (StringUtils.hasText(token.token())) {
            UserDetails details;
            try {
                details = getUserDetails(token.token());
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
                if (token.authentication() != null && !token.authentication().isRouteAuth())
                    return details;
                boolean flag = switch (AuthenticationProperties.authLevel) {
                    case none -> true;
                    case role ->
                            token.authentication() != null && authRole(details.getRoles(), Arrays.asList(token.authentication().roles()));
                    case url -> authUrl(details.getPermissionUrls(), token.url());
                    case roleAndUrl -> token.authentication() != null &&
                            authRole(details.getRoles(), Arrays.asList(token.authentication().roles()))
                            && authUrl(details.getPermissionUrls(), token.url());
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
            AuthenticationProperties.authorizeUrlPrefix.add(url.replace("/**", ""));
        } else if (url.equals("/*")) {
            AuthenticationProperties.authorizeUrlHalf.add(url.replace("/*", ""));
        } else {
            AuthenticationProperties.authorizeUrlAll.add(url);
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
            return UrlType.auth;
        } else {
            return UrlType.noAuth;
        }
    }


    /**
     * 校验url是否无需校验
     *
     * @param url
     * @return
     */
    boolean checkUrl(String url) {
        if (AuthenticationProperties.authorizeUrlAll.contains(url)) {
            return false;
        }
        for (String str : AuthenticationProperties.authorizeUrlHalf) {
            str = str.replace("*", "");
            url = url.replace(str, "");
            if (!url.contains("/")) {
                return false;
            }
        }
        for (String str : AuthenticationProperties.authorizeUrlPrefix) {
            str = str.replace("**", "");
            if (url.contains(str)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取用户登录后的缓存
     *
     * @param token
     * @return
     */
    protected UserDetails getUserDetails(String token) {
        return jwtService.parserToken(token);
    }


}