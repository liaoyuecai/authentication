package cn.lyc.authentication;

public class AuthException extends AuthenticationException {

    public AuthException() {
        super("token error");
    }
}
