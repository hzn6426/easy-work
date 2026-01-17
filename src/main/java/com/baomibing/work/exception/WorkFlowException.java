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

import com.baomibing.work.util.Checker;
import lombok.Getter;

import java.text.MessageFormat;

public class WorkFlowException extends RuntimeException {

    @Getter
    protected int code;
    @Getter
    protected String codeMessage;

    public WorkFlowException(ExceptionEnum exceptionEnum) {
        super(MessageFormat.format(exceptionEnum.getExceptionMessage(), new Object[] {"","","",""}));
        codeMessage = super.getMessage();
        this.code = exceptionEnum.getExceptionCode();
    }

    public WorkFlowException(ExceptionEnum exceptionEnum, Object... arguments) {
        super(MessageFormat.format(exceptionEnum.getExceptionMessage(),
            Checker.BeEmpty(arguments) ? new Object[] { "", "", "", "" } : arguments));
        codeMessage = super.getMessage();
        this.code = exceptionEnum.getExceptionCode();
    }
}
