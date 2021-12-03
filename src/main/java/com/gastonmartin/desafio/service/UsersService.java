package com.gastonmartin.desafio.service;

import com.gastonmartin.desafio.exception.InvalidPasswordException;
import com.gastonmartin.desafio.exception.InvalidUsernameException;
import com.gastonmartin.desafio.exception.UserAlreadyExistsException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static java.lang.String.format;

@Service
public class UsersService {

    @Autowired
    JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void createUser(@NonNull String username, @NonNull String password) throws UserAlreadyExistsException, InvalidUsernameException, InvalidPasswordException {
        if (username.trim().isBlank()) throw new InvalidUsernameException(format("username '%s' is not valid", username));
        if (password.isBlank()) throw new InvalidPasswordException("password is not valid");
        if (password.trim().length() < 5) throw new InvalidPasswordException(format("password should be at least 5 characters long"));
        if (jdbcUserDetailsManager.userExists(username)) throw new UserAlreadyExistsException("user " + username + " already exists");
        UserDetails user = new User(
                username,
                passwordEncoder.encode(password),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        jdbcUserDetailsManager.createUser(user);
    }
}
