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
import com.baomibing.work.predicate.LoopIndexPredicate;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.LoopFlow.aNewLoopFlow;

public class TestLoopFlow {

    private static void testAllLoop() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        WorkFlow flow = aNewLoopFlow(work1, work2, work3, work4);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testBreakLoop() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        WorkFlow flow = aNewLoopFlow(work1, work2, work3, work4).withBreakPredicate(LoopIndexPredicate.indexPredicate(2));
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testContinueLoop() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        WorkFlow flow = aNewLoopFlow(work1, work2, work3, work4).withContinuePredicate(LoopIndexPredicate.indexPredicate(1));
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    public static void main(String[] args) {
//        testAllLoop();
//        testBreakLoop();
        testContinueLoop();
    }

}
