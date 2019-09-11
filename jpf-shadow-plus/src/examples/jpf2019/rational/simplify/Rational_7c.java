/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jpf2019.rational.simplify;

public class Rational_7c {

	public int change(int oldVal, int newVal){return oldVal;}
	public float change(float oldVal, float newVal) {return oldVal;}
	public double change(double oldVal, double newVal){return oldVal;}
	public boolean change(boolean oldVal, boolean newVal){return oldVal;}
	public long change(long oldVal, long newVal){return oldVal;}

	private int num;
	private int den;

	public Rational_7c(int n, int d) {
		num = n;
		den = d;
	}

	public int abs(int x) {
		if (x >= 0) {
			return x;
		} else {
			return -x;
		}
	}

	public int gcd(int a, int b) {
		int c = abs(a);
		if (change(c == 0, false)) {
			return abs(b);
		}
		int count = 0;
		while (b != 0) {
			count++;
			if (count >= 4) {
				assert false;
			}
			if (c > b) {
				c = c - b;
			} else {
				b = b - c;
			}
		}
		return c;
	}

	public void simplify() {
		int gcd = gcd(num, den);
		num = num / gcd;
		den = den / gcd;
	}

	public static void main(String[] args) {
		(new Rational_7c(10, 2)).simplify();
	}

}
