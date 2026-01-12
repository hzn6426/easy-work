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
import com.baomibing.work.flow.ConditionalFlow;
import com.baomibing.work.flow.SequentialFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.report.SequentialWorkReport;
import com.baomibing.work.report.WorkReport;
import work.PrintMessageWork;

import java.util.Map;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class TraceTest {

    private static void testTrace1() {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        WorkContext workContext = new WorkContext();
        SequentialFlow flow = aNewSequentialFlow(
            aNewRepeatFlow(work1).times(3),
            aNewConditionalFlow(
                aNewParallelFlow(work2,work3).withAutoShutDown(true)
            ).when(WorkReportPredicate.COMPLETED, aNamePointWork(work4).named("work4"), work5)
        ).named("sequential").trace(true);
        aNewWorkFlowEngine().run(flow, workContext);
        Map<String, WorkReport> map =  flow.getExecutedReportMap();
        WorkReport work4Report = map.get("work4");
        for (Map.Entry<String, WorkReport> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            WorkReport report = entry.getValue();
            System.out.println(report.getClass().getName());
        }

    }

    private static void testTrace2() {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        ConditionalFlow conditionalFlow = aNewConditionalFlow(
            aNewParallelFlow(work2,work3).withAutoShutDown(true)
        ).when(WorkReportPredicate.COMPLETED, aNamePointWork(work4).named("work4"), work5).trace(true);

        WorkContext workContext = new WorkContext();
        SequentialFlow flow = aNewSequentialFlow(
            aNewRepeatFlow(work1).times(3),
            conditionalFlow
        ).named("sequential").trace(true);
        aNewWorkFlowEngine().run(flow, workContext);
        Map<String, WorkReport> map =  flow.getExecutedReportMap();
        //you can get result of work4
        WorkReport work4Report = map.get("work4");
        for (Map.Entry<String, WorkReport> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            WorkReport report = entry.getValue();
            System.out.println(report.getClass().getName());
        }

        Map<String, WorkReport> map2 =  conditionalFlow.getExecutedReportMap();
        for (Map.Entry<String, WorkReport> entry : map2.entrySet()) {
            System.out.println(entry.getKey());
            WorkReport report = entry.getValue();
            System.out.println(report.getClass().getName());
        }

    }

    public static void main(String[] args) {
        testTrace2();
    }


}
