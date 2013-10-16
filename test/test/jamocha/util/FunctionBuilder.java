package test.jamocha.util;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.ConstantLeaf;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.FunctionWithArgumentsComposite;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathLeaf;

public class FunctionBuilder {
	final Function<?> function;
	final List<FunctionWithArguments> args = new LinkedList<>();

	public FunctionBuilder(final Function<?> function) {
		this.function = function;
	}

	public FunctionBuilder addPath(final Path path, final SlotAddress slot) {
		final SlotType[] paramTypes = this.function.getParamTypes();
		if (paramTypes.length != this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size() + 1] != path.getTemplateSlotType(slot)) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(new PathLeaf(path, slot));
		return this;
	}

	public FunctionBuilder addConstant(final Object value, final SlotType type) {
		final SlotType[] paramTypes = this.function.getParamTypes();
		if (paramTypes.length != this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size() + 1] != type) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(new ConstantLeaf(value, type));
		return this;
	}

	public FunctionBuilder addFunction(final FunctionWithArguments function) {
		final SlotType[] paramTypes = this.function.getParamTypes();
		if (paramTypes.length != this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size() + 1] != function.getReturnType()) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(function);
		return this;
	}

	public FunctionWithArguments build() {
		if (this.function.getParamTypes().length != this.args.size()) {
			throw new IllegalArgumentException("Wrong number of arguments!");
		}
		return new FunctionWithArgumentsComposite(this.function,
				this.args.toArray(new FunctionWithArguments[this.args.size()]));
	}
}