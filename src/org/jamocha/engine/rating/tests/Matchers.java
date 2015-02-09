package org.jamocha.engine.rating.tests;

import static org.hamcrest.CoreMatchers.not;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

public class Matchers {

	static final double jitter = 0.00000001;

	static class JitteredLessThan extends TypeSafeMatcher<Double> {

		final Double right;

		public JitteredLessThan(final Double right) {
			this.right = right;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("Jittered Less Than " + right);
		}

		@Override
		public boolean matchesSafely(final Double left) {
			return (left * (1 + jitter) < right);
		}

	}

	@Factory
	public static Matcher<Double> jitteredLessThan(final Double right) {
		return new JitteredLessThan(right);
	}

	static class JitteredGreaterThan extends TypeSafeMatcher<Double> {

		final Double right;

		public JitteredGreaterThan(final Double right) {
			this.right = right;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("Jittered Greater Than " + right);
		}

		@Override
		public boolean matchesSafely(final Double left) {
			return (left > right * (1 + jitter));
		}

	}

	@Factory
	public static Matcher<Double> jitteredGreaterThan(final Double right) {
		return new JitteredGreaterThan(right);
	}

	static class CloseTo extends TypeSafeMatcher<Double> {

		final Double right;

		public CloseTo(final Double right) {
			this.right = right;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("Close To " + right);
		}

		@Override
		public boolean matchesSafely(final Double left) {
			return not(jitteredLessThan(right)).matches(left)
					&& not(jitteredGreaterThan(right)).matches(left);
		}
	}

	@Factory
	public static Matcher<Double> closeTo(final Double right) {
		return new CloseTo(right);
	}

	static class LessThan extends TypeSafeMatcher<Double> {

		final Double right;

		public LessThan(final Double right) {
			this.right = right;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("Less Than " + right);
		}

		@Override
		public boolean matchesSafely(final Double left) {
			return (left < right);
		}

	}

	@Factory
	public static Matcher<Double> lessThan(final Double right) {
		return new LessThan(right);
	}

	static class GreaterThan extends TypeSafeMatcher<Double> {

		final Double right;

		public GreaterThan(final Double right) {
			this.right = right;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("Greater Than " + right);
		}

		@Override
		public boolean matchesSafely(final Double left) {
			return (left > right);
		}

	}

	@Factory
	public static Matcher<Double> greaterThan(final Double right) {
		return new GreaterThan(right);
	}

	static class LessOrEqual extends TypeSafeMatcher<Double> {

		final double right;

		public LessOrEqual(final Double right) {
			this.right = right;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("Less Or Equal " + right);
		}

		@Override
		public boolean matchesSafely(final Double left) {
			return (left <= right);
		}

	}

	@Factory
	public static Matcher<Double> lessOrEqual(final Double right) {
		return new LessOrEqual(right);
	}

	static class GreaterOrEqual extends TypeSafeMatcher<Double> {

		final Double right;

		public GreaterOrEqual(final Double right) {
			this.right = right;
		}

		@Override
		public void describeTo(final Description description) {
			description.appendText("Greater Or Equal " + right);
		}

		@Override
		public boolean matchesSafely(final Double left) {
			return (left >= right);
		}

	}

	@Factory
	public static Matcher<Double> greaterOrEqual(final Double right) {
		return new GreaterOrEqual(right);
	}

}
