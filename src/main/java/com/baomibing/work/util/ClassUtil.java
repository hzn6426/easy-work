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
import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import static com.baomibing.work.util.WorkUtil.assertNotEmpty;
import static com.baomibing.work.util.WorkUtil.assertNotNull;

public abstract class ClassUtil {

    public static boolean isCollection(Object o) {
        return o instanceof Collection;
    }

    public static boolean isEnum(Object o) {
        return Enum.class.isAssignableFrom(o.getClass());
    }

    public static boolean isMap(Object o) {
        return o instanceof Map;
    }

    public static boolean isSFunction(Object o) {
        return o instanceof SFunction;
    }

    public static boolean isString(Object o) {
        return String.class.isAssignableFrom(o.getClass());
    }

    public static boolean isPrimitive(Object o) {
        return Primitives.isWrapperType(o.getClass()) || isString(o);
    }

    public static  <T> T getProperty(Object bean, String propertyName) {
        assertNotNull(bean, ExceptionEnum.PROPERTY_BEAN_NOT_BE_NULL);
        assertNotEmpty(propertyName, ExceptionEnum.PROPERTY_NAME_NOT_BE_EMPTY);
        try {
            Field field = FieldUtils.getField(bean.getClass(), propertyName, true);
            return (T) field.get(bean);
        } catch (Exception e) {
            throw new WorkFlowException(ExceptionEnum.GET_PROPERTY_VALUE_OCCUR_AN_EXCEPTION, e.getMessage());
        }
    }
}
