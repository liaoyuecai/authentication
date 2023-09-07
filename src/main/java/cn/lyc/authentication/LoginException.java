package cn.lyc.authentication;

public class LoginException extends AuthenticationException {

    public LoginException() {
        super("password error");
    }
}
