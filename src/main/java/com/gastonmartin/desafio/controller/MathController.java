package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.model.AdditionRequest;
import com.gastonmartin.desafio.model.AdditionResult;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@Log
@RestController("/v1/math")
public class MathController {

    @PostMapping("/sum")
    @ResponseStatus(HttpStatus.OK)
    public AdditionResult sumTwoNumbers(@RequestBody final AdditionRequest numbers) {
        log.info(format("Summing %f + %f", numbers.getLeft(), numbers.getRight()));
        return new AdditionResult(numbers.getLeft() + numbers.getRight());
    }
}
