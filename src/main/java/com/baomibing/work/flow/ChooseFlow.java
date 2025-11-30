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
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkReport;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.baomibing.work.work.DefaultWorkReport.aNewWorkReport;

/**
 * A choose flow is defined by as follows:
 *
 *  <ul>
 *      <li>The works to execute first in sequence order</li>
 *      <li>multi choose when predicate for the conditional logic</li>
 *      <li>for every choose when the work to execute if the predicate is satisfied</li>
 *  </ul>
 *
 * @author zening (316279829@qq.com)
 */
public class ChooseFlow extends AbstractWorkFlow {

    private final List<WhenWork>  whenWorks;
    private final List<Work> works;

    @Override
    public WorkReport execute(WorkContext context) {
        try {
            WorkReport report = doExecute(works, context);
            for (WhenWork whenWork : whenWorks) {
                if (whenWork.predicate.apply(report)) {
                    if (Checker.BeNull(whenWork.getWork())) {
                        report = aNewWorkReport();
                    } else {
                        report = doExecute(Lists.newArrayList(whenWork.getWork()), context);
                    }
                    break;
                }
            }

            return doThenWork(report);
        } finally {
            doLastWork();
        }
    }

    @Override
    public WorkReport execute() {
        return doDefaultExecute(this);
    }

    private  ChooseFlow(List<Work> theWorks, List<WhenWork> whenWorks) {
        this.works = theWorks;
        this.whenWorks = whenWorks;
    }

    public static BuildSteps aNewChooseFlow(Work... works) {
        return new BuildSteps(works);
    }

    public interface ChooseWhen {
        ChooseWhen chooseWhen(WorkReportPredicate thePredicate, Work work);
        ChooseFlow otherWise(Work work);
    }

    @AllArgsConstructor
    @Getter
    private static class WhenWork {
        private WorkReportPredicate predicate;
        private Work work;
    }

    public static class  BuildSteps implements ChooseWhen {


        private final List<WhenWork> innerWhenWorks = Lists.newArrayList();
        private final List<Work> innerWorks;

        public BuildSteps(Work... theWorks) {
            innerWorks = Arrays.asList(theWorks);
        }

        @Override
        public ChooseWhen chooseWhen(WorkReportPredicate thePredicate, Work work) {
            innerWhenWorks.add(new WhenWork(thePredicate, work));
            return this;
        }

        @Override
        public ChooseFlow otherWise(Work work) {
            innerWhenWorks.add(new WhenWork(WorkReportPredicate.ALWAYS_TRUE, work));
            return new ChooseFlow(innerWorks,innerWhenWorks);
        }
    }




}
