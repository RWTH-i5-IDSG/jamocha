package org.jamocha.rete.functions;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class JavaFunctions implements FunctionGroup {

	private ArrayList funcs = new ArrayList();
	
	public JavaFunctions() {
		super();
	}
	
	public String getName() {
		return (JavaFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		ClassnameResolver classnameResolver = new ClassnameResolver(engine);
		LoadPackageFunction loadpkg = new LoadPackageFunction(classnameResolver);
		engine.declareFunction(loadpkg);
		funcs.add(loadpkg);
		NewFunction nf = new NewFunction(classnameResolver);
		engine.declareFunction(nf);
		funcs.add(nf);
		MemberFunction mf = new MemberFunction(classnameResolver);
		engine.declareFunction(mf);
		funcs.add(mf);
		InstanceofFunction iof = new InstanceofFunction(classnameResolver);
		engine.declareFunction(iof);
		funcs.add(iof);
			}

	public List listFunctions() {
		return funcs;
	}


}
