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
import com.baomibing.work.json.SFunction;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.report.WorkReport;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Locale;

import static com.baomibing.work.predicate.EqPredicate.aNewEqPredicate;

public abstract class LambdaUtil {

    public static <T> String getFieldName(SFunction<T, ?> func) {
        try {
            if (Checker.BeNull(func)) {
                return Strings.EMPTY;
            }
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) method.invoke(func);
            String name =  lambda.getImplMethodName();
            if (name.startsWith("is")) {
                name = name.substring(2);
            } else if (name.startsWith("get") || name.startsWith("set")) {
                name = name.substring(3);
            }

            if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
                name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
            }
            return name;
        } catch (Exception e) {
            throw new WorkFlowException(ExceptionEnum.ERROR_TO_GET_FIELD_NAME);
        }
    }


    public static void main(String[] args) {

        SFunction<WorkReport, String> f = WorkReport::getWorkName;
//        SFunction<WorkReport, String> f = (WorkReport report) -> report.getWorkName();
        WorkReportPredicate anyNameAPredicate = aNewEqPredicate(WorkReport::getWorkName, "a");
        System.out.printf(getFieldName(WorkReport::getWorkName));

    }

}
