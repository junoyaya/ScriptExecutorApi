package com.juno.groovy.executor.security;

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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;
  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;
  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // Disable CSRF (cross site request forgery)
    http.csrf().disable()
        // No session will be created or used by spring security
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        // Entry points
        .authorizeRequests().antMatchers("/auth/**").permitAll()//
        // .and()
        // .formLogin().loginPage("/auth/signin").permitAll()
        .and()
        // Disallow everything else..
        .authorizeRequests().antMatchers("/api/**").authenticated();

    // If a user try to access a resource without having enough permissions
    http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).accessDeniedPage("/auth/signin");

    // Apply JWT
    http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

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


  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
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
