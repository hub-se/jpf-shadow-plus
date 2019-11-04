package sootconnection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class SootConnector {

	private static Map<String, SootConnector> cache = new HashMap<>();

	public static SootConnector getInstance(String packageName, String className, String classPathExtension,
			List<String> excludePackagesList) {
		return getInstance(packageName, className, classPathExtension, false, excludePackagesList);
	}

	public static SootConnector getInstance(String packageName, String className, String classPathExtension,
			boolean forceReload, List<String> excludePackagesList) {
		classPathExtension = classPathExtension.replace(',', ':');
		String qualifiedName = packageName + "." + className;
		if (!cache.containsKey(qualifiedName) || forceReload) {
			SootConnector instance = new SootConnector(packageName, className, classPathExtension, excludePackagesList);
			cache.put(qualifiedName, instance);
		}
		return cache.get(qualifiedName);
	}

	private String packageName;
	private String className;

	private SootConnector(String packageName, String className, String classPathExtension,
			List<String> excludePackagesList) {
		this.packageName = packageName;
		this.className = className;
		init(classPathExtension, excludePackagesList);
	}

	@SuppressWarnings("static-access")
	private void init(String classPathExtension, List<String> excludePackagesList) {
		soot.G.v().reset(); // TODO really necessary? think about performance
		Options.v().set_soot_classpath(classPathExtension);

		/* Exclude some packages/classes */
		Options.v().set_exclude(excludePackagesList);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_no_bodies_for_excluded(true);

		Options.v().set_src_prec(Options.src_prec_only_class);
		Options.v().set_keep_line_number(true);
		Options.v().set_whole_program(true); // important for interprocedural analysis
		// Options.v().set_verbose(true);
		SootClass c = Scene.v().loadClassAndSupport(packageName + "." + className);
		Scene.v().loadNecessaryClasses();
		c.setApplicationClass();
		Scene.v().setMainClass(c);
		PackManager.v().getPack("cg").apply(); // builds whole CallGraph
	}

	public UnitGraph getCFGForMethodName(String methodName) {
		SootClass c = Scene.v().loadClassAndSupport(packageName + "." + className);
		Scene.v().loadNecessaryClasses();
		c.setApplicationClass();
		SootMethod targetMethod = c.getMethodByName(methodName);
		UnitGraph unitGraph = new ExceptionalUnitGraph(targetMethod.retrieveActiveBody());
		return unitGraph;
	}

	public Iterator<Edge> getIteratorOnCallersForMethodName(String methodName) {
		CallGraph callGraph = Scene.v().getCallGraph();
		SootClass c = Scene.v().loadClassAndSupport(packageName + "." + className);
		Iterator<Edge> i = callGraph.edgesInto(c.getMethodByName(methodName));
		return i;
	}

	public Iterator<Edge> getIteratorOnCallsOutOfMethodName(String methodName) {
		CallGraph callGraph = Scene.v().getCallGraph();
		SootClass c = Scene.v().loadClassAndSupport(packageName + "." + className);
		Iterator<Edge> i = callGraph.edgesOutOf(c.getMethodByName(methodName));
		return i;
	}

}
