/**
 * Copyright (c) 2025-2025, zening (316279828@qq.com).
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
 */
package com.baomibing.work.work;

/**
 * Enum for the workflow execute policy
 *
 * @author zening (316279829@qq.com)
 */
public enum WorkExecutePolicy {
    //when the state FAIL, stop flow and return FAILED, return COMPLETED if not have any FAILED.
    FAST_FAIL,
    //(DEFAULT VALUE) when the state FAIL and exception NOT NULL, stop flow and return FAILED , return COMPLETED if not have any FAILED.
    FAST_FAIL_EXCEPTION,
    //return the FIRST SUCCESS result, return FAILED if not have any success result.
    FAST_SUCCESS,
    //always execute all flow, regardless of whether an exception occurs, always return COMPLETED
    FAST_ALL,
    //return all complete result, return FAILED if not have any success result.
    FAST_ALL_SUCCESS,
    //when an EXCEPTION occurs , throw the exception, return COMPLETED if not have any exception.
    FAST_EXCEPTION
    ;
}
