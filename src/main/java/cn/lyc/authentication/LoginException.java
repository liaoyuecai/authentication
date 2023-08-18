package cn.lyc.authentication;

public class LoginException extends RuntimeException {

    public LoginException() {
        super("password error");
    }
}
