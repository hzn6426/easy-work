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

import com.baomibing.work.flow.LoopFlow;
import com.baomibing.work.predicate.LoopIndexPredicate;
import work.PausePrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.flow.LoopFlow.aNewLoopFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class LoopPauseTest {

    private static void test1() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PausePrintMessageWork c = new PausePrintMessageWork("c", true);
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");

        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        LoopFlow flow = aNewLoopFlow(work1, work2, c, work3, work4)
            .withBreakPredicate(LoopIndexPredicate.indexPredicate(12))
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The c paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    private static void test2() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PausePrintMessageWork c = new PausePrintMessageWork("c",true);
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");

        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        LoopFlow flow = aNewLoopFlow(work1, work2, aNewSequentialFlow(a,b,c,d), work3, work4)
            .withBreakPredicate(LoopIndexPredicate.indexPredicate(12))
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The c paused....");
        flow.execute();
        System.out.println("The  paused....");
        flow.execute("");
    }
    public static void main(String[] args) {
        test1();
//        test2();
    }
}
