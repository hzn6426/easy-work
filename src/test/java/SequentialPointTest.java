import com.baomibing.work.flow.SequentialFlow;
import work.PrintMessageWork;

import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class SequentialPointTest {

    private static void test1() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");

        SequentialFlow flow = aNewSequentialFlow(
            a,
            b,
            aNamePointWork(c).named("THE_C").point("CC"),
            d,
            aNamePointWork(e).named("THE_E").point("EE"),
            f);
        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("EE");
        System.out.println("execute to EE..");
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
        PrintMessageWork h = new PrintMessageWork("f");

        SequentialFlow flow = aNewSequentialFlow(
            a,
            b,
            aNewSequentialFlow(aNamePointWork(c).named("THE_C").point("CC"),d),
            e,
            f,
            aNamePointWork(g).named("THE_G").point("GG"),
            h);
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
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");

        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        SequentialFlow flow = aNewSequentialFlow(
            a,
            aNewSequentialFlow(aNewSequentialFlow(b,aNamePointWork(c).named("THE_C").point("CC")),d),
            e,
            f,
            aNamePointWork(g).named("THE_G").point("GG"),
            h);
        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    private static void testThenPoint() {
        PrintMessageWork a = new PrintMessageWork("a");
        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");
        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        SequentialFlow flow =  aNewSequentialFlow(
            a,
            b,
            aNewSequentialFlow(aNamePointWork(c).named("THE_C").point("CC"),d),
            e
            ).then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);
        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    public static void main(String[] args) {
//        testThenPoint();
        test3();
    }
}
