package com.baomibing.work.report;

import java.util.List;

public class SequentialWorkReport extends MultipleWorkReport {

    public SequentialWorkReport() {

    }

    public static SequentialWorkReport aNewSequentialWorkReport() {
        return new SequentialWorkReport();
    }


    public static SequentialWorkReport aNewSequentialWorkReport(MultipleWorkReport workReport) {
        SequentialWorkReport report =  new SequentialWorkReport();
        report.copy(workReport);
        return report;
    }

    public SequentialWorkReport addAllReports(List<WorkReport> workReports) {
        getReports().addAll(workReports);
        return this;
    }

    public SequentialWorkReport addReport(WorkReport workReport) {
        getReports().add(workReport);
        return this;
    }

    public SequentialWorkReport setWorkName(String workName) {
        this.workName = workName;
        return this;
    }

}
