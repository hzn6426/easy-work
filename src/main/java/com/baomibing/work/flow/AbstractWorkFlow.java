/**
 * Copyright (c) 2025-2025, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomibing.work.flow;

import com.baomibing.work.context.PointContext;
import com.baomibing.work.context.WorkContext;
import com.baomibing.work.exception.WorkFlowException;
import com.baomibing.work.report.DefaultWorkReport;
import com.baomibing.work.report.MultipleWorkReport;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.step.LastStep;
import com.baomibing.work.step.ThenStep;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.*;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomibing.work.context.PointContext.aNewPointContext;
import static com.baomibing.work.report.DefaultWorkReport.aNewWorkReport;
import static com.baomibing.work.report.SequentialWorkReport.aNewSequentialWorkReport;
import static com.baomibing.work.work.WorkStatus.COMPLETED;
import static com.baomibing.work.work.WorkStatus.FAILED;

/**
 * An abstract workflow that implements basic process execution and universal process operations
 *
 * @author zening (316279829@qq.com)
 */
public abstract class AbstractWorkFlow implements  ThenStep, LastStep {
    @Getter
    protected String name = UUID.randomUUID().toString();
    protected WorkExecutePolicy workExecutePolicy = WorkExecutePolicy.FAST_FAIL_EXCEPTION;
    @Getter
    protected WorkContext workContext = null;
    @Getter
    protected Boolean beTrace = Boolean.FALSE;
    @Getter
    protected String currentPoint = null;

    protected final List<Function<WorkReport, Work>> thenFuns = new ArrayList<>();

    @Getter
    protected Work lastWork;

    @Getter
    protected final MultipleWorkReport executedReport = new MultipleWorkReport();
    @Getter
    protected final Map<String, WorkReport> executedReportMap = new HashMap<>();

    public abstract MultipleWorkReport execute();


    @Override
    public MultipleWorkReport execute(WorkContext context) {
        this.workContext = context;
        try {
            MultipleWorkReport report = execute();
            return doThenWork(report);
        } finally {
            doLastWork();
        }
    }


    @Override
    public AbstractWorkFlow then(Function<WorkReport, Work> fun) {
        thenFuns.add(fun);
        return this;
    }

    @Override
    public AbstractWorkFlow then(Work work) {
         thenFuns.add(report -> work);
         return this;
    }

    @Override
    public AbstractWorkFlow lastly(Work work) {
        this.lastWork = work;
        return this;
    }

    protected MultipleWorkReport doThenWork(MultipleWorkReport workReport) {
        List<Work> works = new ArrayList<>();
        if (Checker.BeNotEmpty(thenFuns)) {
            for (Function<WorkReport, Work> fun : thenFuns) {
                works.add(fun.apply(workReport));
            }
        }
        if (Checker.BeEmpty(works)) {
            return workReport;
        }

        return workReport.addReport(doSequenceExecute(works, workContext).setWorkName(workReport.getWorkName() + "_then"));

    }

    protected void doLastWork() {
        if (Checker.BeNotNull(lastWork)) {
            WorkReport report = doSingleWork(lastWork, workContext);
            traceReport(report);
        }
    }

    protected MultipleWorkReport doSequenceExecute(List<Work> works, WorkContext context) {
        List<WorkReport> reports = new ArrayList<>();
        for (Work work : works) {
            WorkReport report;
            if (Checker.BeNull(work)) {
                report = aNewWorkReport();
            } else {
                report = doSingleWork(work, context);
            }
            reports.add(report);
            if (beBreak(report)) {
                break;
            }
        }

        traceReport(aNewSequentialWorkReport().setWorkName(name).addAllReports(reports));
        return getPolicyReport(reports, context);
    }

    protected boolean beThePoint(Work work, String point) {
        if (Checker.BeNotEmpty(point) && work instanceof NamedPointWork) {
            NamedPointWork pointWork = (NamedPointWork)work;
            return pointWork.getPoint().equals(point);
        }
        return false;
    }

    protected boolean beNeedSkip(Work work, String point) {
        if (Checker.BeNotEmpty(point)) {
            if (work instanceof NamedPointWork) {
                NamedPointWork pointWork = (NamedPointWork)work;
                return pointWork.isBeExecuted();
            }
        }
        return false;
    }

    protected void saveCurrentPoint(String point) {
        currentPoint = point;
    }

    protected void savePointReportIfNeed(WorkReport report,  String point) {
        if (Checker.BeNotEmpty(currentPoint) && currentPoint.equals(point)) {
            PointContext pointContext = aNewPointContext(report);
            pointContext.copy(workContext);
            this.workContext = pointContext;
        }
    }

    protected WorkReport wrapPointReportIfNeed(WorkReport report, WorkContext context, String point) {
        WorkReport target = report;
        if (context instanceof PointContext) {
            PointContext pointContext = (PointContext)context;
            target = pointContext.getPointWorkReport();
        }
        savePointReportIfNeed(target, point);
        return target;
    }

