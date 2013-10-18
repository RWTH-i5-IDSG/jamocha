package test.jamocha.util;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.ConstantLeaf;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathLeaf;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.PredicateWithArguments;
import org.jamocha.filter.PredicateWithArgumentsComposite;

public class PredicateBuilder {
	final Predicate predicate;
	final List<FunctionWithArguments> args = new LinkedList<>();

	public PredicateBuilder(final Predicate predicate) {
		this.predicate = predicate;
	}

	public PredicateBuilder addPath(final Path path, final SlotAddress slot) {
		final SlotType[] paramTypes = this.predicate.getParamTypes();
		if (paramTypes.length == this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size()] != path.getTemplateSlotType(slot)) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(new PathLeaf(path, slot));
		return this;
	}

	public PredicateBuilder addConstant(final Object value, final SlotType type) {
		final SlotType[] paramTypes = this.predicate.getParamTypes();
		if (paramTypes.length == this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size()] != type) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(new ConstantLeaf(value, type));
		return this;
	}

	public PredicateBuilder addFunction(final FunctionWithArguments function) {
		final SlotType[] paramTypes = this.predicate.getParamTypes();
		if (paramTypes.length == this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size()] != function.getReturnType()) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(function);
		return this;
	}

	public PredicateWithArguments build() {
		if (this.predicate.getParamTypes().length != this.args.size()) {
			throw new IllegalArgumentException("Wrong number of arguments!");
		}
		return new PredicateWithArgumentsComposite(this.predicate,
				this.args.toArray(new FunctionWithArguments[this.args.size()]));
	}
}