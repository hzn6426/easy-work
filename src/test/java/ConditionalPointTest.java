import com.baomibing.work.flow.ConditionalFlow;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import work.PrintMessageWork;

import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class ConditionalPointTest {

    private static void test1() {
        PrintMessageWork work1 = new PrintMessageWork("do success!");
        PrintMessageWork work2 = new PrintMessageWork("do fail!");
        PrintMessageWork successWork = new PrintMessageWork("im execute success");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        ConditionalFlow flow = aNewConditionalFlow(aNamePointWork(successWork).named("THE_SUCCESS").point("CC"))
            .when(WorkReportPredicate.COMPLETED, work1, work2)
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

        PrintMessageWork work1 = new PrintMessageWork("do success!");
        PrintMessageWork work2 = new PrintMessageWork("do fail!");
        PrintMessageWork successWork = new PrintMessageWork("im execute success");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        ConditionalFlow flow = aNewConditionalFlow(aNewSequentialFlow(a,b,aNamePointWork(successWork).named("THE_SUCCESS").point("CC"),c))
            .when(WorkReportPredicate.COMPLETED, work1, work2)
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

        PrintMessageWork work1 = new PrintMessageWork("do success!");
        PrintMessageWork work2 = new PrintMessageWork("do fail!");
        PrintMessageWork successWork = new PrintMessageWork("im execute success");

        PrintMessageWork f = new PrintMessageWork("f");
        PrintMessageWork g = new PrintMessageWork("g");
        PrintMessageWork h = new PrintMessageWork("h");

        ConditionalFlow flow = aNewConditionalFlow(successWork)
            .when(WorkReportPredicate.COMPLETED, aNewSequentialFlow(a,b,aNamePointWork(work1).named("THE_COMPLETE").point("CC"),c), work2)
            .then(f).then(aNamePointWork(g).named("THE_G").point("GG")).then(h);

        flow.execute("CC");
        System.out.println("execute to CC..");
        flow.execute("GG");
        System.out.println("execute to GG..");
        flow.execute("");
    }

    public static void main(String[] args) {
        test3();
    }
}
