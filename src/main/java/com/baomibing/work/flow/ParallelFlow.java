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
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.*;
import io.foldright.cffu2.Cffu;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.MCffu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.baomibing.work.work.WorkStatus.FAILED;
/**
 * A parallel flow executes a set of work units in parallel. A {@link ParallelFlow}
 * requires a {@link ExecutorService} to execute work units in parallel using multiple
 * threads.
 *
 * @author zening (316279829@qq.com)
 */
public class ParallelFlow extends AbstractWorkFlow {
    private static CffuFactory cffuFactory = null;
    private static ExecutorService  executor = null;
    private final List<Work> works  = new ArrayList<>();
    private boolean autoShutdown = false;
    private int timeoutInSeconds = 60;

    @Override
    public ParallelWorkReport execute() {
        WorkContext context = getDefaultWorkContext();
        try {
            List<Supplier<WorkReport>> supplierList  = new ArrayList<>();
            for (Work work : works) {
                supplierList.add(() ->  doSingleWork(work, context));
            }
            ParallelWorkReport workReport;
            if (WorkExecutePolicy.FAST_SUCCESS == workExecutePolicy) {
                Cffu<WorkReport> report = cffuFactory.iterableOps().mSupplyAnySuccessAsync(supplierList);
                workReport = withResult(report, context);
            } else if (WorkExecutePolicy.FAST_FAIL == workExecutePolicy ) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
                workReport = withFailResult(reports, context);
            } else if (WorkExecutePolicy.FAST_FAIL_EXCEPTION == workExecutePolicy) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
                workReport = withFailExceptionResult(reports, context);
            } else if (WorkExecutePolicy.FAST_ALL == workExecutePolicy) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
                workReport = withResult(reports, context);
            } else if (WorkExecutePolicy.FAST_EXCEPTION == workExecutePolicy) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyFailFastAsync(supplierList);
                workReport = withResultExceptionally(reports);
            } else if (WorkExecutePolicy.FAST_ALL_SUCCESS == workExecutePolicy) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAllSuccessAsync(null, supplierList);
                workReport = withSuccessResult(reports, context);
            } else {
                throw new RuntimeException("Not support work execute policy:" + workExecutePolicy);
            }
            return workReport;
        } finally {
            shutdown();
        }
    }

    public ParallelFlow named(String name) {
        this.name = name;
        return this;
    }

    public ParallelFlow policy(WorkExecutePolicy workExecutePolicy) {
        this.workExecutePolicy = workExecutePolicy;
        return this;
    }

    @Override
    public ParallelFlow context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    private ParallelWorkReport withResult(Cffu<WorkReport> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            WorkReport report = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport.addReport(report);
        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withResultExceptionally(MCffu<WorkReport, List<WorkReport>> cffu) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport.addAllReports(reports);
        }  catch (Exception e) {
            if (e instanceof WorkFlowException) {
                throw (WorkFlowException) e;
            }
            throw (RuntimeException) e;
        }
        if (Checker.BeNotNull(workReport.getError())) {
            Throwable throwable = workReport.getError();
            if (throwable instanceof WorkFlowException) {
                throw (WorkFlowException) throwable;
            }
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            }
            throw new WorkFlowException(workReport.getError());
        }
        return workReport;
    }

    private ParallelWorkReport withSuccessResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            if (reports != null) {
                List<WorkReport> successReports = reports.stream().filter(r -> WorkStatus.COMPLETED.equals(r.getStatus())).collect(Collectors.toList());
                if (Checker.BeNotEmpty(successReports)) {
                    workReport.addAllReports(successReports);
                }
            }

        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFailExceptionResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            if (reports != null) {
                WorkReport report = reports.stream().filter(r -> WorkStatus.FAILED.equals(r.getStatus()) && Checker.BeNotNull(r.getError())).findFirst().orElse(null);
                if (report != null) {
                    workReport.addReport(report);
                    return  workReport;
                }
                workReport.addAllReports(reports);
            }
        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFailResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            if (reports != null) {
                WorkReport report = reports.stream().filter(r -> WorkStatus.FAILED.equals(r.getStatus())).findFirst().orElse(null);
                if (report != null) {
                    workReport.addReport(report);
                    return  workReport;
                }
                workReport.addAllReports(reports);
            }
        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport.addAllReports(reports);
        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ThreadPoolExecutor initExecutor() {
        final int corePoolSize = 10;
        final int maxPoolSize = 20;
        final int queueCapacity = 50;
        final int keepAliveTime = 30;
        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity, true);
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
                queue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private ParallelFlow(List<Work> works) {
        this.works.addAll(works);
        if (executor == null) {
            executor = initExecutor();
        }
        if (cffuFactory == null) {
            cffuFactory = CffuFactory.builder(executor).build();
        }
    }

    public static ParallelFlow aNewParallelFlow(Work... works) {
        return new ParallelFlow(Arrays.asList(works));
    }

    public ParallelFlow withExecutor(ExecutorService theExecutor) {
        if (Checker.BeNotNull(theExecutor)) {
            executor = theExecutor;
            cffuFactory = CffuFactory.builder(executor).build();
        }
        return this;
    }

    public ParallelFlow withTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
        return this;
    }

    public ParallelFlow withAutoShutDown(boolean beAutoShutdown) {
        this.autoShutdown = beAutoShutdown;
        return this;
    }

    public void shutdown() {
        if (autoShutdown && executor != null) {
            executor.shutdown();
        }
    }
}
