package com.baomibing.work.context;

import com.baomibing.work.report.WorkReport;
import lombok.Getter;

public class PointContext extends WorkContext {
    @Getter
    private WorkReport pointWorkReport;

    public PointContext(WorkReport workReport) {
        this.pointWorkReport = workReport;
    }

    public static PointContext aNewPointContext(WorkReport workReport) {
        return new PointContext(workReport);
    }
}
