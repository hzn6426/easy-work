import com.baomibing.work.flow.LoopFlow;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.LoopIndexPredicate;
import work.PrintMessageWork;

import static com.baomibing.work.flow.LoopFlow.aNewLoopFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class LoopPointTest {

    private static void test1() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");

        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        LoopFlow flow = aNewLoopFlow(work1, work2, aNamePointWork(c).named("THE_C").point("CC"), work3, work4)
            .withBreakPredicate(LoopIndexPredicate.indexPredicate(12))
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    private static void test2() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");

        PrintMessageWork e = new PrintMessageWork("e");
        PrintMessageWork f = new PrintMessageWork("f");

        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        LoopFlow flow = aNewLoopFlow(work1, work2, aNewSequentialFlow(a,b,aNamePointWork(c).named("THE_C").point("CC"),d), work3, work4)
            .withBreakPredicate(LoopIndexPredicate.indexPredicate(12))
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }
    public static void main(String[] args) {
        test2();
    }
}
