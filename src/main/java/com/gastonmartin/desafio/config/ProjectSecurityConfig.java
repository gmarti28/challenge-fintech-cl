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
                // La configuracion se genera sin CORS ni CSRF porque no fue mencionado en la consigna.
                // En produccion para acceder a este backend se recomienda implementar un token JWT
                // o un Token contra Cross Site Request Forgery que impida impersonar al usuario sin su consentimiento
                // De agregar un frontend habria que establecer una politica de CORS.
                .csrf().disable()
                .addFilterAfter(new AuditRequestsFilter(List.of("/signup", "/math/add", "/login", "/logout", "/audit")), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .mvcMatchers("/math/add").hasRole("USER")
                .mvcMatchers("/audit").hasRole("USER")
                .mvcMatchers("/logout").authenticated()
                .mvcMatchers("/login").permitAll()
                .mvcMatchers("/signup").permitAll()
                .mvcMatchers("/swagger-ui/**").permitAll()
                .mvcMatchers("/swagger-ui.html").permitAll()
                .mvcMatchers("/error").permitAll()
                .mvcMatchers("/").permitAll()
                .mvcMatchers("/v3/api-docs/**").permitAll()
                .anyRequest().authenticated() // Configuracion default para nuevos endpoints
                .and().httpBasic()
                .and()
                // Spring activa el logout por default con una url /logout que es la misma que estoy utilizando en mi controller
                // pero como no aparece en Swagger la reemplacé con mi implementación por consiguiente la defino con una
                // url customizada que no voy a utilizar (se puede utilizar sin problemas)
                .logout()
                .logoutUrl("/api/logout") // Renombro la url oculta de logout.
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");
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
