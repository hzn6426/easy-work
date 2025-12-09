package com.baomibing.work.report;

import java.util.List;

public class LoopWorkReport extends MultipleWorkReport {

    public LoopWorkReport() {}

    public static LoopWorkReport aNewLoopWorkReport() {
        return new LoopWorkReport();
    }



    public static LoopWorkReport aNewLoopWorkReport(MultipleWorkReport workReport) {
        LoopWorkReport report =  new LoopWorkReport();
        report.copy(workReport);
        return report;
    }

    public LoopWorkReport addAllReports(List<WorkReport> workReports) {
        getReports().addAll(workReports);
        return this;
    }

    public LoopWorkReport addReport(WorkReport workReport) {
        getReports().add(workReport);
        return this;
    }

    public LoopWorkReport setWorkName(String workName) {
        this.workName = workName;
        return this;
    }
}
