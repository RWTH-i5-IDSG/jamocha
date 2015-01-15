package org.jamocha.function.fwa;

import lombok.EqualsAndHashCode;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.Filter;
import org.jamocha.function.Function;

/**
 * This class stores the {@link SlotType} of the represented Slot only. All other relevant
 * information are stored in the containing {@link AddressFilterElement}. A {@link PathLeaf} is
 * translated into a {@link ParameterLeaf} as soon as the {@link Node} representing the surrounding
 * {@link Filter} has been created. In doing so, the containing {@link AddressFilterElement} stores
 * the corresponding {@link SlotInFactAddress} in
 * {@link AddressFilterElement#getAddressesInTarget()}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see AddressFilterElement
 * @see SlotInFactAddress
 */
@EqualsAndHashCode
public class ParameterLeaf implements ExchangeableLeaf<ParameterLeaf> {
	private final SlotType slotType;
	private final SlotType[] slotTypes;
	private final int hashCode;

	public ParameterLeaf(final SlotType type, final int hashCode) {
		super();
		this.slotType = type;
		this.slotTypes = new SlotType[] { type };
		this.hashCode = hashCode;
	}

	@Override
	public String toString() {
		return "[" + slotType + "]";
	}

	@Override
	public SlotType[] getParamTypes() {
		return slotTypes;
	}

	@Override
	public SlotType getReturnType() {
		return slotType;
	}

	@Override
	public Function<?> lazyEvaluate(final Function<?>... params) {
		return params[0];
	}

	@Override
	public Object evaluate(final Object... params) {
		return params[0];
	}

	@Override
	public <T extends FunctionWithArgumentsVisitor<ParameterLeaf>> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	/**
	 * @return the slot type
	 */
	public SlotType getType() {
		return slotType;
	}

	@Override
	public int hashPositionIsIrrelevant() {
		return hashCode;
	}

	@Override
	public ExchangeableLeaf<ParameterLeaf> copy() {
		return new ParameterLeaf(slotType, hashCode);
	}
}