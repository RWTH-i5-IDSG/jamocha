package org.jamocha.adapter.sl;

import org.jamocha.adapter.AdapterTranslationException;
import org.jamocha.adapter.sl.performative.AcceptProposal;
import org.jamocha.adapter.sl.performative.Agree;
import org.jamocha.adapter.sl.performative.Cancel;
import org.jamocha.adapter.sl.performative.Cfp;
import org.jamocha.adapter.sl.performative.Confirm;
import org.jamocha.adapter.sl.performative.Disconfirm;
import org.jamocha.adapter.sl.performative.Failure;
import org.jamocha.adapter.sl.performative.Inform;
import org.jamocha.adapter.sl.performative.InformIf;
import org.jamocha.adapter.sl.performative.InformRef;
import org.jamocha.adapter.sl.performative.NotUnderstood;
import org.jamocha.adapter.sl.performative.Propagate;
import org.jamocha.adapter.sl.performative.Propose;
import org.jamocha.adapter.sl.performative.Proxy;
import org.jamocha.adapter.sl.performative.QueryIf;
import org.jamocha.adapter.sl.performative.QueryRef;
import org.jamocha.adapter.sl.performative.Refuse;
import org.jamocha.adapter.sl.performative.RejectProposal;
import org.jamocha.adapter.sl.performative.Request;
import org.jamocha.adapter.sl.performative.RequestWhen;
import org.jamocha.adapter.sl.performative.RequestWhenever;
import org.jamocha.adapter.sl.performative.Subscribe;

public class SL2CLIPS {

	public static String getCLIPS(String performative, String slCode) throws AdapterTranslationException {
		String clipsCode = "";
		if (performative.equalsIgnoreCase("accept-proposal")) {
			clipsCode = AcceptProposal.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("agree")) {
			clipsCode = Agree.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("cancel")) {
			clipsCode = Cancel.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("cfp")) {
			clipsCode = Cfp.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("confirm")) {
			clipsCode = Confirm.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("disconfirm")) {
			clipsCode = Disconfirm.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("failure")) {
			clipsCode = Failure.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("inform")) {
			clipsCode = Inform.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("inform-if")) {
			clipsCode = InformIf.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("inform-ref")) {
			clipsCode = InformRef.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("not-understood")) {
			clipsCode = NotUnderstood.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("propagate")) {
			clipsCode = Propagate.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("propose")) {
			clipsCode = Propose.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("proxy")) {
			clipsCode = Proxy.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("query-if")) {
			clipsCode = QueryIf.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("query-ref")) {
			clipsCode = QueryRef.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("refuse")) {
			clipsCode = Refuse.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("reject-proposal")) {
			clipsCode = RejectProposal.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("request")) {
			clipsCode = Request.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("request-when")) {
			clipsCode = RequestWhen.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("request-whenever")) {
			clipsCode = RequestWhenever.getCLIPS(slCode);
		} else if (performative.equalsIgnoreCase("subscribe")) {
			clipsCode = Subscribe.getCLIPS(slCode);
		}
		return clipsCode;
	}

}
