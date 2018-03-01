#!/bin/bash

# This script contains the CI build workflow for this repository
# Author: Roland Kluge
# Date: 2018-02-27

# Emulate X server via X Window Virtual Framebuffer
Xvfb :99 &
export DISPLAY=:99.0

# Prepare environment
workspacePath=$(pwd)/../eclipseWorkspace
repositoryRoot=$(pwd)
if [ ! -f $repositoryRoot/shippable.yml ];
then
  echo "Expect to find emoflore-core repository at $repositoryRoot"
  exit -1
fi

# First trigger the MWE2 workflow using Tycho (not necessary, left here for documentation purposes)
mvn generate-sources -pl org.moflon.emf.injection,org.moflon.core.releng.target

echo "Import all projects."
xvfb-run $ECLIPSE_HOME/eclipse -nosplash -application com.seeq.eclipse.importprojects.headlessimport -data $workspacePath -import $repositoryRoot || exit -1

echo "Run eMoflon codegen"
xvfb-run $ECLIPSE_HOME/eclipse -nosplash -application org.eclipse.jdt.apt.core.aptBuild -data $workspacePath || exit -1

echo "Run Tycho (clean compile)"
mvn clean compile || exit -1

echo "Run Tycho (integration-test)"
mvn integration-test || exit -1

echo "Publish JUnit test results"
find . -path "*/target/*/TEST*.xml" -exec cp {} /root/src/github.com/eMoflon/emoflon-core/shippable/testresults/ \;
