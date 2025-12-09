package com.baomibing.work.report;

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.WorkStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultipleWorkReport extends AbstractWorkReport {

    @Getter
    private final List<WorkReport> reports;
    protected WorkContext workContext;
    protected WorkStatus status;
    protected Throwable error;
    protected String workName;

    public MultipleWorkReport() {
        reports = new ArrayList<>();
    }


    public MultipleWorkReport setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public MultipleWorkReport setStatus(WorkStatus status) {
        this.status = status;
        return this;
    }

    public MultipleWorkReport setError(Throwable error) {
        this.error = error;
        return this;
    }

    public MultipleWorkReport setWorkName(String workName) {
        this.workName = workName;
        return this;
    }

    public MultipleWorkReport addAllReports(List<WorkReport> workReports) {
        reports.addAll(workReports);
        return this;
    }

    public MultipleWorkReport addReport(WorkReport workReport) {
        reports.add(workReport);
        return this;
    }

    @Override
    public WorkStatus getStatus() {
        if (Checker.BeEmpty(reports) && Checker.BeNotNull(status)) {
            return status;
        }
        for (WorkReport report : reports) {
            if (report.getStatus().equals(WorkStatus.FAILED)) {
                return WorkStatus.FAILED;
            }
        }
        return WorkStatus.COMPLETED;
    }


    @Override
    public Throwable getError() {
        if (Checker.BeEmpty(reports) && Checker.BeNotNull(error)) {
            return error;
        }
        for (WorkReport report : reports) {
            Throwable error = report.getError();
            if (error != null) {
                return error;
            }
        }
        return null;
    }


    @Override
    public WorkContext getWorkContext() {
        if (Checker.BeEmpty(reports) && Checker.BeNotNull(workContext)) {
            return workContext;
        }
        WorkContext workContext = new WorkContext();
        for (WorkReport report : reports) {
            WorkContext partialContext = report.getWorkContext();
            for (Map.Entry<String, Object> entry : partialContext.getContextMap().entrySet()) {
                workContext.put(entry.getKey(), entry.getValue());
            }
        }
        return workContext;
    }

    @Override
    public String getWorkName() {
        return workName;
    }

    @Override
    public List<Object> getResult() {
        return reports.stream().map(WorkReport::getResult).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T> T getResult(int index, Class<T> clazz) {
        return (T) getResult().get(index);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> getResultCollection(int index, Class<T> clazz) {
        return (Collection<T>) getResult(index, clazz);
    }

    protected void copy(MultipleWorkReport source) {
        setStatus(source.getStatus());
        setWorkName(source.getWorkName());
        setWorkContext(source.getWorkContext());
        setError(source.getError());
        addAllReports(source.getReports());

    }
}
