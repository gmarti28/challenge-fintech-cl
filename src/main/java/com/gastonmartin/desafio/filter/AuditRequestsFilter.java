package com.gastonmartin.desafio.filter;

import com.gastonmartin.desafio.service.AuditService;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

@Log
public class AuditRequestsFilter extends OncePerRequestFilter {

    private List<String> includeURIList;

    public AuditRequestsFilter(){
        this(Collections.emptyList());
    }

    public AuditRequestsFilter(List<String> includeURIList) {
        this.includeURIList = includeURIList;
    }

    // No autowiring available here
    private AuditService auditService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // Este filtro guarda en una tabla el acceso a los endpoints
        // En caso de fallar no fracasa y llama al siguiente filtro de la cadena.
        try {
            String uri = req.getRequestURI();
            String method = req.getMethod();
            String userId = "";

            for (String s: includeURIList){
                if (uri.startsWith(s)){
                    if(auditService==null){
                        ServletContext servletContext = req.getServletContext();
                        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
                        auditService = webApplicationContext.getBean(AuditService.class);
                    }
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null) {
                        userId = auth.getName();
                        auditService.saveAudit(method, uri, userId);
                    }
                }
            }
        } catch (Exception e){
            log.warning(format("Error %s in AuditRequestsFilter", e.getMessage()));
            e.printStackTrace();
        }
        filterChain.doFilter(req, res);
    }
}
