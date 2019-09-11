package jpf2019.rational.gcd;

public class Rational_7c {

	public int change(int oldVal, int newVal){return oldVal;}
	public float change(float oldVal, float newVal) {return oldVal;}
	public double change(double oldVal, double newVal){return oldVal;}
	public boolean change(boolean oldVal, boolean newVal){return oldVal;}
	public long change(long oldVal, long newVal){return oldVal;}
	public final boolean OLD = true;
	public final boolean NEW = false;
	public boolean execute(boolean executionMode){return executionMode;};

	public Rational_7c() {
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
		if (c == 0) {
			return abs(b);
		}
		int count = 0;
		while (change(b != 0, b==b)) {
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

	public static void main(String[] args) {
		(new Rational_7c()).gcd(10, 2);
	}

}
