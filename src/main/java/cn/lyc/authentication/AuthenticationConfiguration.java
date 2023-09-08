package cn.lyc.authentication;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AuthenticationConfiguration implements ImportBeanDefinitionRegistrar {

    public enum AuthLevel {
        none, role, url, roleAndUrl
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(
                        EnableAuthentication.class.getName()));
        AuthenticationProperties.loginUrl = (String) annoAttrs.get("loginUrl");
        AuthenticationProperties.logoutUrl = (String) annoAttrs.get("logoutUrl");
        AuthenticationProperties.rootAccount = (String) annoAttrs.get("rootAccount");
        AuthenticationProperties.registerUrl = (String) annoAttrs.get("registerUrl");
        AuthenticationProperties.secretKey = (String) annoAttrs.get("secretKey");
        AuthenticationProperties.timeout = (int) annoAttrs.get("timeout");
        AuthenticationProperties.authLevel = (AuthLevel) annoAttrs.get("authLevel");

        registry.registerBeanDefinition("httpAuthenticationAdvisor",
                new RootBeanDefinition(HttpAuthenticationAdvisor.class));
        if ((boolean) annoAttrs.get("autoProcessor"))
            registry.registerBeanDefinition("authenticationProcessor", new RootBeanDefinition(AuthenticationProcessor.class));
        if ((boolean) annoAttrs.get("autoController"))
            registry.registerBeanDefinition("authenticationController", new RootBeanDefinition(AuthenticationController.class));

    }


    public static class AuthenticationProperties {
        public static String rootAccount;
        public static String loginUrl;
        public static String logoutUrl;
        public static String registerUrl;
        public static String secretKey;
        public static int timeout;
        public static AuthLevel authLevel;
    }

}
