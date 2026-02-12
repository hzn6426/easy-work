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
import com.baomibing.work.util.Strings;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import static com.baomibing.work.util.ClassUtil.*;
import static com.baomibing.work.util.LambdaUtil.getFieldName;

public abstract class AbstractComparePredicate<T, R> implements WorkReportJsonPredicate{

    protected String expression;
    protected SFunction<T, R> left;
    protected Object right;
    protected Object target;

    protected void assignTarget(WorkReport workReport) {
        target = workReport;
        if (Checker.BeNotEmpty(expression)) {
            target = get(expression);
        }
    }

    protected JsonPredicate convertToJsonPredicate(OperatorEnum operator) {
        String fieldName = getFieldName(left);
        if (Checker.BeNotEmpty(expression)) {
            fieldName = expression + "." + fieldName;
        }
        return new JsonPredicate(fieldName, operator.name(), right);
    }


    protected Object get(Object o) {
        if (Checker.BeNull(o)) {
            return null;
        }
        if (isPrimitive(o)) {
            if (isString(o)) {
                String s =  o.toString();
                if (s.startsWith(Strings.DOLLAR)) {
                    //$result.$name
                    if (s.contains(Strings.DOT)) {
                        List<String> fields =  Splitter.on(Strings.DOT).trimResults().splitToList(s);
                        for (String field : fields) {
                            Object fieldValue = get(field);
                            if (Checker.BeNull(fieldValue)) {
                                return null;
                            }
                            target = get(field);
                        }
                        return get(target);
                    }
                    s = s.replace(Strings.DOLLAR, "");
                    return get(getProperty(target,s));
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
        } else if (isSFunction(o)) {
            SFunction sf = (SFunction) o;
            return sf.apply(target);
        }
        return o;
    }
}
