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
package org.jamocha.languages.clips.parser.errors;

import lombok.Getter;

import org.jamocha.languages.clips.parser.generated.SimpleNode;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.errors.TypeMismatchError;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ClipsTypeMismatchError extends TypeMismatchError {
    private static final long serialVersionUID = -236929147590833282L;

    @Getter
    final SimpleNode context;

    public ClipsTypeMismatchError(final Symbol var, final SimpleNode context) {
        super(var);
        this.context = context;
    }

    public ClipsTypeMismatchError(final Symbol var, final SimpleNode context, final String message,
            final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(var, message, cause, enableSuppression, writableStackTrace);
        this.context = context;
    }

    public ClipsTypeMismatchError(final Symbol var, final SimpleNode context, final String message,
            final Throwable cause) {
        super(var, message, cause);
        this.context = context;
    }

    public ClipsTypeMismatchError(final Symbol var, final SimpleNode context, final String message) {
        super(var, message);
        this.context = context;
    }

    public ClipsTypeMismatchError(final Symbol var, final SimpleNode context, final Throwable cause) {
        super(var, cause);
        this.context = context;
    }
}
