package com.yellowstone.cleantogather.api.config;

import com.yellowstone.cleantogather.api.security.JwtTokenFilterConfigurer;
import com.yellowstone.cleantogather.api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Disable CORS and CSRF (cross site request forgery)
        http.cors().and().csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Entry points
        http.authorizeRequests()
                .antMatchers("/signin").permitAll()
                .antMatchers(HttpMethod.GET, "/events").permitAll()
                .antMatchers(HttpMethod.GET, "/events/{id}").permitAll()
                .antMatchers(HttpMethod.POST, "/events/subscribe").permitAll()
                .antMatchers(HttpMethod.GET, "/events/subscribed/{id}").permitAll()
                .antMatchers(HttpMethod.POST, "/events/unsubscribe").permitAll()
                .antMatchers(HttpMethod.POST, "/markers").permitAll()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/users/mail/{mail}").permitAll()
                .antMatchers(HttpMethod.GET, "/users/name/{name}").permitAll()
                .antMatchers(HttpMethod.GET, "/events/count").permitAll()
                .antMatchers(HttpMethod.GET, "/markers/count").permitAll()
                .anyRequest().authenticated();

        // If a user try to access a resource without having enough permissions
        // http.exceptionHandling().accessDeniedPage("/users/signin");

        // Apply JWT
        http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

        // Optional, if you want to test the API from a browser
        // http.httpBasic();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Allow swagger to be accessed without authentication
        web.ignoring().antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/configuration/**")
                .antMatchers("/webjars/**")
                .antMatchers("/public");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
