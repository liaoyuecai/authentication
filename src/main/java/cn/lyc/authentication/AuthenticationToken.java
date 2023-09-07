package cn.lyc.authentication;


public class AuthenticationToken {

    final String token;
    final String url;
    final Authentication authentication;

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
