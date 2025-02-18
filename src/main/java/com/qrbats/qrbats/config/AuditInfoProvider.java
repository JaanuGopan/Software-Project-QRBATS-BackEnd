package com.qrbats.qrbats.config;

import com.qrbats.qrbats.security.SpringSecurityAuditorAware;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuditInfoProvider {

    private final SpringSecurityAuditorAware auditorAware;

    public Integer getCurrentUserIdentifier() {
        return auditorAware.getCurrentAuditor().orElse(null);
    }
}
