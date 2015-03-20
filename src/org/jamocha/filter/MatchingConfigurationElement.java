package org.jamocha.filter;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;

@Data
@AllArgsConstructor
public class MatchingConfigurationElement implements Comparable<MatchingConfigurationElement> {
	final SlotAddress address;
	final Optional<?> constant;
	final boolean single;

	public MatchingConfigurationElement(final SlotAddress address, final Optional<?> constant, final Template template) {
		this(address, constant, !address.getSlotType(template).isArrayType());
	}

	@Override
	public int compareTo(final MatchingConfigurationElement o) {
		return this.address.compareTo(o.address);
	}
}