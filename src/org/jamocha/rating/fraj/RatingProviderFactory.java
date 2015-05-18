package org.jamocha.rating.fraj;

import java.util.function.DoubleBinaryOperator;

public class RatingProviderFactory implements org.jamocha.rating.RatingProviderFactory {
	@Override
	public RatingProvider newRatingProvider(final DoubleBinaryOperator cpuAndMemCostCombiner) {
		return new RatingProvider(cpuAndMemCostCombiner);
	}
}
