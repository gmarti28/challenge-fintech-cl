package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.exception.InvalidPasswordException;
import com.gastonmartin.desafio.exception.InvalidUsernameException;
import com.gastonmartin.desafio.exception.UserAlreadyExistsException;
import com.gastonmartin.desafio.model.UserCreationRequest;
import com.gastonmartin.desafio.service.AuditService;
import com.gastonmartin.desafio.service.UsersService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static java.lang.String.format;

@Log
@RestController
public class UserController {

    @Autowired
    private AuditService auditService;

    @Autowired
    private UsersService usersService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = Exception.class)
    public String createUser(@RequestBody UserCreationRequest newUser) throws InvalidPasswordException, UserAlreadyExistsException, InvalidUsernameException {
        String username = newUser.getUsername();
        String password = newUser.getPassword();

        log.info(format("Registering user %s", username));
        auditService.saveAudit("/signup");
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
}
