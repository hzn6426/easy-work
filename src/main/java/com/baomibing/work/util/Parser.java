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
import com.baomibing.work.json.JsonPredicate;
import com.baomibing.work.json.OperatorEnum;
import com.baomibing.work.operator.*;
import com.baomibing.work.predicate.WorkReportPredicate;
import org.apache.commons.lang3.EnumUtils;

public class Parser {

    public static WorkReportPredicate parse(JsonPredicate jsonPredicate) {
        String operator = jsonPredicate.getOperator();
        OperatorEnum operatorEnum = EnumUtils.getEnum(OperatorEnum.class, operator);
        if (Checker.BeNull(operatorEnum)) {
            throw new WorkFlowException(ExceptionEnum.NOT_AN_VALID_OPERATOR_ENUM_VALUE, operator);
        }
        WorkReportPredicate predicate = null;
        switch (operatorEnum) {
            case eq:
                predicate = eqPredicate(jsonPredicate);
                break;
            case ne:
                predicate = nePredicate(jsonPredicate);
                break;
                case lt:
                predicate = ltPredicate(jsonPredicate);
                break;
            case le:
                predicate = lePredicate(jsonPredicate);
                break;
            case gt:
                predicate = gtPredicate(jsonPredicate);
                break;
            case ge:
                predicate = gePredicate(jsonPredicate);
                break;
            case ncontains:
                predicate = ncontainsPredicate(jsonPredicate);
                break;
            case contains:
                predicate = containsPredicate(jsonPredicate);
                break;
            case empty:
                predicate = emptyPredicate(jsonPredicate);
                break;
            case nempty:
                predicate = nemptyPredicate(jsonPredicate);
                break;
            case and:
                predicate = and(jsonPredicate);
                break;
            case or:
                predicate = or(jsonPredicate);
                break;
            default:
                throw new WorkFlowException(ExceptionEnum.NOT_SUPPORT_THE_FUNCTION_OF_OPERATOR, operatorEnum.name());
        }
        return predicate;
    }


    private static WorkReportPredicate eqPredicate(JsonPredicate jsonPredicate) {
        return new EqualThan(jsonPredicate.getLeft(), jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate nePredicate(JsonPredicate jsonPredicate) {
        return new NotEqual(jsonPredicate.getLeft(), jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate gePredicate(JsonPredicate jsonPredicate) {
        return new GreaterEqual(jsonPredicate.getLeft(), jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate lePredicate(JsonPredicate jsonPredicate) {
        return new LessEqual(jsonPredicate.getLeft(), jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate gtPredicate(JsonPredicate jsonPredicate) {
        return new GreaterThan(jsonPredicate.getLeft(), jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate ltPredicate(JsonPredicate jsonPredicate) {
        return new LessThan(jsonPredicate.getLeft(), jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate containsPredicate(JsonPredicate jsonPredicate) {
        return new Contains(jsonPredicate.getLeft(), jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate ncontainsPredicate(JsonPredicate jsonPredicate) {
        return new NotContains(jsonPredicate.getLeft(), jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate emptyPredicate(JsonPredicate jsonPredicate) {
        return new Empty(jsonPredicate.getLeft()).toWorkReportPredicate();
    }

    private static WorkReportPredicate nemptyPredicate(JsonPredicate jsonPredicate) {
        return new NotEmpty(jsonPredicate.getLeft()).toWorkReportPredicate();
    }

    private static WorkReportPredicate and(JsonPredicate jsonPredicate) {
        return new And( jsonPredicate.getRight()).toWorkReportPredicate();
    }

    private static WorkReportPredicate or(JsonPredicate jsonPredicate) {
        return new Or(jsonPredicate.getRight()).toWorkReportPredicate();
    }
}
