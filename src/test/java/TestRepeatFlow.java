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
import com.baomibing.work.predicate.TimesPredicate;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.util.Checker;
import work.PrintMessageWork;
import work.RepeatPrintWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;

public class TestRepeatFlow {

    public static void testRepeatTimes() {
        PrintMessageWork repeat = new PrintMessageWork("im repeat");
        WorkFlow flow = aNewRepeatFlow(repeat).times(3);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    public static void testRetry() {
        PrintMessageWork repeat = new PrintMessageWork("im repeat");
        WorkFlow flow = aNewRepeatFlow(repeat).until(TimesPredicate.times(3,
            workReport -> Checker.BeNull(workReport.getError())));
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    public static void testRepeatUntil() {
        RepeatPrintWork repeat = new RepeatPrintWork("do the repeat.");
        WorkFlow flow = aNewRepeatFlow(repeat).until(WorkReportPredicate.FAILED);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }
    public static void main(String[] args) {
//        testRepeatTimes();
        testRepeatUntil();
//        testRetry();
    }
}
