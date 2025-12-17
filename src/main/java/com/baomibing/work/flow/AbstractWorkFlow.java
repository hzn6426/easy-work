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

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.exception.WorkFlowException;
import com.baomibing.work.report.*;
import com.baomibing.work.step.LastStep;
import com.baomibing.work.step.ThenStep;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.*;
import com.google.common.collect.Iterables;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomibing.work.report.SequentialWorkReport.aNewSequentialWorkReport;
import static com.baomibing.work.work.WorkStatus.*;

/**
 * An abstract workflow that implements basic process execution and universal process operations
 *
 * @author zening (316279829@qq.com)
 */
public abstract class AbstractWorkFlow implements  ThenStep, LastStep, PointWorkFlow {
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

    //executed work flow report
    @Getter
    protected final MultipleWorkReport executedReport = new MultipleWorkReport();
    //trace support for executed workflow which mapping by name-workReport
    @Getter
    protected final Map<String, WorkReport> executedReportMap = new HashMap<>();

    // cache the original works in work flow
    protected final List<Work> workList = new ArrayList<>();
    // cache the point of work
    protected Work pointWork;
    // works in queue which support the point
    protected LinkedList<Work> queue;
    // executed work report of the work flow
    protected MultipleWorkReport  multipleWorkReport = aNewSequentialWorkReport();

    public abstract MultipleWorkReport execute();
    public abstract MultipleWorkReport execute(String point);
    public abstract void doExecute(String point);
    public abstract MultipleWorkReport executeThen(MultipleWorkReport workReport, String point);
    public abstract void locate2CurrentWork();


    @Override
    public MultipleWorkReport execute(WorkContext context) {
        this.workContext = context;
        try {
            return execute();
        } finally {
            doLastWork();
        }
    }

    @Override
    public WorkReport execute(WorkContext context, String point) {
        this.workContext = context;
        try {
            return execute(point);
        } finally {
            doLastWork();
        }
    }

    protected MultipleWorkReport executeInternal(String point) {
        multipleWorkReport.setWorkName(name);
        locate2CurrentWork();
        recoveryWorkReport(multipleWorkReport);

        doExecute(point);
        traceReport(multipleWorkReport);

        MultipleWorkReport result =  getPolicyReport(multipleWorkReport.getReports(), getDefaultWorkContext());
        MultipleWorkReport thenResult =  executeThen(result, point);
        traceReport(thenResult);
        return thenResult;
    }

    protected MultipleWorkReport executeThenInternal(MultipleWorkReport workReport, String point) {
        if (workReport.getStatus() == WorkStatus.STOPPED) {
            return workReport;
        }
        List<Work> works = new ArrayList<>();

        if (Checker.BeNotEmpty(thenFuns)) {
            for (Function<WorkReport, Work> fun : thenFuns) {
                works.add(wrapNamedPointWork(fun.apply(workReport)));
            }
            thenFuns.clear();
        }
        if (Checker.BeEmpty(works)) {
            return workReport;
        }

        this.workList.addAll(works);
        if (pointWork == null) {
            pointWork = works.get(0);
            return execute(point);
        }
        return workReport;
    }


    @Override
    public AbstractWorkFlow lastly(Work work) {
        this.lastWork = work;
        return this;
    }

    protected void doLastWork() {
        if (Checker.BeNotNull(lastWork)) {
            WorkReport report = doSingleWork(lastWork, workContext, Strings.EMPTY);
            traceReport(report);
        }
    }


    protected boolean beThePoint(Work work, String point) {
        if (Checker.BeNotEmpty(point) && work instanceof NamedPointWork) {
            NamedPointWork pointWork = (NamedPointWork)work;
            return Checker.BeNotEmpty(pointWork.getPoint()) && pointWork.getPoint().equals(point);
        }
        return false;
    }


    protected void locate2CurrentWorkInternal() {
        queue = new LinkedList<>();
        if (Checker.BeNull(this.pointWork)) {
            queue.addAll(workList);
            return;
        }
        String currentWorkName = getNameOfWork(pointWork);
        int index = Iterables.indexOf(workList, w -> currentWorkName.equals(getNameOfWork(w)));
        queue.addAll(workList.subList(index, workList.size()));
        pointWork = null;
    }

    private WorkReport doSingleWorkInternal(Work work, WorkContext context, String point) {
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
        if (work instanceof PointWorkFlow) {
            PointWorkFlow workFlow = (PointWorkFlow) work;
            workReport = workFlow.execute(context, point);
        } else {
            Object object = work.execute(context);
                workReport = new DefaultWorkReport().setError(null).setWorkContext(context).setResult(object).setStatus(WorkStatus.COMPLETED);
                if (work instanceof NamedPointWork) {
                    ((DefaultWorkReport) workReport).setWorkName(((NamedPointWork)work).getName());
                }

            if (Checker.BeNotEmpty(point) && beThePoint(work, point)) {
                ((DefaultWorkReport) workReport).setStoppedStatus(workReport.getStatus()).setStatus(STOPPED);
            }
        }
        return workReport;
    }

    protected WorkReport doSingleWork(Work work, WorkContext context, String point) {
        WorkReport workReport ;
        try {
            workReport = doSingleWorkInternal(work, context, point);
        } catch (Exception e) {
            workReport = new DefaultWorkReport().setError(e).setWorkContext(context).setResult(null).setStatus(FAILED);
            if (work instanceof NamedPointWork) {
                ((DefaultWorkReport) workReport).setWorkName(((NamedPointWork)work).getName());
            }
            if (Checker.BeNotEmpty(point) && beThePoint(work, point)) {
                ((DefaultWorkReport) workReport).setStoppedStatus(workReport.getStatus()).setStatus(WorkStatus.STOPPED);
            }
        }
        return workReport;
    }

    protected boolean beStopped() {
        if (multipleWorkReport.getStatus() == WorkStatus.STOPPED) {
            return true;
        }
        return false;
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

    protected String getNameOfWork(Work work) {
        String name = "";
        if (work instanceof NamedWork) {
            NamedWork namedWork = (NamedWork) work;
            name = namedWork.getName();
        } else if (work instanceof AbstractWorkFlow) {
            AbstractWorkFlow abstractWorkFlow = (AbstractWorkFlow) work;
            name = abstractWorkFlow.getName();
        }
        return name;
    }


    protected void recoveryWorkReport(WorkReport report) {
        if (report instanceof DefaultWorkReport) {
            DefaultWorkReport defaultWorkReport = (DefaultWorkReport) report;
            if (defaultWorkReport.getStatus() == STOPPED) {
                defaultWorkReport.setStatus(defaultWorkReport.getStoppedStatus()).setStoppedStatus(null);
            }
            return;
        }
        MultipleWorkReport multipleWorkReport = (MultipleWorkReport) report;
        List<WorkReport> workReports = multipleWorkReport.getReports();
        multipleWorkReport.setStatus(null);

        if (Checker.BeNotEmpty(workReports)) {
            //parallel reports not ordered
            if (report instanceof ParallelWorkReport) {
                for (WorkReport workReport : workReports) {
                    recoveryWorkReport(workReport);
                }
            } else {
                WorkReport workReport = workReports.get(workReports.size() - 1);
                recoveryWorkReport(workReport);
            }

        }
    }

}
