package test.jamocha.filter;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;
import org.jamocha.filter.Path;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.filter.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.filter.fwa.GenericWithArgumentsComposite.LazyObject;
import org.jamocha.filter.fwa.PredicateWithArguments;

public class PredicateWithArgumentsMockup implements PredicateWithArguments {

	final private boolean returnValue;
	final private Path[] paths;

	public PredicateWithArgumentsMockup(boolean returnValue, Path[] paths) {
		super();
		this.returnValue = returnValue;
		this.paths = paths;
	}

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
	public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
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

	@Override
	public int hashPositionIsIrrelevant() {
		return FunctionWithArguments.hash(paths, (final Path path) -> {
			return path.getTemplate().hashCode();
		}, FunctionWithArguments.positionIsIrrelevant);
	}

	@Override
	public int hashPositionIsRelevant() {
		return FunctionWithArguments.hash(paths, (final Path path) -> {
			return path.getTemplate().hashCode();
		}, FunctionWithArguments.positionIsRelevant);
	}
}