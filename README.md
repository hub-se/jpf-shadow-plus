[![DOI](https://zenodo.org/badge/207503090.svg)](https://zenodo.org/badge/latestdoi/207503090)
# Complete Shadow Symbolic Execution with Java PathFinder

This repository provides the tool ShadowJPF+ and the evaluation subjects for the paper *Complete Shadow Symbolic Execution with Java PathFinder* accepted for the [Java PathFinder workshop 2019](https://2019.ase-conferences.org/home/jpf-2019), co-located with [ASE 2019](https://2019.ase-conferences.org). The paper will be published in the ACM SIGSOFT Software Engineering Notes, and a pre-print is available [here](https://yannicnoller.github.io/publications/jpf2019_noller_jpfshadow+.pdf).

Authors: [Yannic Noller](https://yannicnoller.github.io), [Hoang Lam Nguyen](https://github.com/hoanglam-nguyen), [Minxing Tang](https://www.informatik.hu-berlin.de/de/institut/mitarbeiter/1691704), [Timo Kehrer](https://www.informatik.hu-berlin.de/de/forschung/gebiete/mse/mitarb/kehrerti.html) and [Lars Grunske](https://www.informatik.hu-berlin.de/de/Members/lars-grunske).

The repository includes:
* the experiment subjects: [jpf-shadow-plus/src/examples/jpf2019](./jpf-shadow-plus/src/examples/jpf2019),
* the raw output files can be found in the archive: [evaluation-results-archive.zip](evaluation-results-archive.zip),
* the source code for ShadowJPF+: [jpf-core](jpf-core), [jpf-symbc](jpf-symbc), and [jpf-shadow-plus](jpf-shadow-plus),
* the source code for ShadowJPF to rerun our evaluation: [jpf-shadow](jpf-shadow),
* and an [install](install.sh) script.

## Tool
JPFShadow+ is built on top of the [Symbolic PathFinder](https://github.com/SymbolicPathFinder/jpf-symbc) and its shadow symbolic execution extension [JPFShadow](https://github.com/hub-se/jpf-shadow).

### Requirements
* Git, Ant, Build-Essentials
* Java JDK = 1.8
* recommended: Ubuntu 18.04.1 LTS

### How to install and run
Be aware that the instructions have been tested for Unix systems only.

1. First you need to build all projects. Therefore, you can execute the [install.sh](install.sh) script from this folder (Caution: the script may override an existing site.properties file).
Otherwise follow the instructions: Please create the file "~/.jpf/site.properties" similar to the provided example file. This is necessary to tell JPF the paths of all extensions. The build will not work without! Afterwards, please use the provided ant scripts int the source folders and follow the order: jpf-core, jpf-symbc, and then either jpf-shadow or jpf-shadow-plus.

2. We use as constraint solver Z3, which need to be configured in the java library path at the command call. You see that we use the DYLD_LIBRARY_PATH variable, which is the right one for macOS. Please adapt this for your platform, e.g. for Windows one need to adapt the classpath syntax and the library path should be named "LD_LIBRARY_PATH". Please be sure that your system is able the definition of the java library path. E.g., OS X El Capitan needs to disable the SIP.

3. To run the jpf-shadow-plus experiments \[Note: this commands runs longer (~30min)\]:
```
DYLD_LIBRARY_PATH=jpf-symbc/lib java -cp "jpf-shadow-plus/build/*:jpf-core/build/*:jpf-symbc/build/*:jpf-symbc/lib/*:SootConnection/build/*:SootConnection/lib/*" gov.nasa.jpf.shadow.RunnerShadowPlus
```

4. To run the jpf-shadow experiments \[Note: this commands runs longer (~3min)\]:
```
DYLD_LIBRARY_PATH=jpf-symbc/lib java -cp "jpf-shadow/build/*:jpf-core/build/*:jpf-symbc/build/*:jpf-symbc/lib/*" gov.nasa.jpf.shadow.RunnerShadow_JPF
```

Step (3) and (4) store all results in the folder ./evaluations-results/. In order to review the files, please scroll down to the end of each file and check the generated path conditions. You may also for "statistics" to get the internal statistics of SPF.

If you want to modify the benchmark execution of jpf-shadow-plus: the class [RunnerShadowPlus](jpf-shadow-plus/src/main/gov/nasa/jpf/shadow/RunnerShadowPlus.java) defines in its main method which subjects will be executed, and the class [SymExParameter](jpf-shadow-plus/src/main/gov/nasa/jpf/shadow/SymExParameter.java) defines the properties of the subjects. You can similarly adapt the benchmark execution of jpf-shadow.
Note: You have to re-compile after the modifications.


## Maintainers

* **Yannic Noller** (yannic.noller at acm.org)


## License
This project is licensed under the Apache-2.0 License - see the [LICENSE](LICENSE) file for details
