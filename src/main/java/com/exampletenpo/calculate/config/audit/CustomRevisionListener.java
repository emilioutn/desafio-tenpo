package com.exampletenpo.calculate.config.audit;

import com.exampletenpo.calculate.config.context.ContextHolder;
import com.exampletenpo.calculate.domain.audit.Revision;
import com.exampletenpo.calculate.service.SecurityService;
import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Autowired;


public class CustomRevisionListener implements RevisionListener {

    @Autowired
    private SecurityService securityService;

    @Override
    public void newRevision(Object revisionEntity) {
        final Revision revision = (Revision) revisionEntity;
        revision.setUserName(securityService.getThreadAccountUserName());
        revision.setFromIp(ContextHolder.getContext().getIpFrom());
    }
}
