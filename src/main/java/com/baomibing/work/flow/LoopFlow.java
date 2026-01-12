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
package com.baomibing.work.flow;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.report.LoopIndexWorkReport;
import com.baomibing.work.report.LoopWorkReport;
import com.baomibing.work.report.MultipleWorkReport;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.*;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static com.baomibing.work.report.LoopWorkReport.aNewLoopWorkReport;

/**
 * A loop flow is defined by as follows:
 *
 *  <ul>
 *      <li>works execute in infinite loop</li>
 *      <li>when break predicate apply, break the loop</li>
 *      <li>when continue predicate apply, skip current and execute next loop</li>
 *  </ul>
 *
 * @author zening (316279829@qq.com)
 */
public class LoopFlow extends AbstractWorkFlow {

    //work report helper which cache the index and length of the loop
    private final LoopIndexWorkReport indexReport = new LoopIndexWorkReport();
    //if the predicate is true, break the loop
    private WorkReportPredicate breakPredicate;
    //if the predicate is true, skip current work and continue the loop
    private WorkReportPredicate continuePredicate;
    //loop index
    private int index = 0;
    //queue for poll operation, when execute `then` block, the poll is true
    private boolean bePoll = false;

    private LoopFlow(List<Work> works) {
        works.forEach(work -> workList.add(wrapNamedPointWork(work)));
        indexReport.setLength(works.size());
    }

    @Override
    public LoopWorkReport execute() {
        return execute(Strings.EMPTY);
    }

    @Override
    public LoopWorkReport execute(String point) {
        return aNewLoopWorkReport(executeInternal(point));
    }

    @Override
    public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
        if (workReport.getStatus() != WorkStatus.STOPPED) {
            if (Checker.BeNotEmpty(thenFuns) ) {
                if (pointWork == null) {
                    bePoll = true;
                }
            }
        }
        return executeThenInternal(workReport, point);
    }

    @Override
    public void doExecute(String point) {
        if (beStopped()) {
            return;
        }

        indexReport.setIndex(index);

        if (!bePoll && Checker.BeNotNull(breakPredicate)) {
            if (breakPredicate.apply(indexReport)) {
                return;
            }
        }
        if (!bePoll && Checker.BeNotNull(continuePredicate)) {
            if (continuePredicate.apply(indexReport)) {
                Work work = queue.poll();
                if (bePoll == false) {
                    queue.offer(work);
                }
            }
        }

        WorkContext workContext = getDefaultWorkContext();
        //poll the work and add the last, infinite loop
        Work work = queue.poll();
        if (bePoll == false) {
            queue.offer(work);
        }

        //cache the result
        WorkReport report = doSingleWork(work, workContext, point);

        multipleWorkReport.addReport(report);
        indexReport.with(report);
        index++;


        if (beStopped()) {
            if (work instanceof WorkFlow) {
                if (bePoll == false) {
                    queue.removeLast();
                }
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
        if (Checker.BeNull(this.pointWork)) {
            queue = new LinkedList<>();
            queue.addAll(workList);
            return;
        }
        if (bePoll) {
            queue = new LinkedList<>();
            String currentWorkName = getNameOfWork(pointWork);
            int index = Iterables.indexOf(workList, w -> currentWorkName.equals(getNameOfWork(w)));
            queue.addAll(workList.subList(index, workList.size()));
        }
        pointWork = null;
    }

    public LoopFlow named(String name) {
        this.name = name;
        return this;
    }

    public LoopFlow policy(WorkExecutePolicy workExecutePolicy) {
        this.workExecutePolicy = workExecutePolicy;
        return this;
    }

    @Override
    public LoopFlow context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public LoopFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }

    @Override
    public LoopFlow then(Function<WorkReport, Work> fun) {
        thenFuns.add(fun);
        return this;
    }

    @Override
    public LoopFlow then(Work work) {
        thenFuns.add(report -> work);
        return this;
    }

    //dynamic add work
    public LoopFlow addWork(Work work) {
        workList.add(work);
        return this;
    }

    //dynamic add work at index
    public LoopFlow addWork(int index, Work work) {
        int enIndex = workList.size() - 1;
        if (index < 0) {
            index = 0;
        }
        if (index > enIndex) {
            index = enIndex;
        }
        workList.add(index, work);
        return this;
    }

    public static LoopFlow aNewLoopFlow(Work... works) {
        return new LoopFlow(Arrays.asList(works));
    }

    public LoopFlow withBreakPredicate(WorkReportPredicate breakPredicate) {
        this.breakPredicate = breakPredicate;
        return this;
    }

    public LoopFlow withContinuePredicate(WorkReportPredicate continuePredicate) {
        this.continuePredicate = continuePredicate;
        return this;
    }


}
