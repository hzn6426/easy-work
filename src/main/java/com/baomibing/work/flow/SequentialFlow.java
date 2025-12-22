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
import com.baomibing.work.report.*;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.*;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.baomibing.work.report.SequentialWorkReport.aNewSequentialWorkReport;

/**
 * A sequential flow executes a set of work units in sequence.
 *
 * @author zening (316279829@qq.com)
 */
public class SequentialFlow extends AbstractWorkFlow {

    private SequentialFlow(List<Work> works) {
        works.forEach(work -> workList.add(wrapNamedPointWork(work)));
        this.workList.add(new EndWork());
    }

    @Override
    public SequentialWorkReport execute() {
        return execute(Strings.EMPTY);
    }

    public SequentialWorkReport execute(String point) {
        return aNewSequentialWorkReport(executeInternal(point));
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
        WorkReport report = doSingleWork(work, workContext, point);
        multipleWorkReport.addReport(report);

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

    @Override
    public SequentialFlow then(Function<WorkReport, Work> fun) {
        thenFuns.add(fun);
        return this;
    }

    @Override
    public SequentialFlow then(Work work) {
        thenFuns.add(report -> work);
        return this;
    }


    public static SequentialFlow aNewSequentialFlow(Work... works) {
        return new SequentialFlow(Arrays.asList(works));
    }

    //dynamic add work
    public SequentialFlow addWork(Work work) {
        int index = Iterables.indexOf(workList, w -> w instanceof EndWork);
        workList.add(index, work);
        return this;
    }

    //dynamic add work at index
    public SequentialFlow addWork(int index, Work work) {
        int endWorkIndex = Iterables.indexOf(workList, w -> w instanceof EndWork);
        if (index < 0) {
            index = 0;
        }
        if (index > endWorkIndex) {
            index = endWorkIndex;
        }
        workList.add(index, work);
        return this;
    }

    public SequentialFlow named(String name) {
        this.name = name;
        return this;
    }

    public SequentialFlow policy(WorkExecutePolicy workExecutePolicy) {
        this.workExecutePolicy = workExecutePolicy;
        return this;
    }

    @Override
    public SequentialFlow context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public SequentialFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }
}
