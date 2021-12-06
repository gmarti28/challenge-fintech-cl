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

    public AuditRequestsFilter(List<String> includeURIList) {
        this.includeURIList = includeURIList;
    }

    public AuditRequestsFilter(){
        this(Collections.emptyList());
    }

    // No autowiring available here
    private AuditService auditService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String uri = req.getRequestURI();
        for (String s: includeURIList){
            if (uri.startsWith(s)){
                if(auditService==null){
                    ServletContext servletContext = req.getServletContext();
                    WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
                    auditService = webApplicationContext.getBean(AuditService.class);
                }
                log.warning(format("TRACE: LOGGEAR %s %s", uri, req.getMethod()));
                auditService.saveAudit(uri);
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth!=null){
            log.warning(format("TRACE: el usuario es %s", auth.getName()));
        } else {
            log.warning(format("TRACE: no hay usuario autenticado!!!"));
        }
        filterChain.doFilter(req, res);
    }
}
