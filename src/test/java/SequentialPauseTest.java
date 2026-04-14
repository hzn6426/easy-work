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

import com.baomibing.work.flow.SequentialFlow;
import work.PausePrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class SequentialPauseTest {

    private static void test1() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PausePrintMessageWork c = new PausePrintMessageWork("c", true);
        PrintMessageWork d = new PrintMessageWork("d");

        PausePrintMessageWork e = new PausePrintMessageWork("e", true);
        PrintMessageWork f = new PrintMessageWork("f");

        SequentialFlow flow = aNewSequentialFlow(
            a,
            b,
            c,
            d,
            e,
            f);
//        flow.execute("CC");
//        System.out.println("execute to CC..");
//        flow.execute("EE");
//        System.out.println("execute to EE..");
        flow.execute();
        System.out.println("c paused....");
        flow.execute();
        System.out.println("e paused....");
        flow.execute();
    }

    private static void test2() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PausePrintMessageWork c = new PausePrintMessageWork("c", true);
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");

        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        SequentialFlow flow = aNewSequentialFlow(
            a,
            b,
            aNewSequentialFlow(c,d),
            e,
            f,
            g,
            h);
        flow.execute();
        System.out.println("The c paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    private static void test3() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PausePrintMessageWork c = new PausePrintMessageWork("c", true);
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");

        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        SequentialFlow flow = aNewSequentialFlow(
            a,
            aNewSequentialFlow(aNewSequentialFlow(b,c),d),
            e,
            f,
            g,
            h);
        flow.execute();
        System.out.println("The c paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute("");
    }

    private static void testThenPoint() {
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PausePrintMessageWork c = new PausePrintMessageWork("c", true);
        PrintMessageWork d = new PrintMessageWork("d");
        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g",true);
        PrintMessageWork h = new PrintMessageWork("h");

        SequentialFlow flow =  aNewSequentialFlow(
            a,
            b,
            aNewSequentialFlow(c,d),
            e
            ).then(f).then(g).then(h);
        flow.execute();
        System.out.println("The c paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    public static void main(String[] args) {
//        testThenPoint();
//        test1();
//        test2();
//        test3();
        testThenPoint();
    }


}
