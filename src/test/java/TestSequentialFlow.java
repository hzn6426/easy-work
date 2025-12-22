import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.SequentialFlow;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.listener.WorkExecuteListener;
import com.baomibing.work.report.DefaultWorkReport;
import com.baomibing.work.work.WorkStatus;
import work.DelayPrintMessageWork;
import work.PrintMessageWork;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class TestSequentialFlow {

    private static void test1() {
        PrintMessageWork work1 = new PrintMessageWork("hello");
        DelayPrintMessageWork work2 = new DelayPrintMessageWork("world",3);
        PrintMessageWork work3 = new PrintMessageWork("im sequential flow");

        WorkFlow flow = aNewSequentialFlow(work1, work2, work3);
        aNewWorkFlowEngine().run(flow, new WorkContext());
    }

    private static void test2() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");
        PrintMessageWork e = new PrintMessageWork("e");

        SequentialFlow flow = aNewSequentialFlow(a, b, c);
        flow.addWork(-1, d);
        flow.addWork(20, e);

        aNewWorkFlowEngine().run(flow, new WorkContext());

    }

    private static void test3() {
        PrintMessageWork a = new PrintMessageWork("a");

        PrintMessageWork b = new PrintMessageWork("b");
        PrintMessageWork c = new PrintMessageWork("c");
        PrintMessageWork d = new PrintMessageWork("d");
        PrintMessageWork e = new PrintMessageWork("e");

        WorkExecuteListener listener = (DefaultWorkReport report, WorkContext workContext, Exception ex) -> {
            System.out.println(report.getStatus() == WorkStatus.COMPLETED ? "YES, SUCCESS" : "NO, FAILURE");
        };
        SequentialFlow flow = aNewSequentialFlow(a, aNamePointWork(b).addWorkExecuteListener(listener), c);
        flow.addWork(-1, d);
        flow.addWork(20, e);

        aNewWorkFlowEngine().run(flow, new WorkContext());

    }

    public static void main(String[] args) {

        test3();

    }
}
