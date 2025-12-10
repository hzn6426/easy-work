package com.baomibing.work.report;

import java.util.List;

public class RepeatWorkReport extends MultipleWorkReport {

    public RepeatWorkReport() {}

    public static RepeatWorkReport aNewRepeatWorkReport() {
        return new RepeatWorkReport();
    }



    public static RepeatWorkReport aNewRepeatWorkReport(MultipleWorkReport workReport) {
        RepeatWorkReport report =  new RepeatWorkReport();
        report.copy(workReport);
        return report;
    }

    public RepeatWorkReport addAllReports(List<WorkReport> workReports) {
        getReports().addAll(workReports);
        return this;
    }

    public RepeatWorkReport addReport(WorkReport workReport) {
        getReports().add(workReport);
        return this;
    }

    public RepeatWorkReport setWorkName(String workName) {
        this.workName = workName;
        return this;
    }
}
