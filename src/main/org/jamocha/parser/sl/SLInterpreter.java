package org.jamocha.parser.sl;

import org.jamocha.adapter.sl.configurations.ActionSLConfiguration;
import org.jamocha.adapter.sl.configurations.ConstantSLConfiguration;
import org.jamocha.adapter.sl.configurations.ContentSLConfiguration;
import org.jamocha.adapter.sl.configurations.FunctionCallSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLConfiguration;
import org.jamocha.adapter.sl.configurations.SequenceSLConfiguration;
import org.jamocha.adapter.sl.configurations.FunctionCallOrFactSLConfiguration;

public class SLInterpreter implements SLParserVisitor {

	private SLConfiguration getChildSLConfiguration(Node n, int childIndex) {
		return (SLConfiguration) n.jjtGetChild(childIndex)
				.jjtAccept(this, null);
	}

	public Object visit(SimpleNode node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLContent node, Object data) {
		ContentSLConfiguration cslc = new ContentSLConfiguration();
		for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
			cslc.addExpression(getChildSLConfiguration(node, i));
		}
		return cslc;
	}

	public Object visit(SLContentExpression node, Object data) {
		return getChildSLConfiguration(node, 0);
	}

	public Object visit(SLProposition node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLWff node, Object data) {
		//WffSLConfiguration wslc = new WffSLConfiguration();
		return null;
	}

	public Object visit(SLIdentifyingExpression node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLActionExpression node, Object data) {
		if (node.getName().equals("action")) {
			ActionSLConfiguration aslc = new ActionSLConfiguration();
			aslc.setAgent(getChildSLConfiguration(node, 0));
			aslc.setAction(getChildSLConfiguration(node, 1));
			return aslc;
		} else {
			// TODO
		}
		return null;
	}

	public Object visit(SLAgent node, Object data) {
		return getChildSLConfiguration(node, 0);
	}

	public Object visit(SLSetOrSequence node, Object data) {
		SequenceSLConfiguration sslc = new SequenceSLConfiguration();
		for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
			sslc.addItem(getChildSLConfiguration(node, i));
		}
		return sslc;
	}

	public Object visit(SLParameter node, Object data) {
		// Is skipped. We directly jump into the Subnodes ParameterName and the
		// Value it has
		return null;
	}

	public Object visit(SLUnaryLogicalOp node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLBinaryLogicalOp node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLBinaryTermOp node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLQuantifier node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLModalOp node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLActionOp node, Object data) {
		return getChildSLConfiguration(node, 0);
	}

	public Object visit(SLReferentialOp node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SLPropositionSymbol node, Object data) {
		return getChildSLConfiguration(node, 0);
	}

	public Object visit(SLPredicateSymbol node, Object data) {
		return getChildSLConfiguration(node, 0);
	}

	public Object visit(SLFunctionSymbol node, Object data) {
		return getChildSLConfiguration(node, 0);
	}

	public Object visit(SLBooleanSymbol node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLString node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLInteger node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLFloat node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLDateTime node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLVariable node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLParameterName node, Object data) {
		return new ConstantSLConfiguration(node.getName().substring(1,
				node.getName().length()));
	}

	public Object visit(SLFunctionalTermWithTermOrIE node, Object data) {
		FunctionCallSLConfiguration fcslc = new FunctionCallSLConfiguration();
		fcslc.setFunctionName(getChildSLConfiguration(node, 0));
		for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
			fcslc.addParameter(getChildSLConfiguration(node, i));
		}
		return fcslc;
	}

	public Object visit(SLFunctionalTermWithParameter node, Object data) {
		FunctionCallOrFactSLConfiguration tslc = new FunctionCallOrFactSLConfiguration();
		tslc.setName(getChildSLConfiguration(node, 0));
		for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
			tslc.addSlot(getChildSLConfiguration(node.jjtGetChild(i), 0),
					getChildSLConfiguration(node.jjtGetChild(i), 1));
		}
		return tslc;
	}

}
