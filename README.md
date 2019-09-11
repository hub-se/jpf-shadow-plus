This readme file describes how to rerun the experiments.

General notes:
Be aware that the instructions have been tested for Unix systems only.

The experiment subjects can be found in: "/jpf-shadow-plus/src/examples/jpf2019"
The raw output files can be found in the archive: "evaluation-results-archive.zip"

In order to rerun the subjects please execute the following steps:

(1)
First you need to build all projects. Therefore, you can execute the install.sh script from this folder (Caution: the script may override an existing site.properties file).
Otherwise follow the instructions: Please create the file "~/.jpf/site.properties" similar to the provided example file. This is necessary to tell JPF the paths of all extensions. The build will not work without! Afterwards, please use the provided ant scripts int the source folders and follow the order: jpf-core, jpf-symbc, and then either jpf-shadow or jpf-shadow-plus.

(2)
We use as constraint solver Z3, which need to be configured in the java library path at the command call. You see that we use the DYLD_LIBRARY_PATH variable, which is the right one for macOS. Please adapt this for your platform, e.g. for Windows one need to adapt the classpath syntax and the library path should be named "LD_LIBRARY_PATH". Please be sure that your system is able the definition of the java library path. E.g., OS X El Capitan needs to disable the SIP.

(3)
To run the jpf-shadow-plus experiments [Note: this commands runs longer (~30min)]:
$ DYLD_LIBRARY_PATH=jpf-symbc/lib java -cp "jpf-shadow-plus/build/*:jpf-core/build/*:jpf-symbc/build/*:jpf-symbc/lib/*" gov.nasa.jpf.shadow.RunnerShadowPlus
 
(4)
To run the jpf-shadow experiments [Note: this commands runs longer (~3min)]:
$ cd ./submission-supplementary-material/
$ DYLD_LIBRARY_PATH=jpf-symbc/lib java -cp "jpf-shadow/build/*:jpf-core/build/*:jpf-symbc/build/*:jpf-symbc/lib/*" gov.nasa.jpf.shadow.RunnerShadow_JPF

Step (3) and (4) store all results in the folder ./evaluations-results/. In order to review the files, please scroll down to the end of each file and check the generated path conditions. You may also for "statistics" to get the internal statistics of SPF.


