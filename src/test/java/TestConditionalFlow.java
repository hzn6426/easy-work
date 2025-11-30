import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import work.FailPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;


public class TestConditionalFlow {

    private static void testComplete() {
        PrintMessageWork work1 = new PrintMessageWork("do success!");
        PrintMessageWork work2 = new PrintMessageWork("do fail!");
        PrintMessageWork successWork = new PrintMessageWork("im execute success");

        WorkFlow flow = aNewConditionalFlow(successWork).when(WorkReportPredicate.COMPLETED, work1, work2);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }


    private static void testFail() {
        PrintMessageWork work1 = new PrintMessageWork("do success!");
        PrintMessageWork work2 = new PrintMessageWork("do fail!");
        FailPrintMessageWork failWork = new FailPrintMessageWork("im execute fail");

        WorkFlow flow = aNewConditionalFlow(failWork).when(WorkReportPredicate.FAILED, work2, work1);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }
    public static void main(String[] args) {
//        testComplete();
        testFail();
    }
}
