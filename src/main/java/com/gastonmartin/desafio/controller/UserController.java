package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.exception.InvalidPasswordException;
import com.gastonmartin.desafio.exception.InvalidUsernameException;
import com.gastonmartin.desafio.exception.UserAlreadyExistsException;
import com.gastonmartin.desafio.model.LoginRequest;
import com.gastonmartin.desafio.model.UserCreationRequest;
import com.gastonmartin.desafio.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;

@Log
@RestController
public class UserController {


    @Autowired
    private UsersService usersService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = Exception.class)
    public String createUser(@RequestBody UserCreationRequest newUser) throws InvalidPasswordException, UserAlreadyExistsException, InvalidUsernameException {
        String username = newUser.getUsername();
        String password = newUser.getPassword();

        log.info(format("Registering user %s", username));
        try {
            usersService.createUser(username, password);
        } catch (InvalidPasswordException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Password", e);
        } catch (InvalidUsernameException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Username", e);
        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exists", e);
        }
        return format("User %s successfully created", username);
    }

    @PostMapping(value="/login")
    public String login(@RequestBody LoginRequest loginRequest){
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(usersService.loginUser(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.setContext(context);
        return "Logged in";
    }

    @PostMapping(value="/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "Logged out";
    }
}
