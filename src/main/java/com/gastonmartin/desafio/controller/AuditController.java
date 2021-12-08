package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.data.Audit;
import com.gastonmartin.desafio.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.java.Log;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@Log
@RestController
@SecurityRequirement(name = "ChallengeBasic")
public class AuditController {

    @Autowired
    private AuditService service;

    @Operation(summary = "Audit usage of endpoints", description = "Retrieve a list of endpoints requested over time with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit trail paginated", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Audit.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })

    @GetMapping("/audit")
    @ResponseStatus(HttpStatus.OK)
    @PageableAsQueryParam
    public Page<Audit> getAuditLog(@Parameter(hidden = true) Pageable pageRequest) {
        log.info(format("Getting audit logs for page %d", pageRequest.getPageNumber()));
        return service.findAll(pageRequest);
    }
}
