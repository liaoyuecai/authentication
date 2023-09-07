package cn.lyc.authentication;

import java.io.Serializable;
import java.util.Collection;


public class AuthHttpRequest implements Serializable {
    protected Integer userId;
    protected boolean root;
    protected int userType;
    protected Collection<String> roles;

    void loadAuthMessage(UserDetails details) {
        this.userId = details.getId();
        this.userType = details.getUserType();
        this.root = details.isRoot();
        this.roles = details.getRoles();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

}
