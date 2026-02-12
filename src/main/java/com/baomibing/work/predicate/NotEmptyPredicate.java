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

public class NotEmptyPredicate<T,R> extends EmptyPredicate<T,R> {


    public NotEmptyPredicate(String express, SFunction<T,R> left) {
        super(express, left);
    }

    public NotEmptyPredicate(SFunction<T,R> left) {
        super(left);
    }

    public NotEmptyPredicate(String express) {
        super(express);
    }

    public static <T,R> NotEmptyPredicate aNewNotEmptyPredicate(String express, SFunction<T, R> left) {
        return new NotEmptyPredicate(express, left);
    }

    public static <T,R> NotEmptyPredicate aNewNotEmptyPredicate(SFunction<T, R> left) {
        return new NotEmptyPredicate(left);
    }

    public static <T,R> NotEmptyPredicate aNewNotEmptyPredicate(String express) {
        return new NotEmptyPredicate(express);
    }

    @Override
    public JsonPredicate toJsonPredicate() {
        return convertToJsonPredicate(OperatorEnum.nempty);
    }

    @Override
    public boolean apply(WorkReport workReport) {
        return !super.apply(workReport);
    }
}
