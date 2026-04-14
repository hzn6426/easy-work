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

import com.baomibing.work.flow.ChooseFlow;
import com.baomibing.work.work.WorkStatus;
import work.*;

import static com.baomibing.work.flow.ChooseFlow.aNewChooseFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class ChoosePauseTest {

    private static void test1() {

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        PauseChoosePrintMessageWork work = new PauseChoosePrintMessageWork(true);
        PrintMessageWork work1 = new PrintMessageWork("1");
        PrintMessageWork work2 = new PrintMessageWork("2");
        PrintMessageWork work3 = new PrintMessageWork("3");
        PrintMessageWork work4 = new PrintMessageWork("4");

        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        ChooseFlow flow = aNewChooseFlow(work).chooseWhen((report) -> report.getResult().equals(1), work1)
            .chooseWhen((report) -> report.getResult().equals(2), work2)
            .chooseWhen((report) -> report.getResult().equals(3), work3)
            .otherWise(work4)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The work paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    private static void test2() {

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        PauseChoosePrintMessageWork work = new PauseChoosePrintMessageWork(true);
        PrintMessageWork work1 = new PrintMessageWork("1");
        PrintMessageWork work2 = new PrintMessageWork("2");
        PrintMessageWork work3 = new PrintMessageWork("3");
        PrintMessageWork work4 = new PrintMessageWork("4");

        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        ChooseFlow flow = aNewChooseFlow(aNewSequentialFlow(a,b,work,c))
//            .chooseWhen((report) -> report.getResult().equals(1), work1)
            .chooseWhen((report) -> report.getStatus().equals(WorkStatus.COMPLETED), work2)
//            .chooseWhen((report) -> report.getResult().equals(3), work3)
            .otherWise(work4)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The work paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    private static void test3() {

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ChooseMinPrintMessageWork work = new ChooseMinPrintMessageWork(4,10);
        PrintMessageWork work1 = new PrintMessageWork("1");
        PrintMessageWork work2 = new PrintMessageWork("2");
        PrintMessageWork work3 = new PrintMessageWork("3");
        PausePrintMessageWork work4 = new PausePrintMessageWork("4", true);

        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        ChooseFlow flow = aNewChooseFlow(work).chooseWhen((report) -> report.getResult().equals(1), work1)
            .chooseWhen((report) -> report.getResult().equals(2), work2)
            .chooseWhen((report) -> report.getResult().equals(3), work3)
            .otherWise(aNewSequentialFlow(a,b,work4,c))
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The 4 paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    private static void test4() {

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ChoosePrintMessageWork work = new ChoosePrintMessageWork();
        PrintMessageWork work1 = new PrintMessageWork("1");
        PausePrintMessageWork work2 = new PausePrintMessageWork("2", true);
        PrintMessageWork work3 = new PrintMessageWork("3");
        PrintMessageWork work4 = new PrintMessageWork("4");

        PrintMessageWork f = new PrintMessageWork("f");
        PausePrintMessageWork g = new PausePrintMessageWork("g", true);
        PrintMessageWork h = new PrintMessageWork("h");

        ChooseFlow flow = aNewChooseFlow(work)
//            .chooseWhen((report) -> report.getResult().equals(1), work1)
            .chooseWhen((report) -> report.getStatus().equals(WorkStatus.COMPLETED), aNewSequentialFlow(a,b,work2,c))
//            .chooseWhen((report) -> report.getResult().equals(3), work3)
            .otherWise(work4)
            .then(f).then(g).then(h);

        flow.execute();
        System.out.println("The 2 paused....");
        flow.execute();
        System.out.println("The g paused....");
        flow.execute();
    }

    public static void main(String[] args) {
//        test1();
//        test2();
//        test3();
        test4();
    }
}
