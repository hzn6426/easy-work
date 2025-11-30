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
import com.baomibing.work.predicate.TimesPredicate;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkReport;
/**
 * A repeat flow executes a work repeatedly until its report satisfies a given predicate.
 *
 * @author zening (316279829@qq.com)
 */
public class RepeatFlow extends AbstractWorkFlow {

    private final WorkReportPredicate workReportPredicate;
    private final Work work;

    @Override
    public WorkReport execute(WorkContext context) {
        WorkReport workReport;
        try {
            do {
                workReport = doSingleWork(work, context);
                if (beBreak(workReport)) {
                    break;
                }
            } while (!workReportPredicate.apply(workReport));
            return doThenWork(workReport);
        } finally {
            doLastWork();
        }
    }

    @Override
    public WorkReport execute() {
        return doDefaultExecute(this);
    }

    private RepeatFlow(WorkReportPredicate predicate, Work theWork) {
        this.workReportPredicate = predicate;
        this.work = theWork;
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
