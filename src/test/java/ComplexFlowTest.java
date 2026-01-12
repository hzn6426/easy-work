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
import work.DelayPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ChooseFlow.aNewChooseFlow;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class ComplexFlowTest {

    private static void testComplex1() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork i = new PrintMessageWork("i");

        PrintMessageWork j = new PrintMessageWork("j");
        PrintMessageWork k = new PrintMessageWork("k");
        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");
        PrintMessageWork n = new PrintMessageWork("n");

        PrintMessageWork z = new PrintMessageWork("z");

        WorkFlow flow = aNewSequentialFlow(
            a,
            aNewParallelFlow(
                aNewSequentialFlow(b,c),
                aNewSequentialFlow(d,e,f),
                aNewSequentialFlow(
                    aNewConditionalFlow(g).when(WorkReportPredicate.COMPLETED,
                        aNewSequentialFlow(h,i, aNewParallelFlow(j,k)),
                        aNewSequentialFlow(l,m)),
                    n)),
            z
        );
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testComplex2() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        DelayPrintMessageWork g = new DelayPrintMessageWork("g", 3);
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork i = new PrintMessageWork("i");

        PrintMessageWork j = new PrintMessageWork("j");
        PrintMessageWork k = new PrintMessageWork("k");
        PrintMessageWork l = new PrintMessageWork("l");
        PrintMessageWork m = new PrintMessageWork("m");

        PrintMessageWork z = new PrintMessageWork("z");

        WorkFlow flow = aNewSequentialFlow(
            a,
            aNewChooseFlow(b)
                .chooseWhen(
                    WorkReportPredicate.COMPLETED,
                    aNewSequentialFlow(
                        c,
                        aNewParallelFlow(
                            aNewSequentialFlow(
                                aNewConditionalFlow(g).when(
                                    WorkReportPredicate.COMPLETED,
                                    aNewSequentialFlow(h,i),
                                    j
                                ),
                                k
                            ),
                            aNewSequentialFlow(l,m)
                        )
                    )
                )
                .otherWise(aNewSequentialFlow(d,e,f)),
            z
        );
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    public static void main(String[] args) {
        //example for lite flow https://liteflow.cc/pages/5156b3/#%E5%A4%8D%E6%9D%82%E4%BE%8B%E5%AD%90%E4%B8%80
        testComplex2();
    }
}
