package com.gastonmartin.desafio.service;

import com.gastonmartin.desafio.data.Audit;
import com.gastonmartin.desafio.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.ZonedDateTime;

@Service
public class AuditService {

    @Autowired
    AuditRepository repository;

    @Autowired
    Clock clock;

    @Transactional(rollbackFor = Exception.class)
    public void saveAudit(String method, String url, String userId) {
        repository.save(new Audit(null,
                method,
                userId,
                url,
                ZonedDateTime.now(clock)));
    }

    @Transactional(readOnly = true)
    public Page<Audit> findAll(Pageable page) {
        return repository.findAll(page);
    }
}
