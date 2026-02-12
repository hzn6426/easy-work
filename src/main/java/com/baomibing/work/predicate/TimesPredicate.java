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
package com.baomibing.work.predicate;


import com.baomibing.work.report.WorkReport;
import com.baomibing.work.util.Checker;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * A predicate implement for repeat times
 *
 * @author zening (316279829@qq.com)
 */
public class TimesPredicate implements WorkReportPredicate {

    @Getter
    private final int times;

    private final AtomicInteger counter = new AtomicInteger();

    private WorkReportPredicate workReportPredicate;

    public TimesPredicate(int times) {
        this.times = times;
    }

    /**
     * When timesPredicate is true stop ,otherwise execute times
     *
     * @param times  execute times
     * @param timesPredicate the predicate which stop the flow
     */
    public TimesPredicate(int times, WorkReportPredicate timesPredicate) {
        this.times = times;
        this.workReportPredicate = timesPredicate;
    }

    @Override
    public boolean apply(WorkReport workReport) {
        boolean beCountTrue = counter.incrementAndGet() == times;
        if (Checker.BeNotNull(workReportPredicate) && workReportPredicate.apply(workReport)) {
            return true;
        }
        return beCountTrue;

    }

    public static TimesPredicate times(int times) {
        return new TimesPredicate(times);
    }

    public static TimesPredicate times(int times, WorkReportPredicate reportPredicate) {
        return new TimesPredicate(times, reportPredicate);
    }

}
