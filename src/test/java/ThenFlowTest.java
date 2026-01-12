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
import com.baomibing.work.work.WorkExecutePolicy;
import work.ExceptionPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class ThenFlowTest {

    private static void testThen() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");
        PrintMessageWork work5 = new PrintMessageWork("work5");
        PrintMessageWork work6 = new PrintMessageWork("after parallel");
        PrintMessageWork work7 = new PrintMessageWork("after conditional");
        PrintMessageWork work8 = new PrintMessageWork("after 8");
        PrintMessageWork work9 = new PrintMessageWork("after 9");
        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();

        WorkContext workContext = new WorkContext();
        WorkFlow flow = aNewSequentialFlow(
            exceptionPrintMessageWork,
            aNewRepeatFlow(work1).times(3)
        ).then(
            aNewConditionalFlow(
                aNewParallelFlow(
                    work2,
                    work3
                ).withAutoShutDown(true).then(work6)
            ).when(
                WorkReportPredicate.COMPLETED,
                work4,
                work5
            ).then(work7).then(work8).then(work9)
        );
        aNewWorkFlowEngine().run(flow, workContext);
    }

    private static void testExceptionThen() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");
        PrintMessageWork work5 = new PrintMessageWork("work5");
        PrintMessageWork work6 = new PrintMessageWork("after parallel");
        PrintMessageWork work7 = new PrintMessageWork("after conditional");
        ExceptionPrintMessageWork exceptionPrintMessageWork = new ExceptionPrintMessageWork();

        WorkContext workContext = new WorkContext();
        WorkFlow flow = aNewSequentialFlow(
            aNewRepeatFlow(work1).times(3),
            exceptionPrintMessageWork
        ).policy(WorkExecutePolicy.FAST_EXCEPTION)
        .then(
            aNewConditionalFlow(
                aNewParallelFlow(
                    work2,
                    work3
                ).then(work6)
            ).when(
                WorkReportPredicate.COMPLETED,
                work4,
                work5
            ).then(work7)
        );
        aNewWorkFlowEngine().run(flow, workContext);
    }

    public static void main(String[] args) {
        testThen();
    }
}
