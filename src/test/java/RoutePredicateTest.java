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

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.report.SequentialWorkReport;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.work.WorkExecutePolicy;
import work.ExceptionPrintMessageWork;
import work.PrintMessageWork;

import java.util.function.Predicate;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.predicate.AllPredicate.allPredicate;
import static com.baomibing.work.predicate.AndPredicate.andPredicate;
import static com.baomibing.work.predicate.AnyPredicate.anyPredicate;
import static com.baomibing.work.predicate.NonePredicate.nonePredicate;
import static com.baomibing.work.predicate.OrPredicate.orPredicate;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class RoutePredicateTest {

    private static void testAllPredicate() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");

        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        WorkFlow flow = aNewConditionalFlow(aNewSequentialFlow(a,b,c)).when(
                        allPredicate(report -> WorkReportPredicate.COMPLETED.apply(report)),
                        aNewSequentialFlow(d,e),
                        aNewSequentialFlow(l,m));
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testAnyPredicate() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();

        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");

        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        WorkFlow flow = aNewConditionalFlow(aNewSequentialFlow(a,exceptionPrintMessageWork,b,c)).when(
            anyPredicate(report -> WorkReportPredicate.COMPLETED.apply(report)),
            aNewSequentialFlow(d,e),
            aNewSequentialFlow(l,m)).policy(WorkExecutePolicy.FAST_ALL);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testNonePredicate() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();

        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");

        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        WorkFlow flow = aNewConditionalFlow(aNewSequentialFlow(a,exceptionPrintMessageWork,b,c).policy(WorkExecutePolicy.FAST_ALL)).when(
            nonePredicate(report -> WorkReportPredicate.FAILED.apply(report)),
            aNewSequentialFlow(d,e),
            aNewSequentialFlow(l,m)).policy(WorkExecutePolicy.FAST_ALL);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testAndPredicate() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();

        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");

        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        Predicate<WorkReport> anyCompletePredicate = workReport -> {
            SequentialWorkReport sequentialWorkReport = (SequentialWorkReport) workReport;
            return sequentialWorkReport.anyMatch(report -> WorkReportPredicate.COMPLETED.apply(report));
        };

        Predicate<WorkReport> anyNameAPredicate = workReport -> {
            SequentialWorkReport sequentialWorkReport = (SequentialWorkReport) workReport;
            return sequentialWorkReport.anyMatch(report -> report.getWorkName().equals("a"));
        };

        WorkFlow flow = aNewConditionalFlow(aNewSequentialFlow(aNamePointWork(a).named("a"),exceptionPrintMessageWork,b,c).policy(WorkExecutePolicy.FAST_ALL)).when(
            andPredicate(anyCompletePredicate, anyNameAPredicate),
            aNewSequentialFlow(d,e),
            aNewSequentialFlow(l,m)).policy(WorkExecutePolicy.FAST_ALL);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testOrPredicate() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();

        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");

        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        Predicate<WorkReport> anyCompletePredicate = workReport -> {
            SequentialWorkReport sequentialWorkReport = (SequentialWorkReport) workReport;
            return sequentialWorkReport.anyMatch(report -> WorkReportPredicate.FAILED.apply(report));
        };

        Predicate<WorkReport> anyNameBPredicate = workReport -> {
            SequentialWorkReport sequentialWorkReport = (SequentialWorkReport) workReport;
            return sequentialWorkReport.anyMatch(report -> report.getWorkName().equals("b"));
        };

        WorkFlow flow = aNewConditionalFlow(aNewSequentialFlow(aNamePointWork(a).named("a"),exceptionPrintMessageWork,b,c).policy(WorkExecutePolicy.FAST_ALL)).when(
            orPredicate(anyCompletePredicate, anyNameBPredicate),
            aNewSequentialFlow(d,e),
            aNewSequentialFlow(l,m)).policy(WorkExecutePolicy.FAST_ALL);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    public static void main(String[] args) {
//        testAllPredicate();
//        testAnyPredicate();
//        testNonePredicate();
//        testAndPredicate();
        testOrPredicate();
    }
}
