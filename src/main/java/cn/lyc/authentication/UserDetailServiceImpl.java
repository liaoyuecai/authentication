package cn.lyc.authentication;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDetailServiceImpl implements UserDetailService {

    private Map<String, UserDetails> userDetailsMap = new ConcurrentHashMap<>();

    void addUser(String username, String password, Integer userType) {
        userDetailsMap.put(username, new UserDetails() {
            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public int getUserType() {
                return userType;
            }
        });
    }

    @Override
    public UserDetails findUserDetails(UserDetails userDetails) {
        return null;
    }

    @Override
    public Collection<String> getRoles(UserDetails userDetails) {
        return null;
    }

    @Override
    public Collection<String> getPermissionUrls(UserDetails userDetails) {
        return null;
    }
}
