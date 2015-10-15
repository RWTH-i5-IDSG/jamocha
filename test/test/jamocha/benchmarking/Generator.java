/*
 * Copyright 2002-2015 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.benchmarking;

import static java.util.stream.Collectors.joining;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Generator {
	public static void main(final String[] args) throws IOException {
		final Random random = new Random();
		final int rows = 500;
		final int lo = 0;
		final int hi = 101;
		try (FileWriter writer = new FileWriter("abc.fct")) {
			writer.write("(A (a ");
			writer.write(random.ints(lo, hi).limit(rows).mapToObj(Integer::toString).collect(joining("))\n(A (a ")));
			writer.write("))\n");
			writer.write("(C (c ");
			writer.write(random.ints(lo, hi).limit(rows).mapToObj(Integer::toString).collect(joining("))\n(C (c ")));
			writer.write("))\n");
			{
				final Iterator<String> as = random.ints(lo, hi).mapToObj(i -> "(a " + i + ")").iterator();
				final Iterator<String> cs = random.ints(lo, hi).mapToObj(i -> "(c " + i + ")").iterator();
				for (int i = 0; i < rows; ++i) {
					writer.write("(B ");
					writer.write(as.next());
					writer.write(cs.next());
					writer.write(")\n");
				}
			}
		}
	}
}
