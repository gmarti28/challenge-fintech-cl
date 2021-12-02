package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.model.AdditionRequest;
import com.gastonmartin.desafio.model.AdditionResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/v1/math")
public class MathController {

    @PostMapping("/sum")
    public AdditionResult sumTwoNumbers(@RequestBody final AdditionRequest numbers) {
        return new AdditionResult(numbers.getLeft() + numbers.getRight());
    }
}
