import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.work.ParallelWorkReport;
import com.baomibing.work.work.WorkExecutePolicy;
import work.ExceptionPrintMessageWork;
import work.ParallelPrintMessageWork;

import java.util.List;

import static com.baomibing.work.enignee.WorkFlowEngineImpl.aNewWorkFlowEngine;
import static com.baomibing.work.flow.ParallelFlow.aNewParallelFlow;

public class TestParallelFlow {

    private static void testParallel1() {
        ParallelPrintMessageWork work1 = new ParallelPrintMessageWork("one");
        ParallelPrintMessageWork work2 = new ParallelPrintMessageWork("two", 3);
        ParallelPrintMessageWork work3 = new ParallelPrintMessageWork("three");
        ParallelWorkReport report = aNewParallelFlow(work1, work2, work3).withAutoShutDown(true).execute();
        System.out.println(report.getResult(0, String.class));
        System.out.println(report.getResult(1, String.class));
        System.out.println(report.getResult(2, String.class));
    }

    private static void testParallel2() {
        ParallelPrintMessageWork work1 = new ParallelPrintMessageWork("one");
        ParallelPrintMessageWork work2 = new ParallelPrintMessageWork("two", 3);
        ParallelPrintMessageWork work3 = new ParallelPrintMessageWork("three");
        ExceptionPrintMessageWork exceptionWork = new ExceptionPrintMessageWork();
        WorkFlow flow = aNewParallelFlow(work1, work2, exceptionWork, work3).withAutoShutDown(true).policy(WorkExecutePolicy.FAST_EXCEPTION);
        ParallelWorkReport report = (ParallelWorkReport) aNewWorkFlowEngine().run(flow, new WorkContext());
        report.getResult().forEach(System.out::println);
    }

    private static void testParallel3() {
        ParallelPrintMessageWork work1 = new ParallelPrintMessageWork("one");
        ParallelPrintMessageWork work2 = new ParallelPrintMessageWork("two", 3);
        ParallelPrintMessageWork work3 = new ParallelPrintMessageWork("three");
        ExceptionPrintMessageWork exceptionWork = new ExceptionPrintMessageWork();
        WorkFlow flow = aNewParallelFlow(work1, exceptionWork, work2, work3).withAutoShutDown(true).policy(WorkExecutePolicy.FAST_FAIL);
        ParallelWorkReport report = (ParallelWorkReport) aNewWorkFlowEngine().run(flow, new WorkContext());
        List<Object> results = report.getResult();
        for (int j = 0; j < results.size(); j++) {
            System.out.println("the j:" + j + " the result:" + results.get(j));
        }
    }

    private static void testParallel4() {
        ParallelPrintMessageWork work1 = new ParallelPrintMessageWork("one");
        ParallelPrintMessageWork work2 = new ParallelPrintMessageWork("two", 3);
        ParallelPrintMessageWork work3 = new ParallelPrintMessageWork("three");
        ExceptionPrintMessageWork exceptionWork = new ExceptionPrintMessageWork();
        ParallelWorkReport report =  aNewParallelFlow(work1, exceptionWork, work2, work3).withAutoShutDown(true).policy(WorkExecutePolicy.FAST_FAIL).execute();
        List<Object> results = report.getResult();
        for (int j = 0; j < results.size(); j++) {
            System.out.println("the j:" + j + " the result:" + results.get(j));
        }
    }

    public static void main(String[] args) {
        testParallel1();
    }
}
