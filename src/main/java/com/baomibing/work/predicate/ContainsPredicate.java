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

import com.baomibing.work.exception.ExceptionEnum;
import com.baomibing.work.exception.WorkFlowException;
import com.baomibing.work.json.JsonPredicate;
import com.baomibing.work.json.OperatorEnum;
import com.baomibing.work.json.SFunction;
import com.baomibing.work.report.WorkReport;
import com.baomibing.work.util.Checker;

import java.util.Collection;

import static com.baomibing.work.util.ClassUtil.isCollection;
import static com.baomibing.work.util.ClassUtil.isPrimitive;

public class ContainsPredicate <T, R> extends AbstractComparePredicate {

    public ContainsPredicate(String express, SFunction<T, R> left, Object right) {
        this.left = left;
        this.right = right;
        this.expression = express;
    }

    public ContainsPredicate(SFunction<T, R> left, Object right) {
        this.left = left;
        this.right = right;
    }

    public ContainsPredicate(String express, Object right) {
        this.left = (Object o) -> o;
        this.expression = express;
        this.right = right;
    }

    public static <T,R> ContainsPredicate aNewContainsPredicate(String express, SFunction<T, R> left, Object right) {
        return new ContainsPredicate(express, left, right);
    }

    public static <T,R> ContainsPredicate aNewContainsPredicate(SFunction<T, R> left, Object right) {
        return new ContainsPredicate(left, right);
    }

    public static <T,R> ContainsPredicate aNewContainsPredicate(String express, Object right) {
        return new ContainsPredicate(express, right);
    }

    @Override
    public JsonPredicate toJsonPredicate() {
        return convertToJsonPredicate(OperatorEnum.contains);
    }


    @Override
    public boolean apply(WorkReport workReport) {
        assignTarget(workReport);
        Object l = left.apply(target);
        Object r = get(right);
        if (Checker.BeNull(l) || Checker.BeNull(r)) {
            return false;
        }
        if (!isCollection(l) || !isPrimitive(r)) {
            throw new WorkFlowException(ExceptionEnum.INVALID_CONTAINS_OPERATION);
        }
        Collection collection = (Collection) l;
        return collection.contains(r);
    }
}
