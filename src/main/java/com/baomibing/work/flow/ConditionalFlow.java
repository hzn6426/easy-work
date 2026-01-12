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
import com.baomibing.work.report.ConditionalWorkReport;
import com.baomibing.work.report.MultipleWorkReport;
import com.baomibing.work.util.Strings;
import com.baomibing.work.work.*;
import com.baomibing.work.report.WorkReport;

import java.util.function.Function;

import static com.baomibing.work.report.ConditionalWorkReport.aNewConditionalWorkReport;
import static com.baomibing.work.work.NamedDecideWork.aNewNamedDecideWork;
import static com.baomibing.work.work.NamedConditionFalseWork.aNewNamedExecuteFalseWork;
import static com.baomibing.work.work.NamedConditionTrueWork.aNewNamedExecuteTrueWork;

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
    //The decide work report
    private WorkReport predicateWorkReport;

    private ConditionalFlow(Work theWork, WorkReportPredicate thePredicate, Work trueWork, Work falseWork) {
        this.predicate = thePredicate;
        this.workList.add(aNewNamedDecideWork(wrapNamedPointWork(theWork)));
        this.workList.add(aNewNamedExecuteTrueWork(wrapNamedPointWork(trueWork)));
        this.workList.add(aNewNamedExecuteFalseWork(wrapNamedPointWork(falseWork)));
        this.workList.add(new EndWork());
    }

    @Override
    public ConditionalWorkReport execute() {
        return execute(Strings.EMPTY);
    }

    @Override
    public ConditionalWorkReport execute(String point) {
        return aNewConditionalWorkReport(executeInternal(point));
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

        boolean beWorkFlow;
        //cache the result
        WorkReport report = null;
        if (work instanceof NamedConditionWork) {
            boolean beTrue = predicate.apply(predicateWorkReport);
            if (work instanceof NamedConditionTrueWork) {
                NamedConditionTrueWork namedExecuteTrueWork = (NamedConditionTrueWork) work;
                beWorkFlow = namedExecuteTrueWork.getWork() instanceof WorkFlow;
                if (beTrue) {
                    report = doSingleWork(namedExecuteTrueWork.getWork(), workContext, point);
                } else {
                    doExecute(point);
                    return;
                }
            } else {
                NamedConditionFalseWork namedExecuteFalseWork = (NamedConditionFalseWork) work;
                beWorkFlow = namedExecuteFalseWork.getWork() instanceof WorkFlow;
                if (!beTrue) {
                    report = doSingleWork(namedExecuteFalseWork.getWork(), workContext, point);
                } else {
                    doExecute(point);
                    return;
                }
            }
        } else if (work instanceof NamedDecideWork){
            NamedDecideWork namedDecideWork = (NamedDecideWork) work;
            beWorkFlow = namedDecideWork.getDecideWork() instanceof WorkFlow;
            report = doSingleWork(namedDecideWork.getDecideWork(), workContext, point);
            predicateWorkReport = report;
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

    @Override
    public ConditionalFlow then(Function<WorkReport, Work> fun) {
        thenFuns.add(fun);
        return this;
    }

    @Override
    public ConditionalFlow then(Work work) {
        thenFuns.add(report -> work);
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
