package cn.lyc.authentication;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("auth")
public class AuthenticationController {

    @PostMapping("login")
    @ResponseBody
    public Object login(@RequestBody LoginRequest request) {
        return new LoginResponse("");
    }

    @PostMapping("logout")
    @ResponseBody
    public Object logout() {
        return new LogoutResponse(true);
    }

}
