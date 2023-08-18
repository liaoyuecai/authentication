package cn.lyc.authentication;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.util.StringUtils;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthenticationInit {
    final Log log = LogFactory.getLog(AuthenticationConfiguration.class);

    @Bean(name = "authenticationProcessor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AuthenticationProcessor securityConfigProcessor(@Autowired ApplicationContext context) throws NoSuchAlgorithmException {
        try {
            context.getBean(AuthenticationProcessor.class);
            return null;
        } catch (BeanCreationException e) {
            AuthenticationProcessor processor = new AuthenticationProcessor();
            if (context.getBeanNamesForType(AuthenticationCacheService.class).length > 0) {
                processor.cacheService(context.getBean(AuthenticationCacheService.class));
            } else {
                String secretKey = AuthenticationConfiguration.AuthenticationProperties.secretKey;
                if (!StringUtils.hasText(AuthenticationConfiguration.AuthenticationProperties.secretKey)) {
                    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                    keyGen.init(256, new SecureRandom());
                    secretKey = Base64.getEncoder().encodeToString(
                            keyGen.generateKey().getEncoded());
                    log.warn("jwt secretKey not set, the random secretKey will be used");
                }
                processor.initJwtService(secretKey);
            }
            return processor;
        }
    }

    @Bean(name = "authenticationController")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AuthenticationController securityConfigController(@Autowired ApplicationContext context) {
        try {
            context.getBean(AuthenticationController.class);
            return null;
        } catch (BeanCreationException e) {
            return new AuthenticationController();
        }
    }

    @Bean(name = "httpAuthenticationAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public HttpAuthenticationAdvisor securityConfigAdvisor(@Autowired ApplicationContext context) {
        try {
            context.getBean(HttpAuthenticationAdvisor.class);
            return null;
        } catch (BeanCreationException e) {
            return new HttpAuthenticationAdvisor(context);
        }
    }


}