    protected WorkReport doSingleWorkExceptionally(Work work, WorkContext context) {
        WorkReport workReport;
        try {
            workReport = doSingleWorkInternal(work, context);
            return workReport;
        } catch (Exception e) {
            if (e instanceof WorkFlowException) {
                throw (WorkFlowException) e;
            } else {
                throw (RuntimeException) e;
            }
        }

    }


    private WorkReport doSingleWorkInternal(Work work, WorkContext context) {
        WorkReport workReport ;
        if (Checker.BeNull(context)) {
            if (Checker.BeNotNull(workContext)) {
                context = workContext;
            } else {
                context = new WorkContext();
            }
        } else {
            if (Checker.BeNotNull(workContext)) {
                context.copy(workContext);
            }
        }
        if (work instanceof WorkFlow) {
            WorkFlow workFlow = (WorkFlow) work;
            workReport = workFlow.execute(context);
        } else {
            Object object = work.execute(context);
            if (object instanceof WorkReport) {
                workReport =  (WorkReport) object;
            } else {
                workReport = new DefaultWorkReport().setError(null).setWorkContext(context).setResult(object).setStatus(WorkStatus.COMPLETED);
                if (work instanceof NamedPointWork) {
                    ((DefaultWorkReport) workReport).setWorkName(((NamedPointWork)work).getName());
                }
            }
        }
        return workReport;
    }

    protected WorkReport doSingleWork(Work work, WorkContext context) {
        WorkReport workReport ;
        try {
            workReport = doSingleWorkInternal(work, context);
        } catch (Exception e) {
            workReport = new DefaultWorkReport().setError(e).setWorkContext(context).setResult(null).setStatus(FAILED);
            if (work instanceof NamedPointWork) {
                ((DefaultWorkReport) workReport).setWorkName(((NamedPointWork)work).getName());
            }
        }
        return workReport;
    }

    protected boolean beBreak(WorkReport workReport) {
        if (workReport.getStatus() == FAILED) {
            if (workExecutePolicy == WorkExecutePolicy.FAST_FAIL) {
                return true;
            } else if (workExecutePolicy == WorkExecutePolicy.FAST_FAIL_EXCEPTION) {
                return Checker.BeNotNull(workReport.getError());
            } else if (workExecutePolicy == WorkExecutePolicy.FAST_EXCEPTION) {
                throw new WorkFlowException(workReport.getError());
            }
        } else if (workReport.getStatus() == COMPLETED) {
            return workExecutePolicy == WorkExecutePolicy.FAST_SUCCESS;
        }
        return false;
    }

    protected WorkContext getDefaultWorkContext() {
        WorkContext context = workContext;
        if (Checker.BeNull(context)) {
            context = new WorkContext();
        }
        return context;
    }

    protected WorkReport doDefaultExecute(WorkFlow workFlow) {
        return workFlow.execute(getDefaultWorkContext());
    }


    protected MultipleWorkReport getPolicyReport(final List<WorkReport> reports, WorkContext context) {
        MultipleWorkReport multipleWorkReport;
        if (WorkExecutePolicy.FAST_SUCCESS == workExecutePolicy) {
            multipleWorkReport =  withFastSuccessResult(reports, context);
        } else if (WorkExecutePolicy.FAST_FAIL == workExecutePolicy ) {
            multipleWorkReport = withFastFailResult(reports, context);
        } else if (WorkExecutePolicy.FAST_FAIL_EXCEPTION == workExecutePolicy) {
            multipleWorkReport = withFastFailExceptionResult(reports, context);
        } else if (WorkExecutePolicy.FAST_ALL == workExecutePolicy) {
            multipleWorkReport = withFastAllResult(reports, context);
        } else if (WorkExecutePolicy.FAST_EXCEPTION == workExecutePolicy) {
            multipleWorkReport = withFastExceptionallyResult(reports, context);
        } else if (WorkExecutePolicy.FAST_ALL_SUCCESS == workExecutePolicy) {
            multipleWorkReport = withFastAllSuccessResult(reports, context);
        } else {
            throw new RuntimeException("Not support work execute policy:" + workExecutePolicy);
        }
        multipleWorkReport.setWorkName(name);
        return multipleWorkReport;
    }


    protected MultipleWorkReport withFastFailResult(List<WorkReport> reports, WorkContext workContext) {
        MultipleWorkReport workReport = new MultipleWorkReport();

        if (Checker.BeEmpty(reports)) {
            workReport.setStatus(COMPLETED).setWorkContext(workContext);
            return workReport;
        }

        WorkReport report = reports.stream().filter(r -> WorkStatus.FAILED.equals(r.getStatus())).findFirst().orElse(null);
        if (report != null) {
            workReport.addReport(report);
            workReport.setStatus(FAILED).setWorkContext(workContext);
        } else {
            workReport.addAllReports(reports);
            workReport.setStatus(COMPLETED).setWorkContext(workContext);
        }

        return workReport;
    }

