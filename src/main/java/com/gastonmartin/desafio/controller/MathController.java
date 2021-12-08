package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.model.AdditionRequest;
import com.gastonmartin.desafio.model.AdditionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Add two numbers", description = "Given a left and a right operands calculate their addition", security = {
            @SecurityRequirement(name = "ChallengeBasic") })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AdditionResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    @PostMapping("/math/add")
    @ResponseStatus(HttpStatus.OK)
    public AdditionResponse sumTwoNumbers(@RequestBody final AdditionRequest numbers) {
        log.info(format("Adding %f to %f", numbers.getLeft(), numbers.getRight()));
        return new AdditionResponse(numbers.getLeft() + numbers.getRight());
    }
}
