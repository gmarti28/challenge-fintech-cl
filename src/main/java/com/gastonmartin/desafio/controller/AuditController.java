package com.gastonmartin.desafio.controller;

import com.gastonmartin.desafio.data.Audit;
import com.gastonmartin.desafio.service.AuditService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.java.Log;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;

@Log
@RestController
@SecurityRequirement(name = "ChallengeBasic")
public class AuditController {

    @Autowired
    private AuditService service;

    @GetMapping("/audit")
    @ResponseStatus(HttpStatus.OK)
    @PageableAsQueryParam
    public Page<Audit> getAuditLog(@Parameter(hidden = true) Pageable pageRequest) {
        log.info(format("Getting audit logs for page %d", pageRequest.getPageNumber()));
        return service.findAll(pageRequest);
    }
}
