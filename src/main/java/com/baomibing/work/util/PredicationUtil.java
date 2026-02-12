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

package com.baomibing.work.util;

import com.baomibing.work.exception.ExceptionEnum;
import com.baomibing.work.exception.WorkFlowException;
import com.baomibing.work.predicate.WorkReportJsonPredicate;
import com.baomibing.work.predicate.WorkReportPredicate;

public abstract class PredicationUtil {

    public static WorkReportJsonPredicate toJsonPredicate(WorkReportPredicate predicate) {
        boolean beJsonPredicate = predicate instanceof WorkReportJsonPredicate;
        if (!beJsonPredicate) {
            throw new WorkFlowException(ExceptionEnum.PREDICATE_NOT_A_JSON_PREDICATE);
        }
        return  (WorkReportJsonPredicate) predicate;
    }
}
