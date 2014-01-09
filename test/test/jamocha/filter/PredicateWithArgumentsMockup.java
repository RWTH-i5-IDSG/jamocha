package test.jamocha.filter;

import java.util.ArrayList;
import java.util.Collection;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.GenericWithArgumentsComposite.LazyObject;
import org.jamocha.filter.Path;
import org.jamocha.filter.PredicateWithArguments;

@RequiredArgsConstructor
public class PredicateWithArgumentsMockup implements PredicateWithArguments {

	final private boolean returnValue;
	final private Path[] paths;

	@Override
	public SlotType getReturnType() {
		return SlotType.BOOLEAN;
	}

	@Override
	public SlotType[] getParamTypes() {
		return SlotType.empty;
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		return new LazyObject(returnValue);
	}

	@Override
	public Boolean evaluate(final Object... params) {
		return returnValue;
	}

	@Override
	public PredicateWithArguments translatePath(final ArrayList<SlotInFactAddress> addressesInTarget) {
		return this;
	}

	@Override
	public <T extends Collection<Path>> T gatherPaths(final T paths) {
		for (Path path : this.paths) {
			paths.add(path);
		}
		return paths;
	}

	@Override
	public <T extends Collection<SlotInFactAddress>> T gatherCurrentAddresses(final T paths) {
		return paths;
	}

	@Override
	public boolean canEqual(final Object other) {
		return other instanceof PredicateWithArgumentsMockup;
	}

	@Override
	public boolean equalsInFunction(final FunctionWithArguments function) {
		if (!(function instanceof PredicateWithArgumentsMockup))
			return false;
		PredicateWithArgumentsMockup fwam = (PredicateWithArgumentsMockup) function;
		return (fwam.returnValue == this.returnValue && fwam.paths.length == this.paths.length);
	}
}