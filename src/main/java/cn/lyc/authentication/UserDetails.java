package cn.lyc.authentication;

import java.util.Collection;

public interface UserDetails {
    default int getId() {
        return 0;
    }

    default String getRealName() {
        return null;
    }

    default boolean isRoot() {
        return false;
    }

    default void setRoot(boolean root) {
    }


    String getUsername();

    String getPassword();

    default int getUserType() {
        return 0;
    }

    default Collection<String> getRoles() {
        return null;
    }

    default Collection<String> getPermissionUrls() {
        return null;
    }

    default boolean equals(UserDetails details) {
        return this.getUsername().equals(details.getUsername()) &&
                this.getPassword().equals(details.getPassword()) &&
                this.getUserType() == details.getUserType();
    }

    default boolean isNotNull() {
        return getUsername() != null && getPassword() != null;
    }
}
