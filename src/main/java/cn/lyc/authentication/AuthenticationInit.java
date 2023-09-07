package cn.lyc.authentication;


import cn.lyc.authentication.AuthenticationConfiguration.AuthenticationProperties;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

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
            try {
                processor.cacheService(context.getBean(AuthenticationCacheService.class));
            } catch (BeansException be) {
                if (!StringUtils.hasText(AuthenticationProperties.secretKey)) {
                    StringBuilder str = new StringBuilder();
                    Random random = new Random();
                    while (str.length() < 64) {
                        int nextInt = random.nextInt(36);
                        if (nextInt < 10) {
                            str.append(nextInt);
                        } else {
                            str.append((nextInt - 10) + 'a');
                        }
                    }
                    processor.initJwtService(new SecretKeySpec(str.toString().getBytes(), SignatureAlgorithm.HS256.getJcaName()));
                    log.warn("jwt secretKey not set, the random secretKey will be used");
                } else {
                    processor.initJwtService(new SecretKeySpec(AuthenticationProperties.secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName()));
                }

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
