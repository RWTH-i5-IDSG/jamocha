/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.parser.sl;

import org.jamocha.adapter.sl.configurations.ActionSLConfiguration;
import org.jamocha.adapter.sl.configurations.ConnectedActionSLConfiguration;
import org.jamocha.adapter.sl.configurations.ConstantSLConfiguration;
import org.jamocha.adapter.sl.configurations.ContentSLConfiguration;
import org.jamocha.adapter.sl.configurations.FunctionCallOrFactSLConfiguration;
import org.jamocha.adapter.sl.configurations.FunctionCallSLConfiguration;
import org.jamocha.adapter.sl.configurations.IdentifyingExpressionSLConfiguration;
import org.jamocha.adapter.sl.configurations.SLConfiguration;
import org.jamocha.adapter.sl.configurations.SequenceSLConfiguration;
import org.jamocha.adapter.sl.configurations.WffSLConfiguration;

public class SLInterpreter implements SLParserVisitor {

	private SLConfiguration getChildSLConfiguration(Node n, int childIndex) {
		return (SLConfiguration) n.jjtGetChild(childIndex)
				.jjtAccept(this, null);
	}

	public Object visit(SimpleNode node, Object data) {
		// not needed
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
		return getChildSLConfiguration(node, 0);
	}

	public Object visit(SLWff node, Object data) {
		WffSLConfiguration wslc = new WffSLConfiguration();
		SimpleNode firstChild = (SimpleNode) node.jjtGetChild(0);
		if (firstChild instanceof SLPropositionSymbol
				|| firstChild instanceof SLBooleanSymbol) {
			wslc.setBraces(false);
		} else {
			wslc.setBraces(true);
		}
		for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
			wslc.addExpression(getChildSLConfiguration(node, i));
		}
		return wslc;
	}

	public Object visit(SLIdentifyingExpression node, Object data) {
		IdentifyingExpressionSLConfiguration ieslc = new IdentifyingExpressionSLConfiguration();
		ieslc.setRefOp(getChildSLConfiguration(node, 0));
		ieslc.setTermOrIE(getChildSLConfiguration(node, 1));
		ieslc.setWff(getChildSLConfiguration(node, 2));
		return ieslc;
	}

	public Object visit(SLActionExpression node, Object data) {
		if (node.getName().equals("action")) {
			ActionSLConfiguration aslc = new ActionSLConfiguration();
			aslc.setAgent(getChildSLConfiguration(node, 0));
			aslc.setAction(getChildSLConfiguration(node, 1));
			return aslc;
		} else {
			ConnectedActionSLConfiguration caslc = new ConnectedActionSLConfiguration();
			caslc.setConnector(node.getName());
			caslc.setFirstAction(getChildSLConfiguration(node, 0));
			caslc.setSecondAction(getChildSLConfiguration(node, 1));
			return caslc;
		}
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
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLBinaryLogicalOp node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLBinaryTermOp node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLQuantifier node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLModalOp node, Object data) {
		return new ConstantSLConfiguration(node.getName());
	}

	public Object visit(SLActionOp node, Object data) {
		return getChildSLConfiguration(node, 0);
	}

	public Object visit(SLReferentialOp node, Object data) {
		return new ConstantSLConfiguration(node.getName());
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
