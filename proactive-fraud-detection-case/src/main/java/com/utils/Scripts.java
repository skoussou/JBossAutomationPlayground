package com.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.demo.Fraud;
import com.demo.SuspiciousActivity;
import com.demo.Transaction;

import org.jbpm.casemgmt.api.CaseService;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseRoleInstance;
import org.jbpm.services.api.service.ServiceRegistry;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.task.model.OrganizationalEntity;

/**
 * Scripts
 */
public class Scripts {

    // Usage:
    // com.utils.Scripts.init(kcontext);
    public static void init(ProcessContext kcontext) {

        // init data when they are not passed at start time
        initData(kcontext);

        // for the sake of simplicity initialize roles to avoid failure when the user
        // start the case with empty roles
        initRoles(kcontext);
    }

    private static void initRoles(ProcessContext kcontext) {
        CaseAssignment caseAssignment = kcontext.getCaseAssignment();

        Collection<OrganizationalEntity> entities = caseAssignment.getAssignments("owner");
        OrganizationalEntity owner = entities.iterator().next();

        CaseService caseService = (CaseService) ServiceRegistry.get().service(ServiceRegistry.CASE_SERVICE);
        String caseId = ((CaseFileInstance) kcontext.getCaseData()).getCaseId();
        Collection<CaseRoleInstance> caseRoleAssignments = caseService.getCaseRoleAssignments(caseId);

        for (CaseRoleInstance caseRoleInstance : caseRoleAssignments) {
            if (caseRoleInstance.getRoleAssignments().isEmpty())
                caseService.assignToCaseRole(caseId, caseRoleInstance.getRoleName(), owner);
        }
    }

    private static void initData(ProcessContext kcontext) {
        SuspiciousActivity activity = (SuspiciousActivity) kcontext.getCaseData().getData("activity");

        if (activity == null) {
            Transaction transaction = new Transaction();
            transaction.setAmount(250.0);
            transaction.setAuthCode("543");
            transaction.setCardType("secured credit");
            transaction.setDateTimeExec(new Date());
            transaction.setLocation("635 Sutter Street, Union Square, San Francisco, CA 94102, USA");
            transaction.setMerchantCode("456-654-1234");
            transaction.setMerchantName("Hotel BestPlace");

            activity = new SuspiciousActivity();

            activity.setRiskRanking(2);
            List<Transaction> transactions = new ArrayList<>(1);
            transactions.add(transaction);
            activity.setTransactions(transactions);
            
            kcontext.getCaseData().add("activity", activity);
        }
    }

    // com.utils.Scripts.followUpOnEntry(kcontext);
    public static void followUpOnEntry(ProcessContext kcontext) {
        Fraud fraud = (Fraud) kcontext.getCaseData().getData("fraud");

        fraud.setStatus("follow up");
        kcontext.getCaseData().add("fraud", fraud);
    }
}