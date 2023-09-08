package cn.lyc.authentication;


import java.util.Collection;

public interface UserDetailService {
    UserDetails findUserDetails(UserDetails userDetails);

    Collection<String> getRoles(UserDetails userDetails);

    Collection<String> getPermissionUrls(UserDetails userDetails);
}
