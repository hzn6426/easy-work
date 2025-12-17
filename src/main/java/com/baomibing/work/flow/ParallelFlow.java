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
import com.baomibing.work.report.MultipleWorkReport;
import com.baomibing.work.report.ParallelWorkReport;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.*;
import com.google.common.collect.Lists;
import io.foldright.cffu2.Cffu;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.MCffu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.baomibing.work.report.ParallelWorkReport.aNewParallelWorkReport;
import static com.baomibing.work.work.NamedParallelWork.aNewParallelWork;
import static com.baomibing.work.work.WorkStatus.FAILED;

/**
 * A parallel flow executes a set of work units in parallel. A {@link ParallelFlow}
 * requires a {@link ExecutorService} to execute work units in parallel using multiple
 * threads.
 *
 * @author zening (316279829@qq.com)
 */
public class ParallelFlow extends AbstractWorkFlow {
    //cffu factory {@link https://github.com/foldright/cffu}
    private static CffuFactory cffuFactory = null;
    //thread pool
    private static ExecutorService  executor = null;
    //shutdown thread pool when setting true
    private boolean autoShutdown = false;
    //join timeout
    private int timeoutInSeconds = 60;

    private ParallelFlow(List<Work> works) {
        List<Work> theWorks  = new ArrayList<>();

        for (Work work : works) {
            theWorks.add(wrapNamedPointWork(work));
        }

        workList.add(aNewParallelWork(theWorks));
        workList.add(new EndWork());

        if (executor == null) {
            executor = initExecutor();
        }
        if (cffuFactory == null) {
            cffuFactory = CffuFactory.builder(executor).build();
        }
    }

    @Override
    public ParallelWorkReport execute() {
        return execute(Strings.EMPTY);
    }

    @Override
    public ParallelWorkReport execute(String point) {
        try {
            return aNewParallelWorkReport(executeInternal(point));
        } finally {
            shutdown();
        }
    }

    @Override
    public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
        return executeThenInternal(workReport, point);
    }

    @Override
    public void doExecute(String point) {
        if (beStopped()) {
            return;
        }

        WorkContext workContext = getDefaultWorkContext();
        Work work = queue.poll();
        if (work instanceof EndWork) {
            return;
        }

        //cache the result
        WorkReport report;

        if (work instanceof NamedParallelWork) {
            report = doParallelWork((NamedParallelWork) work);
        } else {
            report = doSingleWork(work, workContext, point);
            multipleWorkReport.addReport(report);
        }

        if (beStopped()) {
            if (work instanceof WorkFlow) {
                queue.offerFirst(work);
            }
            pointWork = queue.peek();
            return;
        }

        if (beBreak(report)) {
            return;
        }

        //execute to next
        if (report.getStatus() != WorkStatus.STOPPED) {
            doExecute(point);
        }

    }

    @Override
    public void locate2CurrentWork() {
        locate2CurrentWorkInternal();
    }

    private MultipleWorkReport doParallelWork(NamedParallelWork wrapper) {
        WorkContext context = getDefaultWorkContext();

        List<Work> works = wrapper.getSupplierWorks();
        List<Supplier<WorkReport>> supplierList  = new ArrayList<>();
        works.forEach(work -> supplierList.add(() ->  doSingleWork(work, context, Strings.EMPTY)));

        if (WorkExecutePolicy.FAST_SUCCESS == workExecutePolicy) {
            Cffu<WorkReport> report = cffuFactory.iterableOps().mSupplyAnySuccessAsync(supplierList);
            multipleWorkReport = withFastSuccessResult(report, context);
        } else if (WorkExecutePolicy.FAST_FAIL == workExecutePolicy ) {
            MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
            multipleWorkReport = withFastFailResult(reports, context);
        } else if (WorkExecutePolicy.FAST_FAIL_EXCEPTION == workExecutePolicy) {
            MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
            multipleWorkReport = withFastFailExceptionResult(reports, context);
        } else if (WorkExecutePolicy.FAST_ALL == workExecutePolicy) {
            MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAsync(supplierList);
            multipleWorkReport = withFastAllResult(reports, context);
        } else if (WorkExecutePolicy.FAST_EXCEPTION == workExecutePolicy) {
            MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyFailFastAsync(supplierList);
            multipleWorkReport = withFastExceptionallyResult(reports,  context);
        } else if (WorkExecutePolicy.FAST_ALL_SUCCESS == workExecutePolicy) {
            MCffu<WorkReport, List<WorkReport>> reports = cffuFactory.iterableOps().mSupplyAllSuccessAsync(null, supplierList);
            multipleWorkReport = withFastAllSuccessResult(reports, context);
        } else {
            throw new RuntimeException("Not support work execute policy:" + workExecutePolicy);
        }
        multipleWorkReport.setWorkName(name);
        return multipleWorkReport;
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

    @Override
    public ParallelFlow then(Function<WorkReport, Work> fun) {
        thenFuns.add(fun);
        return this;
    }

    @Override
    public ParallelFlow then(Work work) {
        thenFuns.add(report -> work);
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
