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

import com.baomibing.work.flow.ConditionalFlow;
import com.baomibing.work.predicate.WorkReportJsonPredicate;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.work.WorkExecutePolicy;
import work.ExceptionPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.predicate.AllPredicate.allPredicate;
import static com.baomibing.work.predicate.AndPredicate.andPredicate;
import static com.baomibing.work.predicate.AnyPredicate.anyPredicate;
import static com.baomibing.work.predicate.EqPredicate.aNewEqPredicate;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class serializePredicateTest {

    private static void testAllPredicate() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");

        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        ConditionalFlow flow = aNewConditionalFlow(aNewSequentialFlow(a,b,c)).when(
            allPredicate(WorkReportJsonPredicate.COMPLETED),
            aNewSequentialFlow(d,e),
            aNewSequentialFlow(l,m));
        System.out.println(flow.serialize());
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

        ConditionalFlow flow = aNewConditionalFlow(aNewSequentialFlow(a,exceptionPrintMessageWork,b,c)).when(
            anyPredicate(WorkReportJsonPredicate.COMPLETED),
            aNewSequentialFlow(d,e),
            aNewSequentialFlow(l,m)).policy(WorkExecutePolicy.FAST_ALL);
        System.out.println(flow.serialize());
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

        WorkReportPredicate anyCompletePredicate = anyPredicate(WorkReportJsonPredicate.COMPLETED);

        WorkReportPredicate anyNameAPredicate = anyPredicate(aNewEqPredicate(WorkReport::getWorkName, "a"));

        ConditionalFlow flow = aNewConditionalFlow(aNewSequentialFlow(aNamePointWork(a).named("a"),exceptionPrintMessageWork,b,c).policy(WorkExecutePolicy.FAST_ALL)).when(
            andPredicate(anyCompletePredicate, anyNameAPredicate),
            aNewSequentialFlow(d,e),
            aNewSequentialFlow(l,m)).policy(WorkExecutePolicy.FAST_ALL);
        System.out.println(flow.serialize());
    }

    public static void main(String[] args) {
//        testAllPredicate();
//        testAnyPredicate();
        testAndPredicate();
    }
}
