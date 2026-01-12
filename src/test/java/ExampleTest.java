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
import com.baomibing.work.flow.SequentialFlow;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class ExampleTest {

    private static void example() {
        //example in README
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");
        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork z = new PrintMessageWork("z");

        WorkFlow flow = aNewSequentialFlow(
            aNewRepeatFlow(a).times(3),
            aNewSequentialFlow(b,c,d),
            aNewConditionalFlow(
                aNewParallelFlow(e,f).withAutoShutDown(true)
            ).when(
                WorkReportPredicate.COMPLETED,
                g,
                h
            ),
            z
        );
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void examplePoint() {
        //example in README
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");
        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork z = new PrintMessageWork("z");

        SequentialFlow flow = aNewSequentialFlow(
            aNewRepeatFlow(a).times(3),
            aNewSequentialFlow(b,aNamePointWork(c).named("C").point("C_BREAK_POINT"),d),
            aNewConditionalFlow(
                aNewParallelFlow(e,f).withAutoShutDown(true)
            ).when(
                WorkReportPredicate.COMPLETED,
                g,
                h
            ),
            z
        );
        flow.execute("C_BREAK_POINT");
        System.out.println("execute to the break point `C_BREAK_POINT`");
        flow.execute();
    }

    public static void main(String[] args) {
        examplePoint();
    }
}
