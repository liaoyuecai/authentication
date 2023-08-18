package cn.lyc.authentication;


public class AuthenticationToken {

    String token;
    String url;
    Authentication authentication;

    public AuthenticationToken(String token, String url, Authentication authentication) {
        this.token = token;
        this.url = url;
        this.authentication = authentication;
    }

    public String getToken() {
        return token;
    }

    public String getUrl() {
        return url;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

}
