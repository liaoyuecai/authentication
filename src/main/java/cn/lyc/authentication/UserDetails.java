package cn.lyc.authentication;

import java.util.Collection;

public interface UserDetails {
    String username();

    String password();

    default int userType() {
        return 1;
    }

    default Collection<String> roles() {
        return null;
    }

    default Collection<String> permissionUrls() {
        return null;
    }

    default boolean equals(UserDetails details) {
        return this.username().equals(details.username()) &&
                this.password().equals(details.password()) &&
                this.userType() == details.userType();
    }

    default boolean isNotNull() {
        return username() != null && password() != null;
    }
}
