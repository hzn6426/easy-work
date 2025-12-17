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
import com.baomibing.work.report.MultipleWorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.*;
import com.baomibing.work.report.WorkReport;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

import static com.baomibing.work.report.ChooseWorkReport.aNewChooseWorkReport;
import static com.baomibing.work.work.NamedDecideWork.aNewNamedDecideWork;
import static com.baomibing.work.work.NamedOtherWiseWork.aNewNamedOtherWiseWork;
import static com.baomibing.work.work.NamedWhenWork.aNewNamedWhenWork;

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

    private boolean shortLogic = Boolean.TRUE;
    //single choose when executed
    private boolean beExecutedWhen = Boolean.FALSE;
    //executing then block
    private boolean beExecuteThen = Boolean.FALSE;
    //the decide work report
    private WorkReport predicateWorkReport;

    private  ChooseFlow(Work theWork, List<WhenWork> whenWorks, Work otherWiseWork) {
        this.workList.add(aNewNamedDecideWork(wrapNamedPointWork(theWork)));
        whenWorks.forEach(whenWork -> workList.add(aNewNamedWhenWork(whenWork.getPredicate(), wrapNamedPointWork(whenWork.getWork()))));
        this.workList.add(aNewNamedOtherWiseWork(wrapNamedPointWork(otherWiseWork)));
        this.workList.add(new EndWork());
    }

    @Override
    public ChooseWorkReport execute() {
        return execute(Strings.EMPTY);
    }

    @Override
    public ChooseWorkReport execute(String point) {
        return aNewChooseWorkReport(executeInternal(point));
    }

    @Override
    public MultipleWorkReport executeThen(MultipleWorkReport workReport, String point) {
        if (workReport.getStatus() != WorkStatus.STOPPED) {
            if (Checker.BeNotEmpty(thenFuns) ) {
                if (pointWork == null) {
                    beExecuteThen = true;
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
        Work work = queue.poll();

        if (work instanceof EndWork) {
            return;
        }

        boolean beWorkFlow;
        //cache the result
        WorkReport report;
        if (work instanceof NamedWhenWork) {
            NamedWhenWork namedWhenWork = (NamedWhenWork) work;
            boolean beTrue = namedWhenWork.getPredicate().apply(predicateWorkReport);
            beWorkFlow = namedWhenWork.getWork() instanceof WorkFlow;
            if (beTrue) {
                beExecutedWhen = true;
                report = doSingleWork(namedWhenWork.getWork(), workContext, point);
            } else {
                doExecute(point);
                return;
            }
        } else if (work instanceof NamedDecideWork){
            NamedDecideWork namedDecideWork = (NamedDecideWork) work;
            beWorkFlow = namedDecideWork.getDecideWork() instanceof WorkFlow;
            report = doSingleWork(namedDecideWork.getDecideWork(), workContext, point);
            predicateWorkReport = report;
        } else if (work instanceof NamedOtherWiseWork){
            NamedOtherWiseWork namedOtherWiseWork = (NamedOtherWiseWork) work;
            beWorkFlow = namedOtherWiseWork.getWork() instanceof WorkFlow;
            if (!beExecutedWhen) {
                report = doSingleWork(namedOtherWiseWork.getWork(), workContext, point);
            } else {
                doExecute(point);
                return;
            }
        } else {
            beWorkFlow = work instanceof WorkFlow;
            report = doSingleWork(work, workContext, point);
        }

        multipleWorkReport.addReport(report);

        if (beStopped()) {
            if (beWorkFlow) {
                queue.offerFirst(work);
            }
            pointWork = queue.peek();
            return;
        }

        if (shortLogic && beExecutedWhen && !beExecuteThen) {
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

    @Override
    public ChooseFlow then(Function<WorkReport, Work> fun) {
        thenFuns.add(fun);
        return this;
    }

    @Override
    public ChooseFlow then(Work work) {
        thenFuns.add(report -> work);
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
