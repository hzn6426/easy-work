/*
 * Copyright (c) 2025-2026, zening (316279828@qq.com).
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
 *
 */
package com.baomibing.work.report;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.work.Work;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.LoopFlow.aNewLoopFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

/**
 * An abstract execution result implementation that defaults to a single step execution process
 *
 * @author zening (316279829@qq.com)
 */
public abstract class AbstractWorkReport implements WorkReport {

    protected WorkContext workContext = new WorkContext();

    @Override
    public WorkReport context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    @Override
    public WorkReport execute(WorkContext context) {
        return this;
    }

    @Override
    public WorkReport thenExecute(Work... works) {
        return aNewSequentialFlow(works).context(workContext).execute();
    }

    @Override
    public WorkReport thenExecute(Function<WorkReport, Work> fn) {
        return aNewSequentialFlow(fn.apply(this)).context(workContext).execute();
    }

    @Override
    public WorkReport whenExecute(WorkReportPredicate predicate, Work work) {
        return aNewConditionalFlow(this).when(predicate, work).context(workContext).execute();
    }

    @Override
    public WorkReport whenExecute(WorkReportPredicate predicate, Work trueWork, Work falseWork) {
        return aNewConditionalFlow(this).when(predicate, trueWork, falseWork).context(workContext).execute();
    }

    @Override
    public WorkReport repatUtilExecute(WorkReportPredicate predicate, Work work) {
        return aNewRepeatFlow(work).until(predicate).context(workContext).execute();
    }

    @Override
    public WorkReport parallelExecute(ExecutorService service, Work... works) {
        return aNewParallelFlow(works).withExecutor(service).context(workContext).execute();
    }

    @Override
    public WorkReport parallelExecute(Work... works) {
        return aNewParallelFlow(works).context(workContext).execute();
    }

    @Override
    public WorkReport loopExecute(Work... works) {
        return aNewLoopFlow(works).context(workContext).execute();
    }

    @Override
    public WorkReport loopExecute(WorkReportPredicate breakPredicate, WorkReportPredicate continuePredicate, Work... works) {
        return aNewLoopFlow(works).withBreakPredicate(breakPredicate).withContinuePredicate(continuePredicate).context(workContext).execute();
    }
}
