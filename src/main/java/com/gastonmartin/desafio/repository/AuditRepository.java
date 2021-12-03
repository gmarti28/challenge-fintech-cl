package com.gastonmartin.desafio.repository;

import com.gastonmartin.desafio.data.Audit;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AuditRepository extends PagingAndSortingRepository<Audit, Long> {

}
