package cn.lyc.authentication;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        String[] authorizeUrlPrefix = (String[]) annoAttrs.get("authorizeUrlPrefix");
        if (authorizeUrlPrefix.length > 0)
            AuthenticationProperties.authorizeUrlPrefix.addAll(Arrays.stream(authorizeUrlPrefix).toList());
        String[] authorizeUrlHalf = (String[]) annoAttrs.get("authorizeUrlHalf");
        if (authorizeUrlPrefix.length > 0)
            AuthenticationProperties.authorizeUrlHalf.addAll(Arrays.stream(authorizeUrlHalf).toList());
        String[] authorizeUrlAll = (String[]) annoAttrs.get("authorizeUrlAll");
        if (authorizeUrlPrefix.length > 0)
            AuthenticationProperties.authorizeUrlAll.addAll(Arrays.stream(authorizeUrlAll).toList());
        registry.registerBeanDefinition("httpAuthenticationAdvisor",
                new RootBeanDefinition(HttpAuthenticationAdvisor.class));
        if ((boolean) annoAttrs.get("autoProcessor"))
            registry.registerBeanDefinition("authenticationProcessor", new RootBeanDefinition(AuthenticationProcessor.class));
        if ((boolean) annoAttrs.get("autoController"))
            registry.registerBeanDefinition("authenticationController", new RootBeanDefinition(AuthenticationController.class));

    }


    public static class AuthenticationProperties {
        static String rootAccount;
        static String loginUrl;
        static String logoutUrl;
        static String registerUrl;
        static String secretKey;
        static int timeout;
        static AuthLevel authLevel;
        static final Set<String> authorizeUrlPrefix = new HashSet<>();
        static final Set<String> authorizeUrlHalf = new HashSet<>();
        static final Set<String> authorizeUrlAll = new HashSet<>();
    }

}
