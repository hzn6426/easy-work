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

import com.baomibing.work.report.DefaultWorkReport;
import com.baomibing.work.report.MultipleWorkReport;
import com.baomibing.work.report.WorkReport;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Predicate;

/**
 * Abstract predicate for all and or any none predicate
 *
 * @author zening (316279829@qq.com)
 */
public abstract class AbstractPredicate implements WorkReportPredicate {

    protected Predicate<WorkReport> predicate;

    List<WorkReport> wrap(WorkReport workReport) {
        if (workReport instanceof DefaultWorkReport) {
            return Lists.newArrayList(workReport);
        } else if (workReport instanceof MultipleWorkReport) {
            return ((MultipleWorkReport) workReport).getReports();
        }
        return Lists.newArrayList(workReport);
    }
}
