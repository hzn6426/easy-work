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

import com.baomibing.work.flow.RepeatFlow;
import work.PausePrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

//import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class RepeatPauseTest {

    private static void test1() {
        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g",true);
        PrintMessageWork h = new PrintMessageWork("h");
        PausePrintMessageWork repeat = new PausePrintMessageWork("im repeat", true);
        RepeatFlow flow = aNewRepeatFlow(repeat).times(3)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The Repeat paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute("");
    }

    private static void test2() {
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");
        PausePrintMessageWork repeat = new PausePrintMessageWork("im repeat", true);
        RepeatFlow flow = aNewRepeatFlow(aNewSequentialFlow(a,b,c,repeat) ).times(3)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The Repeat paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute("");
    }

    private static void test3() {
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");
        PausePrintMessageWork repeat = new PausePrintMessageWork("im repeat", true);
        RepeatFlow flow = aNewRepeatFlow(aNewSequentialFlow(a,aNewSequentialFlow(b,c,repeat)) ).times(3)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The Repeat paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute("");
    }

    public static void main(String[] args) {
//        test1();
//        test2();
        test3();
    }
}
