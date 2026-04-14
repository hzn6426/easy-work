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
import com.baomibing.work.predicate.WorkReportPredicate;
import work.PausePrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class ConditionalPauseTest {

    private static void test1() {
        PrintMessageWork work1 = new PrintMessageWork("do success!");
        PrintMessageWork work2 = new PrintMessageWork("do fail!");
        PausePrintMessageWork successWork = new PausePrintMessageWork("im execute success", true);

        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        ConditionalFlow flow = aNewConditionalFlow(successWork)
            .when(WorkReportPredicate.COMPLETED, work1, work2)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The c paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    private static void test2() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        PrintMessageWork work1 = new PrintMessageWork("do success!");
        PrintMessageWork work2 = new PrintMessageWork("do fail!");
        PausePrintMessageWork successWork = new PausePrintMessageWork("im execute success",true);

        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g",true);
        PrintMessageWork h = new PrintMessageWork("h");

        ConditionalFlow flow = aNewConditionalFlow(aNewSequentialFlow(a,b,successWork,c))
            .when(WorkReportPredicate.COMPLETED, work1, work2)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The c paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    private static void test3() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        PausePrintMessageWork work1 = new PausePrintMessageWork("do success!", true);
        PrintMessageWork work2 = new PrintMessageWork("do fail!");
        PrintMessageWork successWork = new PrintMessageWork("im execute success");

        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g",true);
        PrintMessageWork h = new PrintMessageWork("h");

        ConditionalFlow flow = aNewConditionalFlow(successWork)
            .when(WorkReportPredicate.COMPLETED, aNewSequentialFlow(a,b,work1,c), work2)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The c paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    public static void main(String[] args) {
//        test1();
//        test2();
        test3();
    }
}
