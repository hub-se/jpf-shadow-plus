package gov.nasa.jpf.shadow;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class SymExParameter {

    public String classpath;
    public String sourcepath;
    public String packageName;
    public String className;
    public String methodName;
    public String methodNameWithSymbolicParameter;
    public int numberOfClassesInBenchmark;
    public String specialBenchmarks;
    public String resultsDirectory;
    public Map<String, String> constraintSolver;
    public boolean useDirectedSymEx;
    public String allMethods;

    public SymExParameter(String classpath, String sourcepath, String packageName, String className, String methodName,
            String methodNameWithSymbolicParameter, int numberOfClassesInBenchmark, String specialBenchmarks,
            String resultsDirectory, Map<String, String> constraintSolver, boolean useDirectedSymEx,
            String allMethods) {
        this.classpath = classpath;
        this.sourcepath = sourcepath;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.methodNameWithSymbolicParameter = methodNameWithSymbolicParameter;
        this.numberOfClassesInBenchmark = numberOfClassesInBenchmark;
        this.specialBenchmarks = specialBenchmarks;
        this.resultsDirectory = resultsDirectory;
        this.constraintSolver = constraintSolver;
        this.useDirectedSymEx = useDirectedSymEx;
        this.allMethods = allMethods;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("classpath=");
        sb.append(this.classpath);
        sb.append("\n");
        sb.append("sourcepath=");
        sb.append(this.sourcepath);
        sb.append("\n");
        sb.append("package=");
        sb.append(this.packageName);
        sb.append("\n");
        sb.append("class=");
        sb.append(this.className);
        sb.append("\n");
        sb.append("method=");
        sb.append(this.methodNameWithSymbolicParameter);
        sb.append("\n");

        return sb.toString();
    }

    // -----------------------

    public static final SymExParameter Foo = new SymExParameter("${jpf-shadow-plus}/build/jpf-shadow-plus.jar",
            "${jpf-shadow-plus}/src/examples", "jpf2019.foo", "Foo", "foo", "foo(sym)", 1, "",
            "evaluation-results/00_Foo_JPF/shadow-plus-results/", new HashMap<String, String>() {
                {
                    put("1", "coral");
                }
            }, false, "foo");

    public static final SymExParameter Joda_LocalToUTC = new SymExParameter("${jpf-shadow-plus}/build/jpf-shadow-plus.jar",
            "${jpf-shadow-plus}/src/examples", "jpf2019.joda.localToUTC", "ZonedChronology", "localToUTC", "localToUTC(sym#sym)", 1, "",
            "evaluation-results/03_Joda_LocalToUTC_JPF/shadow-plus-results/", null, false, "localToUTC");

    public static final SymExParameter Rational_abs = new SymExParameter("${jpf-shadow-plus}/build/jpf-shadow-plus.jar",
            "${jpf-shadow-plus}/src/examples", "jpf2019.rational.abs", "Rational", "main", "abs(sym)", 5, "",
            "evaluation-results/01_Rational/abs/shadow-plus-results/", null, false, "");

    public static final SymExParameter Rational_gcd = new SymExParameter("${jpf-shadow-plus}/build/jpf-shadow-plus.jar",
            "${jpf-shadow-plus}/src/examples", "jpf2019.rational.gcd", "Rational", "main", "gcd(sym#sym)", 22, "",
            "evaluation-results/01_Rational/gcd/shadow-plus-results/", new HashMap<String, String>() {
                {
                    put("15", "coral");
                    put("17", "coral");
                    put("19", "coral");
                    put("21", "coral");
                }
            }, false, "");

    public static final SymExParameter Rational_simplify = new SymExParameter("${jpf-shadow-plus}/build/jpf-shadow-plus.jar",
            "${jpf-shadow-plus}/src/examples", "jpf2019.rational.simplify", "Rational", "simplify", // "main",
            "gcd(sym#sym)", 27, "2_27,2_16,3_11,16_27,2_16_27",
            "evaluation-results/01_Rational/simplify/shadow-plus-results/", new HashMap<String, String>() {
                {
                    put("19", "coral");
                    put("21", "coral");
                    put("23", "coral");
                    put("25", "coral");
                }
            }, false, "");


    public static final SymExParameter WBS_update = new SymExParameter("${jpf-shadow-plus}/build/jpf-shadow-plus.jar",
            "${jpf-shadow-plus}/src/examples", "jpf2019.wbs.update", "WBS", "main", "update(sym#sym#sym)", 10, "",
            "evaluation-results/02_WBS/update/shadow-plus-results/", null, false, "");

    public static final SymExParameter WBS_launch = new SymExParameter("${jpf-shadow-plus}/build/jpf-shadow-plus.jar",
            "${jpf-shadow-plus}/src/examples", "jpf2019.wbs.launch", "WBS", "main", "launch(sym#sym#sym#sym#sym#sym#sym#sym#sym)", 10, "",
            "evaluation-results/02_WBS/launch/shadow-plus-results/", null, false, "");

}
