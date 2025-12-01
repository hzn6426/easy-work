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
import com.baomibing.work.work.WorkExecutePolicy;
import com.baomibing.work.work.WorkReport;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

import static com.baomibing.work.work.DefaultWorkReport.aNewWorkReport;

/**
 * A conditional flow is defined by 4 artifacts:
 *
 *  <ul>
 *      <li>The works to execute first in sequence order</li>
 *      <li>A predicate for the conditional logic</li>
 *      <li>The work to execute if the predicate is satisfied</li>
 *      <li>The work to execute if the predicate is not satisfied (optional)</li>
 *  </ul>
 *
 * @author zening (316279829@qq.com)
 */
public class ConditionalFlow extends AbstractWorkFlow {
    private final WorkReportPredicate predicate;
    private final Work trueWork;
    private final Work falseWork;
    private final List<Work> works;


    @Override
    public WorkReport execute() {
        WorkContext context = getDefaultWorkContext();
        WorkReport report = doExecute(works, context);
        boolean beTrue = predicate.apply(report);
        if (beTrue) {
            report = doExecute(Lists.newArrayList(trueWork), context);
        } else {
            if (Checker.BeNotNull(falseWork)) {
                report = doExecute(Lists.newArrayList(falseWork), context);
            } else {
                report = aNewWorkReport();
            }
        }
        return doThenWork(report);
    }


    private ConditionalFlow(List<Work> theWorks, WorkReportPredicate thePredicate, Work trueWork, Work falseWork) {
        this.predicate = thePredicate;
        this.trueWork = trueWork;
        this.falseWork = falseWork;
        this.works = theWorks;
    }

    public ConditionalFlow named(String name) {
        this.name = name;
        return this;
    }

    public ConditionalFlow policy(WorkExecutePolicy workExecutePolicy) {
        this.workExecutePolicy = workExecutePolicy;
        return this;
    }

    @Override
    public ConditionalFlow context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }


    public static BuildSteps aNewConditionalFlow(Work... works) {
        return new BuildSteps(works);
    }


    public interface WhenWork {
        ConditionalFlow when(WorkReportPredicate predicate, Work trueWork, Work falseWork);

        ConditionalFlow when(WorkReportPredicate predicate, Work trueWork);

    }

    public static class BuildSteps implements WhenWork {
        private final List<Work> works;

        public BuildSteps(Work... theWorks) {
            works = Arrays.asList(theWorks);
        }


        @Override
        public ConditionalFlow when(WorkReportPredicate predicate, Work trueWork, Work falseWork) {
            return new ConditionalFlow(works, predicate, trueWork, falseWork);
        }

        @Override
        public ConditionalFlow when(WorkReportPredicate predicate, Work trueWork) {
            return new ConditionalFlow(works, predicate, trueWork, null);
        }
    }

}
