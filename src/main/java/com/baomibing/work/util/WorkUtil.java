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
import com.baomibing.work.work.Work;

public class WorkUtil {

    public static Class<?> loadClass(String className) {
        try {
            return java.lang.Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new WorkFlowException(ExceptionEnum.CAN_NOT_LOAD_WORK_BY_CLASS);
        }
    }

    public static <T> void  assertNotNull(T reference, ExceptionEnum exceptionEnum) {
        if (Checker.BeNull(reference)) {
            throw new WorkFlowException(exceptionEnum);
        }
    }

    public static void assertNotEmpty(String s, ExceptionEnum exceptionEnum ) {
        if (Checker.BeEmpty(s)) {
            throw new WorkFlowException(exceptionEnum);
        }
    }

    public static void assertNotEmpty(Iterable s, ExceptionEnum exceptionEnum ) {
        if (Checker.BeEmpty(s)) {
            throw new WorkFlowException(exceptionEnum);
        }
    }

    public static void assertSubWork(Class<?> clazz) {
        if (clazz == null) {
            throw new WorkFlowException(ExceptionEnum.WORK_CLASS_NOT_BE_NULL);
        }
        if (!Work.class.isAssignableFrom(clazz)) {
            throw new  WorkFlowException(ExceptionEnum.CLASS_IS_NOT_ASSIGNABLE_FROM_WORK, clazz.getName());
        }

    }
}
