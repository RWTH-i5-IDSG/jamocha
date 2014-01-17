package test.jamocha.filter;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;
import org.jamocha.filter.GenericWithArgumentsComposite.LazyObject;
import org.jamocha.filter.Path;
import org.jamocha.filter.PredicateWithArguments;
import org.jamocha.filter.Visitor;

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
		return new LazyObject(isReturnValue());
	}

	@Override
	public Boolean evaluate(final Object... params) {
		return isReturnValue();
	}

	@Override
	public <T extends Visitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	/**
	 * @return the paths
	 */
	public Path[] getPaths() {
		return paths;
	}

	/**
	 * @return the returnValue
	 */
	public boolean isReturnValue() {
		return returnValue;
	}

}