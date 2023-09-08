package cn.lyc.authentication;

import cn.lyc.authentication.AuthenticationConfiguration.AuthLevel;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(AuthenticationConfiguration.class)
public @interface EnableAuthentication {
    String loginUrl() default "/auth/login";

    String rootAccount() default "admin";

    String logoutUrl() default "/auth/logout";

    String registerUrl() default "/user/register";

    String secretKey() default "";

    AuthLevel authLevel() default AuthLevel.role;

    int timeout() default 300;

    boolean autoController() default true;

    boolean autoProcessor() default true;
}
