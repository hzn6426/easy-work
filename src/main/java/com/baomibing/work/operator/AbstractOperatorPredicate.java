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
import com.baomibing.work.predicate.WorkReportJsonPredicate;
import com.baomibing.work.util.Checker;
import com.baomibing.work.util.Strings;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.baomibing.work.util.ClassUtil.*;

public abstract class AbstractOperatorPredicate implements Predicate<Object> {
    protected Object report;
    protected Object left;
    protected Object right;


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

    public abstract WorkReportJsonPredicate toWorkReportPredicate();

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
