package jpf2019.rational.abs;

public class Rational_4c {

	public int change(int oldVal, int newVal){return oldVal;}
	public float change(float oldVal, float newVal) {return oldVal;}
	public double change(double oldVal, double newVal){return oldVal;}
	public boolean change(boolean oldVal, boolean newVal){return oldVal;}
	public long change(long oldVal, long newVal){return oldVal;}
	public final boolean OLD = true;
	public final boolean NEW = false;
	public boolean execute(boolean executionMode){return executionMode;};

	private int num;
	private int den;

	public Rational_4c() {
		num = 0;
		den = 1;
	}

	public Rational_4c(int n, int d) {
		num = n;
		den = d;
	}

	public int abs(int x) {
		if (x >= 0) {
			return x;
		} else {
			return change(-x, +x);
		}
	}

	public static void main(String[] args) {
		(new Rational_4c()).abs(0);
	}

}
