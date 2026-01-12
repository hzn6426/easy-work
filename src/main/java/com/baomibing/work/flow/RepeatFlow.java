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
import com.baomibing.work.predicate.TimesPredicate;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.report.MultipleWorkReport;
import com.baomibing.work.report.RepeatWorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkExecutePolicy;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.work.WorkStatus;
import java.util.function.Function;

import static com.baomibing.work.report.RepeatWorkReport.aNewRepeatWorkReport;

/**
 * A repeat flow executes a work repeatedly until its report satisfies a given predicate.
 *
 * @author zening (316279829@qq.com)
 */
public class RepeatFlow extends AbstractWorkFlow {
    //If the predicate is true, stop the repeat loop
    private final WorkReportPredicate workReportPredicate;
    //default peek model for the queue, when execute then block use poll mode
    private boolean bePoll = false;

    private RepeatFlow(WorkReportPredicate predicate, Work theWork) {
        this.workReportPredicate = predicate;
        workList.add(wrapNamedPointWork(theWork));
    }

    @Override
    public RepeatWorkReport execute() {
        return execute(Strings.EMPTY);
    }

    @Override
    public RepeatWorkReport execute(String point) {
        return aNewRepeatWorkReport(executeInternal(point));
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

        WorkContext workContext = getDefaultWorkContext();

        Work work;
        if (bePoll) {
            work = queue.poll();
        } else {
            work = queue.peek();
        }

        //cache the result
        WorkReport report = doSingleWork(work, workContext, point);
        multipleWorkReport.addReport(report);

        boolean beWorkFlow = work instanceof WorkFlow;
        boolean beStopped = beStopped();

        if (beWorkFlow) {
            if (!beStopped) {
                if ( workReportPredicate.apply(report)) {
                    return;
                }
            }
        } else {
            if ( workReportPredicate.apply(report)) {
                return;
            }
        }

        if (beStopped) {
            if (beWorkFlow) {
                if (bePoll) {
                    queue.offerFirst(work);
                }
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
    public RepeatFlow then(Function<WorkReport, Work> fun) {
        thenFuns.add(fun);
        return this;
    }

    @Override
    public RepeatFlow then(Work work) {
        thenFuns.add(report -> work);
        return this;
    }

    public RepeatFlow named(String name) {
        this.name = name;
        return this;
    }

    public RepeatFlow policy(WorkExecutePolicy workExecutePolicy) {
        this.workExecutePolicy = workExecutePolicy;
        return this;
    }

    @Override
    public RepeatFlow context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public RepeatFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }

    public static BuildSteps aNewRepeatFlow(Work theWork) {
        return new BuildSteps(theWork);
    }

    public interface UntilStep {
        RepeatFlow until(WorkReportPredicate predicate);
        RepeatFlow times(int times);
    }

    public static class  BuildSteps implements UntilStep {

        private final Work innerWork;

        public BuildSteps(Work theWork) {
            this.innerWork = theWork;
        }

        @Override
        public RepeatFlow until(WorkReportPredicate predicate) {
            return new RepeatFlow(predicate, innerWork);
        }

        @Override
        public RepeatFlow times(int times) {
            return until(TimesPredicate.times(times));
        }
    }
}
