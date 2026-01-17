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

import com.alibaba.fastjson2.JSONObject;
import com.baomibing.work.exception.ExceptionEnum;
import com.baomibing.work.exception.WorkFlowException;
import com.baomibing.work.json.JsonPredicate;
import com.baomibing.work.predicate.WorkReportPredicate;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.Strings;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.baomibing.work.util.WorkUtil.assertNotEmpty;
import static com.baomibing.work.util.WorkUtil.assertNotNull;

public abstract class AbstractOperatorPredicate implements Predicate<Object> {
    protected Object report;
    protected Object left;
    protected Object right;

    protected boolean isCollection(Object o) {
        return o instanceof Collection;
    }

    protected boolean isString(Object o) {
        return String.class.isAssignableFrom(o.getClass());
    }

    protected boolean isEnum(Object o) {
        return Enum.class.isAssignableFrom(o.getClass());
    }

    protected boolean isMap(Object o) {
        return o instanceof Map;
    }

    protected boolean isPrimitive(Object o) {
        return Primitives.isWrapperType(o.getClass()) || isString(o);
    }

    private  <T> T getProperty(Object bean, String propertyName) {
        assertNotNull(bean, ExceptionEnum.PROPERTY_BEAN_NOT_BE_NULL);
        assertNotEmpty(propertyName, ExceptionEnum.PROPERTY_NAME_NOT_BE_EMPTY);
        try {
            Field field = FieldUtils.getField(bean.getClass(), propertyName, true);
            return (T) field.get(bean);
        } catch (Exception e) {
            throw new WorkFlowException(ExceptionEnum.GET_PROPERTY_VALUE_OCCUR_AN_EXCEPTION, e.getMessage());
        }
    }

    protected List<JsonPredicate> right2Predicates() {
        if (!isCollection(right)) {
            throw new WorkFlowException(ExceptionEnum.VALUE_IN_AND_OR_MUST_BE_AN_COLLECTION);
        }
        Collection<JSONObject> collection = (Collection) right;
        if (Checker.BeEmpty(collection)) {
            throw new WorkFlowException(ExceptionEnum.VALUES_IN_AND_OR_NOT_BE_EMPTY);
        }
        List<JsonPredicate> predicates = collection.stream().map(json -> json.toJavaObject(JsonPredicate.class)).filter(p -> {
            if (Checker.BeEmpty(p.getOperator())) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        if (Checker.BeEmpty(predicates)) {
            throw new WorkFlowException(ExceptionEnum.VALUES_IN_AND_OR_NOT_BE_VALID);
        }
        return predicates;
    }

    public WorkReportPredicate toWorkReportPredicate() {
        return workReport ->  this.test(workReport);
    }

    public Object get(Object o) {
        if (Checker.BeNull(o)) {
            return null;
        }
        if (isPrimitive(o)) {
            if (isString(o)) {
                String s =  o.toString();
                if (s.startsWith(Strings.DOLLAR)) {
                    //$result.name
                    if (s.contains(Strings.DOT)) {
                        List<String> fields =  Splitter.on(Strings.DOT).trimResults().splitToList(s);
                        for (String field : fields) {
                            Object fieldValue = get(field);
                            if (Checker.BeNull(fieldValue)) {
                                return null;
                            }
                            report = get(field);
                        }
                        return get(report);
                    }
                    s = s.replace(Strings.DOLLAR, "");
                    return get(getProperty(report,s));
                }
                return s;
            } else if (Boolean.class.isAssignableFrom(o.getClass())) {
                return (boolean) o;
            } else if (Double.class.isAssignableFrom(o.getClass())) {
                return (double) o;
            }  else if (Integer.class.isAssignableFrom(o.getClass())) {
                return (int) o;
            }  else if (Long.class.isAssignableFrom(o.getClass())) {
                return (long) o;
            }   else if (Short.class.isAssignableFrom(o.getClass())) {
                return (short) o;
            } else if (Float.class.isAssignableFrom(o.getClass())) {
                return (float) o;
            }
        } else if (isMap(o)) {
            return (Map) o;
        } else if (isCollection(o)) {
            return Lists.newArrayList(o);
        } else if (isEnum(o)) {
            Enum e = (Enum) o;
            return e.name();
        }
       return o;
    }
}
