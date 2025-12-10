import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.LoopIndexPredicate;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.LoopFlow.aNewLoopFlow;

public class TestLoopFlow {

    private static void testAllLoop() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        WorkFlow flow = aNewLoopFlow(work1, work2, work3, work4);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testBreakLoop() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        WorkFlow flow = aNewLoopFlow(work1, work2, work3, work4).withBreakPredicate(LoopIndexPredicate.indexPredicate(2));
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void testContinueLoop() {
        PrintMessageWork work1 = new PrintMessageWork("work1");
        PrintMessageWork work2 = new PrintMessageWork("work2");
        PrintMessageWork work3 = new PrintMessageWork("work3");
        PrintMessageWork work4 = new PrintMessageWork("work4");

        WorkFlow flow = aNewLoopFlow(work1, work2, work3, work4).withContinuePredicate(LoopIndexPredicate.indexPredicate(1));
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    public static void main(String[] args) {
//        testAllLoop();
//        testBreakLoop();
        testContinueLoop();
    }

}
