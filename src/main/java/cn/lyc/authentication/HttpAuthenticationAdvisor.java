package cn.lyc.authentication;


import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class HttpAuthenticationAdvisor {
    final Log log = LogFactory.getLog(AuthenticationConfiguration.class);

    public HttpAuthenticationAdvisor(@Autowired ApplicationContext context) {
        this.context = context;
    }

    @Pointcut("""
                @annotation(org.springframework.web.bind.annotation.RequestMapping)
                ||@annotation(org.springframework.web.bind.annotation.PostMapping)
                ||@annotation(org.springframework.web.bind.annotation.GetMapping)
                ||@annotation(org.springframework.web.bind.annotation.PutMapping)
                ||@annotation(org.springframework.web.bind.annotation.DeleteMapping)
            """)
    public void requestPointcut() {
    }

    private final ApplicationContext context;

    @Around(value = "requestPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (context.getBeanNamesForType(AuthenticationProcessor.class).length > 0) {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes()).getRequest();
            String uri = request.getRequestURI();
            if ("/error".equals(uri)){
                return joinPoint.proceed();
            }
            AuthenticationProcessor processor = context.getBean(AuthenticationProcessor.class);
            AuthenticationProcessor.UrlType type = processor.uriType(uri);
            Object[] args = joinPoint.getArgs();
            MethodSignature invocation = (MethodSignature) joinPoint.getSignature();
            switch (type) {
                case register -> processor.register((UserDetails) args[0]);
                case login -> {
                    return processor.login((UserDetails) args[0]);
                }
                case auth -> {
                    LoadAuthMessage loadAuthMessage =
                            invocation.getMethod().getAnnotation(LoadAuthMessage.class);
                    Authentication authentication =
                            invocation.getMethod().getAnnotation(Authentication.class);
                    if (authentication == null)
                        authentication =
                                invocation.getMethod().getDeclaringClass().getAnnotation(Authentication.class);
                    UserDetails details = processor.auth(new AuthenticationToken(
                            request.getHeader("accessToken"), uri, authentication));
                    if (loadAuthMessage != null) {
                        if (args.length < 1)
                            log.warn("there id no param to receive auth message");
                        else {
                            if (args[0] instanceof AuthHttpRequest)
                                ((AuthHttpRequest) args[0]).loadAuthMessage(details);
                            else log.warn("http request class need extends cn.lyc.authentication.AuthHttpRequest");
                        }
                    }
                }
                case logout -> processor.logout(request.getHeader("accessToken"));
            }
        }
        return joinPoint.proceed();
    }

}
