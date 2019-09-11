package gov.nasa.jpf.shadow;

import java.util.HashMap;

@SuppressWarnings("serial")
public class SymExParameter_JPF {

    public static final SymExParameter Foo_JPF = new SymExParameter("${jpf-shadow}/build/jpf-shadow.jar",
            "${jpf-shadow}/src/examples", "jpf2019.foo", "Foo", "foo", "foo(sym)", 1, "",
            "evaluation-results/00_Foo_JPF/shadow-results/", null);

    public static final SymExParameter Joda_LocalToUTC = new SymExParameter("${jpf-shadow}/build/jpf-shadow.jar",
            "${jpf-shadow}/src/examples", "jpf2019.joda.localToUTC", "ZonedChronology", "localToUTC", "localToUTC(sym#sym)", 1, "",
            "evaluation-results/03_Joda_LocalToUTC_JPF/shadow-results/", null);

    public static final SymExParameter Rational_abs = new SymExParameter("${jpf-shadow}/build/jpf-shadow.jar",
            "${jpf-shadow}/src/examples", "jpf2019.rational.abs", "Rational", "main", "abs(sym)", 5, "",
            "evaluation-results/01_Rational/abs/shadow-results/", null);

    public static final SymExParameter Rational_gcd = new SymExParameter("${jpf-shadow}/build/jpf-shadow.jar",
            "${jpf-shadow}/src/examples", "jpf2019.rational.gcd", "Rational", "main", "gcd(sym#sym)", 22, "",
            "evaluation-results/01_Rational/gcd/shadow-results/", new HashMap<String, String>() {
                {
                    put("15", "coral");
                    put("17", "coral");
                    put("19", "coral");
                    put("21", "coral");
                }
            });

    public static final SymExParameter Rational_simplify = new SymExParameter("${jpf-shadow}/build/jpf-shadow.jar",
            "${jpf-shadow}/src/examples", "jpf2019.rational.simplify", "Rational", "simplify", // "main",
            "gcd(sym#sym)", 27, "2_27,2_16,3_11,16_27,2_16_27",
            "evaluation-results/01_Rational/simplify/shadow-results/",new HashMap<String, String>() {
        {
            put("19", "coral");
            put("21", "coral");
            put("23", "coral");
            put("25", "coral");
        }
    });

    public static final SymExParameter WBS_update = new SymExParameter("${jpf-shadow}/build/jpf-shadow.jar",
            "${jpf-shadow}/src/examples", "jpf2019.wbs.update", "WBS", "main", "update(sym#sym#sym)", 10, "",
            "evaluation-results/02_WBS/update/shadow-results/", null);

    public static final SymExParameter WBS_launch = new SymExParameter("${jpf-shadow}/build/jpf-shadow.jar",
            "${jpf-shadow}/src/examples", "jpf2019.wbs.launch", "WBS", "main", "launch(sym#sym#sym#sym#sym#sym#sym#sym#sym)", 10, "",
            "evaluation-results/02_WBS/launch/shadow-results/", null);

}
