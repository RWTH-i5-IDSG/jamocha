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
package org.jamocha.logging.formatter;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.Bind;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ExchangeableLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.Modify.SlotAndValue;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public final class FunctionWithArgumentsFormatter<L extends ExchangeableLeaf<L>>
        implements Formatter<FunctionWithArguments<L>> {

    private static final FunctionWithArgumentsFormatter<SymbolLeaf> SINGLETON = new FunctionWithArgumentsFormatter<>();

    static FunctionWithArgumentsFormatter<SymbolLeaf> getFunctionWithArgumentsFormatter() {
        return SINGLETON;
    }

    static String formatFwa(final FunctionWithArguments<SymbolLeaf> fwa) {
        return getFunctionWithArgumentsFormatter().format(fwa);
    }

    private FunctionWithArgumentsFormatter() {
    }

    @Override
    public String format(final FunctionWithArguments<L> fwa) {
        final FunctionWithArgumentsFormatterVisitor fwaf = new FunctionWithArgumentsFormatterVisitor();
        fwa.accept(fwaf);
        return fwaf.getString();
    }

    private class FunctionWithArgumentsFormatterVisitor implements FunctionWithArgumentsVisitor<L> {

        private final StringBuffer sb = new StringBuffer();

        FunctionWithArgumentsFormatterVisitor() {
        }

        public String getString() {
            return this.sb.toString();
        }

        private void prettyPrint(final GenericWithArgumentsComposite<?, ?, L> fwa) {
            this.sb.append("(").append(fwa.getFunction().inClips());
            for (final FunctionWithArguments<L> functionWithArguments : fwa.getArgs()) {
                this.sb.append(" ");
                functionWithArguments.accept(this);
            }
            this.sb.append(")");
        }

        @Override
        public void visit(final FunctionWithArgumentsComposite<L> functionWithArgumentsComposite) {
            prettyPrint(functionWithArgumentsComposite);
        }

        @Override
        public void visit(final PredicateWithArgumentsComposite<L> predicateWithArgumentsComposite) {
            prettyPrint(predicateWithArgumentsComposite);
        }

        @Override
        public void visit(final ConstantLeaf<L> constantLeaf) {
            this.sb.append(constantLeaf.toString());
        }

        @Override
        public void visit(final GlobalVariableLeaf<L> globalVariableLeaf) {
            this.sb.append(globalVariableLeaf.toString());
        }

        @Override
        public void visit(final Bind<L> fwa) {
            this.sb.append(fwa.toString());
        }

        @Override
        public void visit(final Assert<L> fwa) {
            this.sb.append(fwa.toString());
        }

        @Override
        public void visit(final TemplateContainer<L> fwa) {
            this.sb.append(fwa.toString());
        }

        @Override
        public void visit(final Retract<L> fwa) {
            this.sb.append(fwa.toString());
        }

        @Override
        public void visit(final Modify<L> fwa) {
            this.sb.append(fwa.toString());
        }

        @Override
        public void visit(final SlotAndValue<L> fwa) {
            this.sb.append(fwa.toString());
        }

        @Override
        public void visit(final L fwa) {
            if (fwa instanceof SymbolLeaf) {
                this.sb.append(((SymbolLeaf) fwa).getSymbol().toString());
            } else {
                this.sb.append(fwa.toString());
            }
        }
    }
}
