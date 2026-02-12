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

import com.baomibing.work.json.JsonPredicate;
import com.baomibing.work.json.OperatorEnum;
import com.baomibing.work.report.WorkReport;

/**
 * All predicate for work report
 *
 * @author zening (316279829@qq.com)
 */
public class AllPredicate extends AbstractSinglePredicate {

    private AllPredicate(WorkReportPredicate predicate) {
        this.predicate = predicate;
    }

    public static AllPredicate allPredicate(WorkReportPredicate predicate) {
        return new AllPredicate(predicate);
    }

    @Override
    public boolean apply(WorkReport workReport) {
        return wrap(workReport).stream().allMatch(report -> predicate.apply(report));
    }

    @Override
    public JsonPredicate toJsonPredicate() {
        WorkReportJsonPredicate workReportJsonPredicate = assertJsonPredicate();
        return new JsonPredicate(null, OperatorEnum.all.name(), workReportJsonPredicate.toJsonPredicate());
    }
}
