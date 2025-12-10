/**
 * Copyright (c) 2025-2025, zening (316279828@qq.com).
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
 */
package com.baomibing.work.flow;


import com.baomibing.work.context.WorkContext;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.report.LoopIndexWorkReport;
import com.baomibing.work.report.LoopWorkReport;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.baomibing.work.report.LoopWorkReport.aNewLoopWorkReport;

/**
 * A loop flow is defined by as follows:
 *
 *  <ul>
 *      <li>works execute in loop</li>
 *      <li>when break predicate apply, break the loop</li>
 *      <li>when continue predicate apply, skip current and execute next loop</li>
 *  </ul>
 *
 * @author zening (316279829@qq.com)
 */
public class LoopFlow extends AbstractWorkFlow {

    private final List<Work> works = new ArrayList<>();

    private WorkReportPredicate breakPredicate;

    private WorkReportPredicate continuePredicate;

    private LoopFlow(List<Work> works) {
        for (Work work : works) {
            this.works.add(wrapNamedPointWork(work));
        }
    }

    public LoopFlow named(String name) {
        this.name = name;
        return this;
    }

    public LoopFlow policy(WorkExecutePolicy workExecutePolicy) {
        this.workExecutePolicy = workExecutePolicy;
        return this;
    }

    @Override
    public LoopFlow context(WorkContext workContext) {
        this.workContext = workContext;
        return this;
    }

    public LoopFlow trace(boolean beTrace) {
        this.beTrace = beTrace;
        return this;
    }

    @Override
    public LoopWorkReport execute() {
        WorkContext context = getDefaultWorkContext();
        LoopIndexWorkReport indexReport = new LoopIndexWorkReport();
        List<WorkReport> reports = new ArrayList<>();
        indexReport.setLength(works.size());
        for (int i = 0, len = works.size(); i < len ; i++) {
            indexReport.setIndex(i);
            Work work = works.get(i);

            if (Checker.BeNotNull(breakPredicate)) {
                if (breakPredicate.apply(indexReport)) {
                    break;
                }
            }
            if (Checker.BeNotNull(continuePredicate)) {
                if (continuePredicate.apply(indexReport)) {
                    continue;
                }
            }
            WorkReport defaultReport = doSingleWork(work, context);
            reports.add(defaultReport);
            indexReport.with(defaultReport);
            if (beBreak(defaultReport)) {
                break;
            }

        }
        traceReport(aNewLoopWorkReport().setWorkName(name).addAllReports(reports));
        return aNewLoopWorkReport(getPolicyReport(reports, context));
    }


    public static LoopFlow aNewLoopFlow(Work... works) {
        return new LoopFlow(Arrays.asList(works));
    }

    public LoopFlow withBreakPredicate(WorkReportPredicate breakPredicate) {
        this.breakPredicate = breakPredicate;
        return this;
    }

    public LoopFlow withContinuePredicate(WorkReportPredicate continuePredicate) {
        this.continuePredicate = continuePredicate;
        return this;
    }


}
