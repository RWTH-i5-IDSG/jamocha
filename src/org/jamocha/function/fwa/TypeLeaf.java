package org.jamocha.function.fwa;

import java.util.Objects;

import lombok.EqualsAndHashCode;
import lombok.Value;

import org.jamocha.dn.memory.SlotType;

@Value
@EqualsAndHashCode(of = { "type" })
public class TypeLeaf implements ExchangeableLeaf<TypeLeaf> {
	final SlotType type;

	final SlotType[] paramTypes;

	public TypeLeaf(final SlotType type) {
		this.type = type;
		this.paramTypes = new SlotType[] { this.type };
	}

	@Override
	public String toString() {
		return type.toString();
	}

	@Override
	public SlotType[] getParamTypes() {
		return this.paramTypes;
	}

	@Override
	public SlotType getReturnType() {
		return this.type;
	}

	@Override
	public org.jamocha.function.Function<?> lazyEvaluate(final org.jamocha.function.Function<?>... params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object evaluate(final Object... params) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return FunctionWithArguments.hash(new int[] { Objects.hash(type) }, FunctionWithArguments.positionIsIrrelevant);
	}

	@Override
	public <V extends FunctionWithArgumentsVisitor<TypeLeaf>> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public ExchangeableLeaf<TypeLeaf> copy() {
		throw new UnsupportedOperationException();
	}
}