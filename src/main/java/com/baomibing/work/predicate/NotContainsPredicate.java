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
import com.baomibing.work.json.SFunction;
import com.baomibing.work.report.WorkReport;

public class NotContainsPredicate<T, R> extends ContainsPredicate<T, R> {

    public NotContainsPredicate(String express, SFunction<T, R> left, Object right) {
        super(express, left, right);
    }

    public NotContainsPredicate(SFunction<T, R> left, Object right) {
        super(left, right);
    }

    public NotContainsPredicate(String express, Object right) {
        super(express, right);
    }

    public static <T,R> NotContainsPredicate aNewNotContainsPredicate(String express, SFunction<T, R> left, Object right) {
        return new NotContainsPredicate(express, left, right);
    }

    public static <T,R> NotContainsPredicate aNewNotContainsPredicate(SFunction<T, R> left, Object right) {
        return new NotContainsPredicate(left, right);
    }

    public static <T,R> NotContainsPredicate aNewNotContainsPredicate(String express, Object right) {
        return new NotContainsPredicate(express, right);
    }

    @Override
    public JsonPredicate toJsonPredicate() {
        return convertToJsonPredicate(OperatorEnum.ncontains);
    }

    @Override
    public boolean apply(WorkReport workReport) {
        return !super.apply(workReport);
    }
}
