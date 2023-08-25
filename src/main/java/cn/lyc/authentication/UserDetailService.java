package cn.lyc.authentication;


public interface UserDetailService {
    UserDetailEntity getUser(UserDetails userDetails);
}
