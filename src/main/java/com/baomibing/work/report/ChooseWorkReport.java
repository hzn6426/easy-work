package com.baomibing.work.report;

import java.util.List;

public class ChooseWorkReport extends MultipleWorkReport {
    public ChooseWorkReport() {

    }

    public static ChooseWorkReport aNewChooseWorkReport() {
        return new ChooseWorkReport();
    }


    public static ChooseWorkReport aNewChooseWorkReport(MultipleWorkReport workReport) {
        ChooseWorkReport report =  new ChooseWorkReport();
        report.copy(workReport);
        return report;
    }

    public ChooseWorkReport addAllReports(List<WorkReport> workReports) {
        getReports().addAll(workReports);
        return this;
    }

    public ChooseWorkReport addReport(WorkReport workReport) {
        getReports().add(workReport);
        return this;
    }

    public ChooseWorkReport setWorkName(String workName) {
        this.workName = workName;
        return this;
    }
}
