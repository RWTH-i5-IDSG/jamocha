package org.jamocha.dn.memory.javaimpl;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.dn.memory.MemoryHandler;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public abstract class MemoryHandlerTemp extends MemoryHandlerBase implements
		org.jamocha.dn.memory.MemoryHandlerTemp {

	final MemoryHandlerMain originatingMainHandler;

	protected MemoryHandlerTemp(final MemoryHandlerMain originatingMainHandler,
			final ArrayList<FactTuple> rows) {
		this(originatingMainHandler.template, originatingMainHandler, rows);
	}

	protected MemoryHandlerTemp(final Template[] template,
			final MemoryHandlerMain originatingMainHandler, final ArrayList<FactTuple> rows) {
		super(template, rows);
		this.originatingMainHandler = originatingMainHandler;
	}

	public List<MemoryHandler> splitIntoChunksOfSize(final int size) {
		final List<MemoryHandler> memoryHandlers = new ArrayList<>();
		if (size >= this.size()) {
			memoryHandlers.add(this);
			return memoryHandlers;
		}
		final int max = this.size();
		int current = 0;
		while (current < max) {
			final ArrayList<FactTuple> facts = new ArrayList<>();
			for (int i = 0; i < size && current + i < max; ++i) {
				facts.add(this.rows.get(current + i));
			}
			memoryHandlers.add(new MemoryHandlerBase(getTemplate(), facts));
			current += size;
		}
		return memoryHandlers;
	}

	protected static boolean applyFilterElement(final Fact fact, final AddressFilterElement element) {
		// determine parameters
		final SlotInFactAddress addresses[] = element.getAddressesInTarget();
		final int paramLength = addresses.length;
		final Object params[] = new Object[paramLength];
		for (int i = 0; i < paramLength; ++i) {
			final SlotInFactAddress address = addresses[i];
			params[i] = fact.getValue(address.getSlotAddress());
		}
		// check filter
		return element.getFunction().evaluate(params);
	}
}
