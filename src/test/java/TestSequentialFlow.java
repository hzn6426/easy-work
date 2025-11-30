import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import work.DelayPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;

public class TestSequentialFlow {

    public static void main(String[] args) {

        PrintMessageWork work1 = new PrintMessageWork("hello");
        DelayPrintMessageWork work2 = new DelayPrintMessageWork("world",3);
        PrintMessageWork work3 = new PrintMessageWork("im sequential flow");

        WorkFlow flow = aNewSequentialFlow(work1, work2, work3);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }
}
