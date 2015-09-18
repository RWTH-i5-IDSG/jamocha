package org.jamocha.dn.compiler.ecblocks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.compiler.ecblocks.ECBlocks.Element;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
class ElementPartition extends Partition<Element, Partition.SubSet<Element>> {
	public ElementPartition(final Partition<Element, Partition.SubSet<Element>> copy) {
		super(copy);
	}
}