/*
 * Copyright (c) 2025-2026, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.flow.WorkFlow;
import com.baomibing.work.report.ParallelWorkReport;
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
        WorkFlow flow = aNewParallelFlow(work1,  exceptionWork, work2, work3).withAutoShutDown(true).policy(WorkExecutePolicy.FAST_EXCEPTION);
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
        ParallelWorkReport report =  aNewParallelFlow(work1, exceptionWork, work2, work3).withAutoShutDown(true).policy(WorkExecutePolicy.FAST_SUCCESS).execute();
        List<Object> results = report.getResult();
        for (int j = 0; j < results.size(); j++) {
            System.out.println("the j:" + j + " the result:" + results.get(j));
        }
    }

    private static void testParallel5() {
        ParallelPrintMessageWork work1 = new ParallelPrintMessageWork("one");
        ParallelPrintMessageWork work2 = new ParallelPrintMessageWork("two", 3);
        ParallelPrintMessageWork work3 = new ParallelPrintMessageWork("three");
        ExceptionPrintMessageWork exceptionWork = new ExceptionPrintMessageWork();
        ParallelWorkReport report =  aNewParallelFlow(work1, exceptionWork, work2, work3).withAutoShutDown(true).policy(WorkExecutePolicy.FAST_ALL_SUCCESS).execute();
        List<Object> results = report.getResult();
        for (int j = 0; j < results.size(); j++) {
            System.out.println("the j:" + j + " the result:" + results.get(j));
        }
    }

    private static void testParallel6() {
        ParallelPrintMessageWork work1 = new ParallelPrintMessageWork("one");
        ParallelPrintMessageWork work2 = new ParallelPrintMessageWork("two", 3);
        ParallelPrintMessageWork work3 = new ParallelPrintMessageWork("three");
        ExceptionPrintMessageWork exceptionWork = new ExceptionPrintMessageWork();
        ParallelWorkReport report =  aNewParallelFlow(work1, exceptionWork, work2, work3).withAutoShutDown(true).policy(WorkExecutePolicy.FAST_ALL).execute();
        List<Object> results = report.getResult();
        for (int j = 0; j < results.size(); j++) {
            System.out.println("the j:" + j + " the result:" + results.get(j));
        }
    }

    public static void main(String[] args) {
//        testParallel1();
//        testParallel2();//FAST_EXCEPTION
        testParallel3();//FAST_FAIL
//        testParallel4();//FAST_SUCCESS
//        testParallel5(); //FAST_ALL_SUCCESS
//        testParallel6();// FAST_ALL
    }
}
