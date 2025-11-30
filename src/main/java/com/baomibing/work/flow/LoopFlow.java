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
import com.baomibing.work.util.Checker;
import com.baomibing.work.work.LoopWorkReport;
import com.baomibing.work.work.Work;
import com.baomibing.work.work.WorkReport;

import java.util.Arrays;
import java.util.List;

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

    private final List<Work> works;

    private WorkReportPredicate breakPredicate;

    private WorkReportPredicate continuePredicate;

    private LoopFlow(List<Work> works) {
        this.works = works;
    }

    @Override
    public WorkReport execute(WorkContext context) {
        try {
            LoopWorkReport report = new LoopWorkReport();
            report.setLength(works.size());
            for (int i = 0; i < works.size(); i++) {
                report.setIndex(i);
                Work work = works.get(i);
                if (Checker.BeNotNull(breakPredicate)) {
                    if (breakPredicate.apply(report)) {
                        break;
                    }
                }
                if (Checker.BeNotNull(continuePredicate)) {
                    if (continuePredicate.apply(report)) {
                        continue;
                    }
                }
                WorkReport defaultReport = doSingleWork(work, context);
                report.with(defaultReport);
            }
            return doThenWork(report);
        } finally {
            doLastWork();
        }
    }

    @Override
    public WorkReport execute() {
        return doDefaultExecute(this);
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
