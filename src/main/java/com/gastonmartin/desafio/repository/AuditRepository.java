package com.gastonmartin.desafio.repository;

import com.gastonmartin.desafio.data.Audit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditRepository extends PagingAndSortingRepository<Audit, Long>{}
