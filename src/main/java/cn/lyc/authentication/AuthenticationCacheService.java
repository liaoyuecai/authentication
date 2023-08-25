package cn.lyc.authentication;


public interface AuthenticationCacheService {
    UserDetailEntity getUserDetails(String key);

    void setUserDetails(String key, UserDetails details, long second);

    Object removeUserDetails(String key);
}
