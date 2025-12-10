import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import work.PrintMessageWork;
import work.RepeatPrintWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;

public class TestRepeatFlow {

    public static void testRepeatTimes() {
        PrintMessageWork repeat = new PrintMessageWork("im repeat");
        WorkFlow flow = aNewRepeatFlow(repeat).times(3);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    public static void testRepeatUntil() {
        RepeatPrintWork repeat = new RepeatPrintWork("do the repeat.");
        WorkFlow flow = aNewRepeatFlow(repeat).until(WorkReportPredicate.FAILED);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }
    public static void main(String[] args) {
//        testRepeatTimes();
        testRepeatUntil();
    }
}
