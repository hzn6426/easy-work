import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.ConditionalFlow;
import com.baomibing.work.flow.SequentialFlow;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.report.SequentialWorkReport;
import com.baomibing.work.report.WorkReport;
import work.PrintMessageWork;

import java.util.Map;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ConditionalFlow.aNewConditionalFlow;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;
import static com.baomibing.work.flow.RepeatFlow.aNewRepeatFlow;
import static com.baomibing.work.flow.SequentialFlow.aNewSequentialFlow;
import static com.baomibing.work.work.NamedPointWork.aNamePointWork;

public class TraceTest {

    private static void testTrace1() {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        WorkContext workContext = new WorkContext();
        SequentialFlow flow = aNewSequentialFlow(
            aNewRepeatFlow(work1).times(3),
            aNewConditionalFlow(
                aNewParallelFlow(work2,work3).withAutoShutDown(true)
            ).when(WorkReportPredicate.COMPLETED, aNamePointWork(work4).named("work4"), work5)
        ).named("sequential").trace(true);
        aNewWorkFlowEngine().run(flow, workContext);
        Map<String, WorkReport> map =  flow.getExecutedReportMap();
        WorkReport work4Report = map.get("work4");
        for (Map.Entry<String, WorkReport> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            WorkReport report = entry.getValue();
            System.out.println(report.getClass().getName());
        }

    }

    private static void testTrace2() {
        PrintMessageWork work1 = new PrintMessageWork("foo");
        PrintMessageWork work2 = new PrintMessageWork("hello");
        PrintMessageWork work3 = new PrintMessageWork("world");
        PrintMessageWork work4 = new PrintMessageWork("ok");
        PrintMessageWork work5 = new PrintMessageWork("nok");

        ConditionalFlow conditionalFlow = aNewConditionalFlow(
            aNewParallelFlow(work2,work3).withAutoShutDown(true)
        ).when(WorkReportPredicate.COMPLETED, aNamePointWork(work4).named("work4"), work5).trace(true);

        WorkContext workContext = new WorkContext();
        SequentialFlow flow = aNewSequentialFlow(
            aNewRepeatFlow(work1).times(3),
            conditionalFlow
        ).named("sequential").trace(true);
        aNewWorkFlowEngine().run(flow, workContext);
        Map<String, WorkReport> map =  flow.getExecutedReportMap();
        //you can get result of work4
        WorkReport work4Report = map.get("work4");
        for (Map.Entry<String, WorkReport> entry : map.entrySet()) {
            System.out.println(entry.getKey());
            WorkReport report = entry.getValue();
            System.out.println(report.getClass().getName());
        }

        Map<String, WorkReport> map2 =  conditionalFlow.getExecutedReportMap();
        for (Map.Entry<String, WorkReport> entry : map2.entrySet()) {
            System.out.println(entry.getKey());
            WorkReport report = entry.getValue();
            System.out.println(report.getClass().getName());
        }

    }

    public static void main(String[] args) {
        testTrace2();
    }


}
