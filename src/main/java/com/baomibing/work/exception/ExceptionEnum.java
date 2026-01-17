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

package com.baomibing.work.exception;

public enum ExceptionEnum {
    CAN_NOT_LOAD_RESOURCE_OF_NAME(990, "Can not load resource of name:{0}"),
    NOT_SUPPORT_THE_FUNCTION_OF_OPERATOR(991, "Not support the function of operator:{0}"),
    NOT_AN_VALID_OPERATOR_ENUM_VALUE(992, "Not an valid operation enum value:{0}"),
    GET_PROPERTY_VALUE_OCCUR_AN_EXCEPTION(993,"Get property value occur an exception:{0}"),
    PROPERTY_NAME_NOT_BE_EMPTY(994, "property name can't be empty!"),
    PROPERTY_BEAN_NOT_BE_NULL(995, "Property bean not be null!"),
    WORK_EXECUTION_OCCUR_AN_ERROR(996, "Work execution occur an error:{0}"),
    CLASS_IS_NOT_ASSIGNABLE_FROM_WORK(997,"Class:{0} is not assignable from work!"),
    WORK_CLASS_NOT_BE_NULL(998, "Work class must not be null!"),
    CAN_NOT_LOAD_WORK_BY_CLASS(999, "Can not load work by class!"),
    TYPE_IS_NOT_AN_VALID_WORK_FLOW_TYPE(1000, "Type is not an valid work flow type!"),
    TYPE_IS_NOT_AN_VALID_WORK_TYPE(1001, "Type is not an valid work type!"),
    FLOW_JSON_NOT_BE_NULL(1002, "Flow json must not be null!"),
    WORK_JSON_NOT_BE_NULL(1003, "Work json must not be null!"),
    WORKS_NOT_BE_NULL_OR_EMPTY(1004, "Works must not be null or empty!"),
    PREDICATE_OR_TIMES_NOT_BE_NULL(1005, "Predicate or times must not be null!"),
    DECIDE_WORK_NOT_BE_NULL(1006, "Decide work must not be null!"),
    TRUE_WORK_NOT_BE_NULL(1007, "True work must not be null!"),
    PREDICATE_NOT_BE_NULL(1008, "Predicate must not be null!"),
    WHEN_NOT_BE_NULL_OR_EMPTY(1009, "When must not be null or empty!"),
    PREDICATE_OR_WORK_NOT_BE_NULL(2000, "Predicate or work must not be null!"),
    WORK_REPORTS_NOT_BE_EMPTY(2001, "Work reports must not be empty!"),
    INVALID_CONTAINS_OPERATION(2002, "Invalid contains operation, left or right is invalid!"),
    VALUE_IN_AND_OR_MUST_BE_AN_COLLECTION(2003, "Value in 'and' 'or' Must be an collection!"),
    VALUES_IN_AND_OR_NOT_BE_EMPTY(2004, "Values in 'and' 'or' Must not be empty!"),
    VALUES_IN_AND_OR_NOT_BE_VALID(2005, "Values in 'and' 'or' Must be valid!"),

    ;

    private int code;

    private String message;


    public int getExceptionCode() {
        return this.code;
    }


    public String getExceptionMessage() {
        return this.message;
    }



    ExceptionEnum(final int code, final String message){
        this.code = code;
        this.message = message;
    }
}
