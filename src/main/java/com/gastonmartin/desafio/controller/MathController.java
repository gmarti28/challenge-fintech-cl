package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.model.AdditionRequest;
import com.gastonmartin.desafio.model.AdditionResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@Log
@RestController
@SecurityRequirement(name = "ChallengeBasic")
public class MathController {

    @PostMapping("/math/add")
    @ResponseStatus(HttpStatus.OK)
    public AdditionResult sumTwoNumbers(@RequestBody final AdditionRequest numbers) {
        log.info(format("Adding %f to %f", numbers.getLeft(), numbers.getRight()));
        return new AdditionResult(numbers.getLeft() + numbers.getRight());
    }
}
