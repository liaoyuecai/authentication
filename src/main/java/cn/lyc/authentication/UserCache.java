package cn.lyc.authentication;

import java.util.Collection;


public class UserCache implements UserDetails {
    private int id;
    private boolean root;
    private String account;
    private String username;
    private String password;
    private String email;
    private String phone;
    private int userType;
    private Collection<String> roles;
    private Collection<String> permissionUrls;

    public UserCache() {
    }

    public void copy(UserCache that) {
        this.id = that.id;
        this.email = that.email;
        this.account = that.account;
        this.username = that.username;
        this.phone = that.phone;
        this.roles = that.roles;
        this.permissionUrls = that.permissionUrls;
        this.root = that.root;
        this.userType = that.userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Collection<String> getPermissionUrls() {
        return permissionUrls;
    }

    public void setPermissionUrls(Collection<String> permissionUrls) {
        this.permissionUrls = permissionUrls;
    }

    @Override
    public String username() {
        return this.account;
    }

    @Override
    public String password() {
        return this.password;
    }

    @Override
    public int userType() {
        return this.userType;
    }

    @Override
    public Collection<String> roles() {
        return this.roles;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    @Override
    public Collection<String> permissionUrls() {
        return this.permissionUrls;
    }
}
