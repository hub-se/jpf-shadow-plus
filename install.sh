#!/bin/bash
trap "exit" INT

echo "Installing JPFShadow+ ..."
workingDir=$(pwd)

# Write site.properites file.
sitePropFile="$HOME/.jpf/site.properties"
echo "Prepare properties file: $sitePropFile ..."
if [ -e $sitePropFile ]
then
  rm $sitePropFile
fi
touch $sitePropFile
echo "jpf-core = $workingDir/jpf-core" >> $sitePropFile
echo "jpf-symbc = $workingDir/jpf-symbc" >> $sitePropFile
echo "jpf-shadow = $workingDir/jpf-shadow" >> $sitePropFile
echo "jpf-shadow-plus = $workingDir/jpf-shadow-plus" >> $sitePropFile
echo "extensions=\${jpf-core},\${jpf-symbc},\${jpf-shadow},\${jpf-shadow-plus}" >> $sitePropFile

# Build jpf-core
echo "Build jpf-core ..."
cd "jpf-core"
rm -f build.log
ant clean >> build.log
ant >> build.log
cd ..

# Build jpf-symbc
echo "Build jpf-symbc ..."
cd "jpf-symbc"
rm -f build.log
ant clean >> build.log
ant >> build.log
cd ..

# Build jpf-shadow
echo "Build jpf-shadow ..."
cd "jpf-shadow"
rm -f build.log
ant clean >> build.log
ant >> build.log
cd ..

# Build SootConnection
echo "Build SootConnection ..."
cd "SootConnection"
rm -f build.log
ant clean >> build.log
ant >> build.log
cd ..

# Build jpf-shadow-plus
echo "Build jpf-shadow-plus ..."
cd "jpf-shadow-plus"
rm -f build.log
ant clean >> build.log
ant >> build.log
cd ..

echo "Done."
