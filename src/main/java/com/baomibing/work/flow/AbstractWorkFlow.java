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
import com.baomibing.work.step.LastStep;
import com.baomibing.work.step.ThenStep;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.*;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.baomibing.work.work.WorkStatus.COMPLETED;
import static com.baomibing.work.work.WorkStatus.FAILED;

/**
 * An abstract workflow that implements basic process execution and universal process operations
 *
 * @author zening (316279829@qq.com)
 */
public abstract class AbstractWorkFlow implements WorkFlow, ThenStep, LastStep {
    @Getter
    protected String name;

    protected WorkExecutePolicy workExecutePolicy = WorkExecutePolicy.FAST_FAIL_EXCEPTION;
    @Getter
    protected WorkContext workContext = null;
    @Getter
    protected Function<WorkReport, Work> thenFun;
    @Getter
    protected List<Work> thenWorks = Lists.newArrayList();
    @Getter
    protected List<Work> lastWorks = Lists.newArrayList();

    public abstract WorkReport execute();

    @Override
    public WorkReport execute(WorkContext context) {
        this.workContext = context;
        try {
            WorkReport report = execute();
            return doThenWork(report);
        } finally {
            doLastWork();
        }
    }

    @Override
    public AbstractWorkFlow then(Function<WorkReport, Work> fun) {
        this.thenFun = fun;
        return this;
    }

    @Override
    public AbstractWorkFlow then(Work... works) {
         this.thenWorks.addAll(Arrays.asList(works));
         return this;
    }

    @Override
    public AbstractWorkFlow lastly(Work... works) {
        this.lastWorks.addAll(Arrays.asList(works));
        return this;
    }

    protected WorkReport doThenWork(WorkReport workReport) {
        if (Checker.BeNull(thenFun)) {
            return workReport;
        }
        Work flow = thenFun.apply(workReport);
        if (Checker.BeNotNull(flow)) {
            thenWorks.add(flow);
        }
        if (Checker.BeNotEmpty(thenWorks)) {
            return doExecute(thenWorks, workContext);
        }
        return workReport;
    }

    protected void doLastWork() {
        if (Checker.BeNotEmpty(lastWorks)) {
            doExecute(lastWorks, workContext);
        }
    }

    protected WorkReport doSingleWorkExceptionally(Work work, WorkContext context) {
        WorkReport report = doSingleWork(work, context);
        Throwable e = report.getError();
        if (Checker.BeNotNull(e)) {
            if (e instanceof WorkFlowException) {
                throw (WorkFlowException) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new WorkFlowException(report.getError());
        }
        return report;
    }

    protected WorkReport doSingleWork(Work work, WorkContext context) {
        WorkReport workReport = new DefaultWorkReport();
        try {
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
                    ((DefaultWorkReport) workReport).setError(null).setWorkContext(context).setResult(object).setStatus(WorkStatus.COMPLETED);
                }
            }
        } catch (Exception e) {
            ((DefaultWorkReport)workReport).setError(e).setWorkContext(context).setResult(null).setStatus(FAILED);
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

    protected WorkReport doExecute(List<Work> works, WorkContext context) {
        WorkReport workReport = new DefaultWorkReport();
        for (Work work : works) {
            workReport = doSingleWork(work, context);
            if (beBreak(workReport)) {
                break;
            }

        }
        return workReport;
    }
}
