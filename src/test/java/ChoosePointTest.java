import com.baomibing.work.flow.ChooseFlow;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.work.WorkStatus;
import work.ChooseMinPrintMessageWork;
import work.ChoosePrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.flow.ChooseFlow.aNewChooseFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class ChoosePointTest {

    private static void test1() {

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ChoosePrintMessageWork work = new ChoosePrintMessageWork();
        PrintMessageWork work1 = new PrintMessageWork("1");
        PrintMessageWork work2 = new PrintMessageWork("2");
        PrintMessageWork work3 = new PrintMessageWork("3");
        PrintMessageWork work4 = new PrintMessageWork("4");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        ChooseFlow flow = aNewChooseFlow(aNamePointWork(work).named("THE_SUCCESS").point("CC")).chooseWhen((report) -> report.getResult().equals(1), work1)
            .chooseWhen((report) -> report.getResult().equals(2), work2)
            .chooseWhen((report) -> report.getResult().equals(3), work3)
            .otherWise(work4)
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    private static void test2() {

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ChoosePrintMessageWork work = new ChoosePrintMessageWork();
        PrintMessageWork work1 = new PrintMessageWork("1");
        PrintMessageWork work2 = new PrintMessageWork("2");
        PrintMessageWork work3 = new PrintMessageWork("3");
        PrintMessageWork work4 = new PrintMessageWork("4");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        ChooseFlow flow = aNewChooseFlow(aNewSequentialFlow(a,b,aNamePointWork(work).named("THE_SUCCESS").point("CC"),c))
//            .chooseWhen((report) -> report.getResult().equals(1), work1)
            .chooseWhen((report) -> report.getStatus().equals(WorkStatus.COMPLETED), work2)
//            .chooseWhen((report) -> report.getResult().equals(3), work3)
            .otherWise(work4)
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    private static void test3() {

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ChooseMinPrintMessageWork work = new ChooseMinPrintMessageWork(4,10);
        PrintMessageWork work1 = new PrintMessageWork("1");
        PrintMessageWork work2 = new PrintMessageWork("2");
        PrintMessageWork work3 = new PrintMessageWork("3");
        PrintMessageWork work4 = new PrintMessageWork("4");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        ChooseFlow flow = aNewChooseFlow(work).chooseWhen((report) -> report.getResult().equals(1), work1)
            .chooseWhen((report) -> report.getResult().equals(2), work2)
            .chooseWhen((report) -> report.getResult().equals(3), work3)
            .otherWise(aNewSequentialFlow(a,b,aNamePointWork(work4).named("THE_SUCCESS").point("CC"),c))
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    private static void test4() {

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");

        ChoosePrintMessageWork work = new ChoosePrintMessageWork();
        PrintMessageWork work1 = new PrintMessageWork("1");
        PrintMessageWork work2 = new PrintMessageWork("2");
        PrintMessageWork work3 = new PrintMessageWork("3");
        PrintMessageWork work4 = new PrintMessageWork("4");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        ChooseFlow flow = aNewChooseFlow(work)
//            .chooseWhen((report) -> report.getResult().equals(1), work1)
            .chooseWhen((report) -> report.getStatus().equals(WorkStatus.COMPLETED), aNewSequentialFlow(a,b,aNamePointWork(work2).named("THE_SUCCESS").point("CC"),c))
//            .chooseWhen((report) -> report.getResult().equals(3), work3)
            .otherWise(work4)
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    public static void main(String[] args) {
        test4();
    }
}
