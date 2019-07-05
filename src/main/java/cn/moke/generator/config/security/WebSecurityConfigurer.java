package cn.moke.generator.config.security;

import cn.moke.generator.config.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import javax.annotation.Resource;

/**
 * @author moke
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Value("${moke.authority.login-uri:/auth/login}")
    private String loginUrl;
    @Value("${moke.authority.logout-uri:/auth/logout}")
    private String logoutUrl;
    @Resource
    private CustomLogoutHandler customLogoutHandler;
    @Resource
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @Resource
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Resource
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Resource
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Resource
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;
    @Resource
    private CustomAccessDecisionManager customAccessDecisionManager;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        // 认证信息不存入session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 在注销过滤器前加入自定义的jwt认证过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, LogoutFilter.class);
        // 使用自定义的用户密码认证过滤器替换默认的用户密码认证过滤器
        http.addFilterAt(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // 添加自定义的认证授权的失败的处理
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler).authenticationEntryPoint(customAuthenticationEntryPoint);
        //
        http.authorizeRequests()
                .anyRequest().authenticated()
                // 设置FilterSecurityInterceptor,让其调用自定义的AccessDecisionManager和SecurityMetadataSource,进而实现从数据库动态获取【匹配规则】
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                        fsi.setAccessDecisionManager(customAccessDecisionManager);
                        fsi.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        return fsi;
                    }
                })
             .and()
                .formLogin()
                .permitAll()
             .and().cors()
                .and()
                .logout()
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .logoutUrl(logoutUrl)
                .permitAll()
             .and()
                // 禁用csrf防御。由于没使用cookie存token，不存在csrf攻击；且csrf会导致post请求需要csrf验证
                .csrf().disable();

    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter() throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
        filter.setFilterProcessesUrl(loginUrl);
        filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

//    public static void main(String[] args) {
//        String encode = new BCryptPasswordEncoder(8).encode("111111");
//        System.out.println(encode);
//    }
}
