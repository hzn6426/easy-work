import com.baomibing.work.flow.ParallelFlow;
import com.baomibing.work.report.ParallelWorkReport;
import work.ParallelPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class ParallelPointTest {


    private static void test1() {
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        ParallelFlow flow = aNewParallelFlow(a, b, aNamePointWork(c).named("THE_C").point("CC"),d,e).withAutoShutDown(true)
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
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");
        ParallelFlow flow = aNewParallelFlow(a, b, aNewSequentialFlow(aNamePointWork(c).named("THE_C").point("CC"),d),e).withAutoShutDown(true)
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

//        flow.execute("CC");
//        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    public static void main(String[] args) {
        test2();
    }
}
