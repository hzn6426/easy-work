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
import com.baomibing.work.flow.RepeatFlow;
import com.baomibing.work.flow.SequentialFlow;
import com.baomibing.work.flow.WorkFlow;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
//import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class RepeatPointTest {

    private static void test1() {
        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork repeat = new PrintMessageWork("im repeat");
        RepeatFlow flow = aNewRepeatFlow(aNamePointWork(repeat).named("THE_REPEAT").point("R")).times(3)
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("R");
        System.out.println("execute to R..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    private static void test2() {
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork repeat = new PrintMessageWork("im repeat");
        RepeatFlow flow = aNewRepeatFlow(aNewSequentialFlow(a,b,c,aNamePointWork(repeat).named("THE_REPEAT").point("R")) ).times(3)
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("R");
        System.out.println("execute to R..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    private static void test3() {
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        PrintMessageWork repeat = new PrintMessageWork("im repeat");
        RepeatFlow flow = aNewRepeatFlow(aNewSequentialFlow(a,aNewSequentialFlow(b,c,aNamePointWork(repeat).named("THE_REPEAT").point("R"))) ).times(3)
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("R");
        System.out.println("execute to R..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    public static void main(String[] args) {
        test3();
    }
}
