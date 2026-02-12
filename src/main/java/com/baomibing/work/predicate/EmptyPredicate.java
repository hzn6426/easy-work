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
import com.baomibing.work.util.Checker;

import java.util.Collection;
import java.util.Map;

import static com.baomibing.work.util.ClassUtil.isCollection;
import static com.baomibing.work.util.ClassUtil.isMap;

public class EmptyPredicate <T, R> extends AbstractComparePredicate {

    public EmptyPredicate(String express, SFunction<T, R> left) {
        this.left = left;
        this.expression = express;
    }

    public EmptyPredicate(SFunction<T, R> left) {
        this.left = left;
    }

    public EmptyPredicate(String express) {
        this.left = (Object o) -> o;
        this.expression = express;
    }

    public static <T,R> EmptyPredicate aNewEmptyPredicate(String express, SFunction<T, R> left) {
        return new EmptyPredicate(express, left);
    }

    public static <T,R> EmptyPredicate aNewEmptyPredicate(SFunction<T, R> left) {
        return new EmptyPredicate(left);
    }

    public static <T,R> EmptyPredicate aNewEmptyPredicate(String express) {
        return new EmptyPredicate(express);
    }

    @Override
    public JsonPredicate toJsonPredicate() {
        return convertToJsonPredicate(OperatorEnum.empty);
    }


    @Override
    public boolean apply(WorkReport workReport) {
        assignTarget(workReport);
        Object l = left.apply(target);
        if (Checker.BeNull(l)) {
            return true;
        }
        if (isCollection(l)) {
            Collection collection = (Collection) l;
            return Checker.BeEmpty(collection);
        } else if (isMap(l)) {
            Map map = (Map) l;
            return Checker.BeEmpty(map);
        } else {
            return Checker.BeNull(l);
        }
    }
}
