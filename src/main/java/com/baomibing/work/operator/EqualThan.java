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

package com.baomibing.work.operator;

import com.baomibing.work.json.JsonPredicate;
import com.baomibing.work.json.OperatorEnum;
import com.baomibing.work.predicate.WorkReportJsonPredicate;
import com.baomibing.work.util.Checker;

import java.util.Objects;

public class EqualThan extends AbstractOperatorPredicate {

    public EqualThan(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public WorkReportJsonPredicate toWorkReportPredicate() {
        return () -> new JsonPredicate(left, OperatorEnum.eq.name(),  right);
    }

    @Override
    public boolean test(Object o) {
        this.report = o;
        Object l = get(left);
        Object r = get(right);
        if (Checker.BeNull(l) || Checker.BeNull(r)) {
            return false;
        }
        return  Objects.equals(l, r);
    }
}
