package org.jamocha.dn.compiler.ecblocks;

import org.jamocha.visitor.Visitor;

interface FilterVisitor extends Visitor {
	public void visit(final Filter filter);

	public void visit(final FilterProxy filter);
}