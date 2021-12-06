package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.model.AdditionRequest;
import com.gastonmartin.desafio.model.AdditionResult;
import com.gastonmartin.desafio.service.AuditService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AuditService service;

    @PostMapping("/math/sum")
    @ResponseStatus(HttpStatus.OK)
    public AdditionResult sumTwoNumbers(@RequestBody final AdditionRequest numbers) {
        log.info(format("Summing %f + %f", numbers.getLeft(), numbers.getRight()));
        //service.saveAudit("/math/sum");
        return new AdditionResult(numbers.getLeft() + numbers.getRight());
    }
}
