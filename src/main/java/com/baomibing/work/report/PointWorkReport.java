package com.baomibing.work.report;

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.work.WorkStatus;
import lombok.Getter;

public class PointWorkReport extends AbstractWorkReport {
    @Getter
    private WorkReport workReport;

    private PointWorkReport(WorkReport workReport) {
        this.workReport = workReport;
    }

    public static PointWorkReport aNewPointWorkReport(WorkReport workReport) {
        return new PointWorkReport(workReport);
    }

    @Override
    public WorkStatus getStatus() {
        return workReport.getStatus();
    }

    @Override
    public Throwable getError() {
        return workReport.getError();
    }

    @Override
    public WorkContext getWorkContext() {
        return workReport.getWorkContext();
    }

    @Override
    public Object getResult() {
        return workReport.getResult();
    }

    @Override
    public String getWorkName() {
        return workReport.getWorkName();
    }
}
