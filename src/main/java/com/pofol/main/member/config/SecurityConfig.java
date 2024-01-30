package com.pofol.main.member.config;

import com.pofol.main.member.handler.CustomLogoutSuccessHandler;
import com.pofol.main.member.handler.LoginFailureHandler;
import com.pofol.main.member.handler.LoginSuccessHandler;
import com.pofol.main.member.handler.MyAccessDeniedHandler;
import com.pofol.main.member.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {




    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()//인증이 필요한 url지정
                .antMatchers("/admin/**", "/admin1/**").hasAuthority("ADMIN")
//                .antMatchers("/order/checkout").hasAuthority("USER")
                .antMatchers("/grade", "/coupon", "/point/all", "/order/**", "/mypage/**", "/order/checkout").hasAnyAuthority("USER","ADMIN")
                .anyRequest().permitAll()
                .and()
                    .exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler())
                .and()
                    .formLogin()    //Form Login 방식 적용
                    .loginPage("/member/login_form")
                    .loginProcessingUrl("/login")
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler(authenticationFailureHandler())
                    .usernameParameter("mem_id")
                    .passwordParameter("mem_pwd")
                .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    //.logoutSuccessUrl("/member/info")
                    .logoutSuccessHandler(logoutSuccessHandler())
                    //.logoutSuccessUrl("/member/logout")
                    .invalidateHttpSession(true);


    }

    //UserDetailsService가 로그인 요청을 하고 리턴시
    //사용자가 적은 패스워드를 해쉬로 암호화하고  DB의 회원가입 되어 암호화 된 비밀번호와 비교를 먼저해준다.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }


    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new MyAccessDeniedHandler();
    }

    @Bean AuthenticationSuccessHandler authenticationSuccessHandler(){
        return  new LoginSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return  new LoginFailureHandler();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return  new UserDetailsServiceImpl();
    }

}
