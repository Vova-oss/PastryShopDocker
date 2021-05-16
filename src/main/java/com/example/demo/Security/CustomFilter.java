package com.example.demo.Security;

import com.example.demo.Entity.User;
import com.example.demo.Services.UserService;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomFilter extends UsernamePasswordAuthenticationFilter {

    UserService userService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {

        User user = userService.findUserByEmail(request.getParameter("username"));

        if(user!= null && user.getActivationCode() != null) {
            setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(){
                @Override
                public void onAuthenticationFailure(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    AuthenticationException exception)
                        throws IOException, ServletException {
                    super.setDefaultFailureUrl("/login?afterR");
                    super.onAuthenticationFailure(request, response, exception);
                }
            });
            throw new AuthenticationException("BadErrorGoodNotError") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }
        return super.attemptAuthentication(request,response);
    }

    public CustomFilter(String url, AuthenticationManager authenticationManager, UserService userService) {

        this.userService = userService;
        setAuthenticationManager(authenticationManager);

        setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler(){
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

                Cookie pCookie = new Cookie("password", request.getParameter("password"));
                Cookie lCookie = new Cookie("login", request.getParameter("username"));
                pCookie.setMaxAge(60*60*24*365);
                lCookie.setMaxAge(60*60*24*365);
                response.addCookie(pCookie);
                response.addCookie(lCookie);
                super.setDefaultTargetUrl("/postCookie");
                super.onAuthenticationSuccess(request, response, authentication);
            }
        });

        setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(){
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                super.setDefaultFailureUrl("/login?error");
                super.onAuthenticationFailure(request, response, exception);
            }
        });

        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(url,HttpMethod.POST.name()));
    }

}