    protected MultipleWorkReport withFastFailExceptionResult(List<WorkReport> reports, WorkContext workContext) {
        MultipleWorkReport workReport = new MultipleWorkReport();

        if (Checker.BeEmpty(reports)) {
            workReport.setStatus(COMPLETED).setWorkContext(workContext);
            return workReport;
        }

        WorkReport report = reports.stream().filter(r -> WorkStatus.FAILED.equals(r.getStatus()) && Checker.BeNotNull(r.getError())).findFirst().orElse(null);
        if (report != null) {
            workReport.addReport(report);
            workReport.setStatus(FAILED).setWorkContext(workContext);
        } else {
            workReport.addAllReports(reports);
            workReport.setStatus(COMPLETED).setWorkContext(workContext);
        }

        return workReport;
    }

    protected MultipleWorkReport withFastSuccessResult(List<WorkReport> reports, WorkContext workContext) {
        MultipleWorkReport workReport = new MultipleWorkReport();
        if (Checker.BeEmpty(reports)) {
            workReport.setStatus(FAILED).setWorkContext(workContext);
            return workReport;
        }
        WorkReport report = reports.stream().filter(r -> COMPLETED.equals(r.getStatus())).findFirst().orElse(null);
        if (report != null) {
            workReport.addReport(report);
            workReport.setWorkContext(this.workContext).setStatus(WorkStatus.COMPLETED);
        } else {
            workReport.setStatus(FAILED).setWorkContext(workContext);
        }

        return workReport;
    }

    protected MultipleWorkReport withFastAllSuccessResult(List<WorkReport> reports, WorkContext workContext) {
        MultipleWorkReport workReport = new MultipleWorkReport();
        if (Checker.BeEmpty(reports)) {
            workReport.setStatus(FAILED).setWorkContext(workContext);
            return workReport;
        }

        List<WorkReport> successReports = reports.stream().filter(r -> WorkStatus.COMPLETED.equals(r.getStatus())).collect(Collectors.toList());
        if (Checker.BeNotEmpty(successReports)) {
            workReport.addAllReports(successReports);
            workReport.setStatus(COMPLETED).setWorkContext(workContext);
        } else {
            workReport.setStatus(FAILED).setWorkContext(workContext);
        }

        return workReport;
    }

    protected MultipleWorkReport withFastExceptionallyResult(List<WorkReport> reports, WorkContext workContext) {
        MultipleWorkReport workReport = new MultipleWorkReport();
        if (Checker.BeEmpty(reports)) {
            workReport.setStatus(FAILED).setWorkContext(workContext);
            return workReport;
        }
        WorkReport report = reports.stream().filter(r -> Checker.BeNotNull(r.getError())).findFirst().orElse(null);
        if (Checker.BeNotNull(report)) {
            Throwable throwable = report.getError();
            if (throwable instanceof WorkFlowException) {
                throw (WorkFlowException) throwable;
            }
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            throw new WorkFlowException(workReport.getError());
        } else {
            workReport.setStatus(COMPLETED).setWorkContext(workContext);
        }
        return workReport;
    }

    protected MultipleWorkReport withFastAllResult(List<WorkReport> reports, WorkContext workContext) {
        assertReportNotEmpty(reports);
        MultipleWorkReport workReport = new MultipleWorkReport();
        workReport.addAllReports(reports);
        workReport.setStatus(WorkStatus.COMPLETED).setWorkContext(workContext);
        return workReport;
    }

    private void assertReportNotEmpty(List<WorkReport> reports) {
        if (Checker.BeEmpty(reports)) {
            throw new WorkFlowException("Work reports is empty!");
        }
    }


    private void mappedReport(WorkReport report) {
        if (report instanceof MultipleWorkReport) {
            MultipleWorkReport multipleWorkReport = (MultipleWorkReport) report;
            executedReportMap.put(multipleWorkReport.getWorkName(), report);
            mappedReports(((MultipleWorkReport) report).getReports());
        } else if (report instanceof DefaultWorkReport) {
            executedReportMap.put(report.getWorkName(), report);
        }
    }

    private void mappedReports(List<WorkReport> reports) {
        for (WorkReport report : reports) {
            mappedReport(report);
        }
    }

    protected void traceReport(WorkReport report) {
        if (this.beTrace) {
            executedReport.addReport(report);
            mappedReport(report);
        }

    }

    protected static Work wrapNamedPointWork(Work work) {
        if (work instanceof NamedPointWork) {
            return work;
        }
        NamedPointWork namedPointWork = new NamedPointWork(work);
        if (!(work instanceof WorkFlow)) {
            return namedPointWork;
        }
        return work;
    }

    protected WorkReport getReportByCache(Work work) {
        String name = "";
        if (work instanceof NamedPointWork) {
            NamedPointWork namedPointWork = (NamedPointWork) work;
            name = namedPointWork.getName();
        } else if (work instanceof AbstractWorkFlow) {
            AbstractWorkFlow abstractWorkFlow = (AbstractWorkFlow) work;
            name = abstractWorkFlow.getName();
        }
        if (Checker.BeNotEmpty(name) && executedReportMap.containsKey(name)) {
            return executedReportMap.get(name);
        }
        return null;
    }

    protected WorkReport getReportByWork(Work work, WorkContext context) {
        WorkReport report = getReportByCache(work);
        if (Checker.BeNull(report)) {
            return doSingleWork(work, context);
        }
        return report;

    }
}
