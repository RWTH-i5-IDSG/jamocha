/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package org.jamocha.languages.common.errors;

import lombok.Getter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class VariableNotDeclaredError extends Error {
    private static final long serialVersionUID = -5557376783613225289L;

    @Getter
    final String var;

    public VariableNotDeclaredError(final String var) {
        super();
        this.var = var;
    }

    protected VariableNotDeclaredError(final String var, final String message, final Throwable cause,
            final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.var = var;
    }

    public VariableNotDeclaredError(final String var, final String message, final Throwable cause) {
        super(message, cause);
        this.var = var;
    }

    public VariableNotDeclaredError(final String var, final String message) {
        super(message);
        this.var = var;
    }

    public VariableNotDeclaredError(final String var, final Throwable cause) {
        super(cause);
        this.var = var;
    }
}
