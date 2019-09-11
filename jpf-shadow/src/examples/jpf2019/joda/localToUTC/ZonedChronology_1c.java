package jpf2019.joda.localToUTC;

import gov.nasa.jpf.vm.Verify;

public class ZonedChronology_1c {

    public int change(int oldVal, int newVal) {
        return oldVal;
    }

    public float change(float oldVal, float newVal) {
        return oldVal;
    }

    public double change(double oldVal, double newVal) {
        return oldVal;
    }

    public boolean change(boolean oldVal, boolean newVal) {
        return oldVal;
    }

    public long change(long oldVal, long newVal) {
        return oldVal;
    }

    public final boolean OLD = true;
    public final boolean NEW = false;

    public boolean execute(boolean executionMode) {
        return executionMode;
    };

    public int localToUTC(int localInstant, int offsetFromLocal) {
        final int MAX_INT = 100;
        final int MIN_INT = -100;
        final int max_offset = 24;

        if (change(false, true)) { // jpf-shadow
            if (localInstant == MAX_INT) {
                return MAX_INT;
            } else if (localInstant == MIN_INT) {
                return MIN_INT;
            }
        }

        int offset = offsetFromLocal;
        Verify.ignoreIf(offset > max_offset || offset < -max_offset);
        int utcInstant = localInstant - offset;

         if (change(false, true)) { // jpf-shadow
            if (localInstant > 0 && utcInstant < 0 || utcInstant > MAX_INT) {
                return MAX_INT;
            }
            else if (localInstant < 0 && utcInstant > 0 || utcInstant < MIN_INT) {
                return MIN_INT;
            }
        }

        return utcInstant;

    }

    public static void main(String[] args) {
        ZonedChronology_1c z = new ZonedChronology_1c();
        int x = z.localToUTC(100, 20);
    }
}
