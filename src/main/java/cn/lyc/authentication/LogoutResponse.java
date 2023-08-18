package cn.lyc.authentication;


public class LogoutResponse {
    private boolean result;

    public LogoutResponse(boolean result) {
        this.result = result;
    }

    public boolean result() {
        return result;
    }

    public LogoutResponse setResult(boolean result) {
        this.result = result;
        return this;
    }
}
