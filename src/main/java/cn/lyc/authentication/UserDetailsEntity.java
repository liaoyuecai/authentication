package cn.lyc.authentication;

import java.util.Collection;


public class UserDetailsEntity implements UserDetails {
    private int id;
    private boolean root;
    private String realName;
    private String username;
    private String email;
    private String phone;
    private String password;
    private int userType;
    private Collection<String> roles;
    private Collection<String> permissionUrls;

    public UserDetailsEntity() {
    }

    public UserDetailsEntity(UserDetails details) {
        this.id = details.getId();
        this.username = details.getUsername();
        this.userType = details.getUserType();
        this.password = details.getPassword();
    }

    public void copy(UserDetailsEntity that) {
        this.id = that.id;
        this.email = that.email;
        this.realName = that.realName;
        this.username = that.username;
        this.phone = that.phone;
        this.roles = that.roles;
        this.permissionUrls = that.permissionUrls;
        this.root = that.root;
        this.password = that.password;
        this.userType = that.userType;
    }

    @Override
    public boolean isRoot() {
        return root;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
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

    @Override
    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<String> getPermissionUrls() {
        return permissionUrls;
    }

    public void setPermissionUrls(Collection<String> permissionUrls) {
        this.permissionUrls = permissionUrls;
    }
}
