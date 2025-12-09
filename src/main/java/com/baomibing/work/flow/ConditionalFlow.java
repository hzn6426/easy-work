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
import com.baomibing.work.report.ConditionalWorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkExecutePolicy;
import com.baomibing.work.report.WorkReport;
import com.google.common.collect.Lists;

import java.util.List;

import static com.baomibing.work.report.ConditionalWorkReport.aNewConditionalWorkReport;
import static com.baomibing.work.report.DefaultWorkReport.aNewWorkReport;

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
    private final Work work;


    @Override
    public ConditionalWorkReport execute() {
        WorkContext context = getDefaultWorkContext();

        WorkReport report = doSingleWork(work, context);
        traceReport(report);

        List<WorkReport> reports = Lists.newArrayList();

        WorkReport executeReport;
        boolean beTrue = predicate.apply(report);
        if (beTrue) {
            executeReport = doSingleWork(trueWork, context);
        } else {
            if (Checker.BeNotNull(falseWork)) {
                executeReport = doSingleWork(falseWork, context);
            } else {
                executeReport = aNewWorkReport();
            }
        }
        reports.add(executeReport);
        traceReport(aNewConditionalWorkReport().setWorkName(name).addAllReports(reports));
        return aNewConditionalWorkReport(getPolicyReport(reports, context));
    }

    private ConditionalFlow(Work theWork, WorkReportPredicate thePredicate, Work trueWork, Work falseWork) {
        this.predicate = thePredicate;
        this.trueWork = wrapNamedPointWork(trueWork);
        this.falseWork = wrapNamedPointWork(falseWork);
        this.work = wrapNamedPointWork(theWork);
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

    public ConditionalFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }


    public static BuildSteps aNewConditionalFlow(Work work) {
        return new BuildSteps(work);
    }


    public interface WhenWork {
        ConditionalFlow when(WorkReportPredicate predicate, Work trueWork, Work falseWork);

        ConditionalFlow when(WorkReportPredicate predicate, Work trueWork);

    }

    public static class BuildSteps implements WhenWork {
        private final Work work;

        public BuildSteps(Work theWork) {
            this.work = theWork;
        }


        @Override
        public ConditionalFlow when(WorkReportPredicate predicate, Work trueWork, Work falseWork) {
            return new ConditionalFlow(work, predicate, trueWork, falseWork);
        }

        @Override
        public ConditionalFlow when(WorkReportPredicate predicate, Work trueWork) {
            return new ConditionalFlow(work, predicate, trueWork, null);
        }
    }

}
