/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package woolfel.hashtest;

import java.util.Random;
import java.util.HashMap;

/**
 * @author Peter Lin
 *
 * A simple test to measure hashmap benchmark
 */
public class HashMapBenchmark {

	/**
	 * 
	 */
	public HashMapBenchmark() {
		super();
	}
    
    public HashMap createHashMap(int count) {
    	HashMap map = new HashMap();
        for (int idx=0; idx < count; idx++) {
            map.put(String.valueOf(count), count + "value");
        }
        return map;
    }

	public static void main(String[] args) {
        int[] series = {1000,10000,100000,1000000,10000000};
        HashMapBenchmark util = new HashMapBenchmark();
        Random ran = new Random();
        for (int idx=0; idx < series.length; idx++) {
        	HashMap map = util.createHashMap(series[idx]);
            long start = System.currentTimeMillis();
            for (int idz=0; idz < 10000000; idz++) {
                String key = String.valueOf(ran.nextInt(series[idx]));
                Object val = map.get(key);
            }
            long end = System.currentTimeMillis();
            long el = end - start;
            double perOp = (double)el/10000000;
            String fstring = String.valueOf(perOp * 1000000);
            System.out.println("Test for " + series[idx] + " items");
            System.out.println("elapsed time " + el + " millisecond");
            System.out.println("per lookup " + fstring + " nanosecond");
        }
	}
}
