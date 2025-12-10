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
import com.baomibing.work.report.ChooseWorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkExecutePolicy;
import com.baomibing.work.report.WorkReport;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static com.baomibing.work.report.ChooseWorkReport.aNewChooseWorkReport;
import static com.baomibing.work.report.DefaultWorkReport.aNewWorkReport;

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
    private final Work work;
    private final Work otherWiseWork;
    private boolean shortLogic = Boolean.TRUE;

    @Override
    public ChooseWorkReport execute() {
        WorkContext context = getDefaultWorkContext();

        WorkReport report = doSingleWork(work, context);
        traceReport(report);

        List<WorkReport> reports = Lists.newArrayList();
        WorkReport executeReport;
        boolean beExecute = false;
        for (WhenWork whenWork : whenWorks) {
            if (whenWork.predicate.apply(report)) {
                beExecute = true;
                if (Checker.BeNull(whenWork.getWork())) {
                    executeReport = aNewWorkReport();
                } else {
                    executeReport = doSingleWork(whenWork.getWork(), context);
                }
                reports.add(executeReport);
                if (shortLogic) {
                    break;
                }
            }
        }
        if (!beExecute) {
            if (Checker.BeNotNull(otherWiseWork)) {
                executeReport = doSingleWork(otherWiseWork, context);
                reports.add(executeReport);
            }
        }
        traceReport(aNewChooseWorkReport().setWorkName(name).addAllReports(reports));
        return aNewChooseWorkReport(getPolicyReport(reports, context));
    }

    private  ChooseFlow(Work theWorks, List<WhenWork> whenWorks, Work otherWiseWork) {
        this.work = theWorks;
        this.whenWorks = whenWorks;
        this.otherWiseWork = otherWiseWork;
    }

    public ChooseFlow witShortLogic(boolean shortLogic) {
        this.shortLogic = shortLogic;
        return this;
    }

    public ChooseFlow named(String name) {
        this.name = name;
        return this;
    }

    public ChooseFlow policy(WorkExecutePolicy workExecutePolicy) {
        this.workExecutePolicy = workExecutePolicy;
        return this;
    }

    @Override
    public ChooseFlow context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public ChooseFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }

    public static BuildSteps aNewChooseFlow(Work work) {
        return new BuildSteps(work);
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
        private final Work innerWork;

        public BuildSteps(Work theWork) {
            this.innerWork = wrapNamedPointWork(theWork);
        }

        @Override
        public ChooseWhen chooseWhen(WorkReportPredicate thePredicate, Work work) {
            innerWhenWorks.add(new WhenWork(thePredicate, wrapNamedPointWork(work)));
            return this;
        }

        @Override
        public ChooseFlow otherWise(Work work) {
            return new ChooseFlow(innerWork,innerWhenWorks, wrapNamedPointWork(work));
        }
    }




}
