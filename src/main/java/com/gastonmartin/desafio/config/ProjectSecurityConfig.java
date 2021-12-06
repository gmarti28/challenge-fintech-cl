package com.gastonmartin.desafio.config;

import com.gastonmartin.desafio.filter.AuditRequestsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity(debug=false)
public class ProjectSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // todo: Restringir los endpoints para que por default se requiera authenticacion si no estan listados.

        http
                .csrf().disable()
                .addFilterBefore(new AuditRequestsFilter(List.of("/math/sum", "/login2", "/logout", "/audit")), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .mvcMatchers("/math/sum").hasRole("USER")
                .mvcMatchers("/math/sum").hasRole("USER")
                .mvcMatchers("/audit").hasRole("USER")
                .mvcMatchers("/logout").authenticated()
                .mvcMatchers("/signup").permitAll()
                .mvcMatchers("/swagger-ui/**").permitAll()
                .mvcMatchers("/v3/api-docs/**").permitAll()
                .and().httpBasic()
                .and().formLogin()
                .and()
                .logout()
                .logoutUrl("/api/logout") // Este endpoint no se va a usar porque no aparece en swagger y no dispara los filters (AuditFilter por ej.)
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
        //todo: Agregar csrf token
        //todo: Agregar control de cors explicito pero permisivo en un bean

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

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() throws Exception {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
        jdbcUserDetailsManager.setDataSource(dataSource);
        return jdbcUserDetailsManager;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
