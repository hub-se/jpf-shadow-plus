package jpf2019.foo;

public class Foo_1c {

	public int change(int oldVal, int newVal){return oldVal;}
	public float change(float oldVal, float newVal) {return oldVal;}
	public double change(double oldVal, double newVal){return oldVal;}
	public boolean change(boolean oldVal, boolean newVal){return oldVal;}
	public long change(long oldVal, long newVal){return oldVal;}

	public int foo(int x) {
		int y;

		if (x<0) {
			y = change(-x, x*x);
		} else {
			y = 2 * x;
		}

		y = change(y, y+1);

		if (y>1) {
			return 0;
		} else {
			if (y==1) {
				assert(false);
			}
		}
		return 1;
	}

	public static void main(String[] args) {
		(new Foo_1c()).foo(0);
	}

}
