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
import com.baomibing.work.report.ParallelWorkReport;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.*;
import com.google.common.collect.Lists;
import io.foldright.cffu2.Cffu;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.MCffu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static com.baomibing.work.report.ParallelWorkReport.aNewParallelWorkReport;
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
        ParallelWorkReport workReport;
        try {
            List<Supplier<WorkReport>> supplierList  = new ArrayList<>();

            works.forEach(work -> supplierList.add(() ->  doSingleWork(work, context)));

            if (WorkExecutePolicy.FAST_SUCCESS == workExecutePolicy) {
                Cffu<WorkReport> report = cffuFactory.iterableOps().mSupplyAnySuccessAsync(supplierList);
                workReport = withFastSuccessResult(report, context);
            } else if (WorkExecutePolicy.FAST_FAIL == workExecutePolicy ) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
                workReport = withFastFailResult(reports, context);
            } else if (WorkExecutePolicy.FAST_FAIL_EXCEPTION == workExecutePolicy) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
                workReport = withFastFailExceptionResult(reports, context);
            } else if (WorkExecutePolicy.FAST_ALL == workExecutePolicy) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
                workReport = withFastAllResult(reports, context);
            } else if (WorkExecutePolicy.FAST_EXCEPTION == workExecutePolicy) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyFailFastAsync(supplierList);
                workReport = withFastExceptionallyResult(reports,  context);
            } else if (WorkExecutePolicy.FAST_ALL_SUCCESS == workExecutePolicy) {
                MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAllSuccessAsync(null, supplierList);
                workReport = withFastAllSuccessResult(reports, context);
            } else {
                throw new RuntimeException("Not support work execute policy:" + workExecutePolicy);
            }
            workReport.setWorkName(name);
            traceReport(workReport);
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

    public ParallelFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }

    private ParallelWorkReport withFastFailResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastFailResult(reports, workContext));
        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastFailExceptionResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastFailExceptionResult(reports, workContext));
        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastSuccessResult(Cffu<WorkReport> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            WorkReport report = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastSuccessResult(Lists.newArrayList(report), workContext));

        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastAllResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastAllResult(reports, workContext));

        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }

    private ParallelWorkReport withFastAllSuccessResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport = new ParallelWorkReport();
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastAllResult(reports, workContext));
        }  catch (Exception e) {
            workReport.setError(e).setWorkContext(workContext).setStatus(FAILED);
        }
        return workReport;
    }


    private ParallelWorkReport withFastExceptionallyResult(MCffu<WorkReport, List<WorkReport>> cffu, WorkContext workContext) {
        ParallelWorkReport workReport;
        try {
            List<WorkReport> reports = cffu.join(timeoutInSeconds, TimeUnit.SECONDS);
            workReport = aNewParallelWorkReport(withFastExceptionallyResult(reports, workContext));
        }  catch (Exception e) {
            if (e instanceof WorkFlowException) {
                throw (WorkFlowException) e;
            }
            throw (RuntimeException) e;
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
        for (Work work : works) {
            this.works.add(wrapNamedPointWork(work));
        }
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
