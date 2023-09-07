package cn.lyc.authentication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * write auth message to request
 * http request class need extends cn.lyc.authentication.AuthHttpRequest
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadAuthMessage {
}
