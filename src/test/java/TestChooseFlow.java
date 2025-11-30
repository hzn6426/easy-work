import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import work.ChoosePrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ChooseFlow.aNewChooseFlow;

public class TestChooseFlow {

    public static void main(String[] args) {
        ChoosePrintMessageWork work = new ChoosePrintMessageWork();
        PrintMessageWork work1 = new PrintMessageWork("1");
        PrintMessageWork work2 = new PrintMessageWork("2");
        PrintMessageWork work3 = new PrintMessageWork("3");
        PrintMessageWork work4 = new PrintMessageWork("4");

        WorkFlow flow = aNewChooseFlow(work).chooseWhen((report) -> report.getResult().equals(1), work1)
                .chooseWhen((report) -> report.getResult().equals(2), work2)
                .chooseWhen((report) -> report.getResult().equals(3), work3)
                .otherWise(work4);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }
}
