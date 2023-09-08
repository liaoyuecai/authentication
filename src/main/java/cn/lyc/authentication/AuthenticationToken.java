package cn.lyc.authentication;


public record AuthenticationToken(String token, String url, Authentication authentication) {

}
