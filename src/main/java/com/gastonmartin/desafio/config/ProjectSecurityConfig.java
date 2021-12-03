package com.gastonmartin.desafio.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class ProjectSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // todo: Restringir los endpoints para que por default se requiera authenticacion si no estan listados.

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/math/sum").authenticated()
                .antMatchers("/audit").authenticated()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()
                .and().httpBasic()
                .and().formLogin();
    }

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.jdbcAuthentication()
                .dataSource(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
