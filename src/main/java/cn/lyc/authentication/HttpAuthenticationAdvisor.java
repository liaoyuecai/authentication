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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
public class HttpAuthenticationAdvisor {
    final Log log = LogFactory.getLog(AuthenticationConfiguration.class);

    final AuthenticationProcessor processor;

    public HttpAuthenticationAdvisor(@Autowired AuthenticationProcessor processor) {
        this.processor = processor;
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


    @Around(value = "requestPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        if ("/error".equals(uri)) {
            return joinPoint.proceed();
        }
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
        return joinPoint.proceed();
    }

}
