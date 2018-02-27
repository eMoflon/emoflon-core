#!/bin/bash

# This script contains the CI build workflow for this repository
# Author: Roland Kluge
# Date: 2018-02-27

# Emulate X server via X Window Virtual Framebuffer
apt-get update && apt-get install -y --no-install-recommends xvfb
Xvfb :1 -screen 0 1360x1024x24 &
export DISPLAY=:1

# Prepare environment
workspacePath=.
repositoryRoot=.
if [ ! -f $repositoryRoot/shippable.yml ];
then
  echo "Expect to find emoflore-core repository at $repositoryRoot"
  exit -1
fi

# First trigger the MWE2 workflow using Tycho.
mvn generate-sources -pl org.moflon.emf.injection,org.moflon.core.releng.target

# Run eMoflon codegen
$ECLIPSE_HOME/eclipse -nosplash -application org.eclipse.jdt.apt.core.aptBuild -data $workspacePath || exit -1

$ECLIPSE_HOME/eclipse -nosplash -application com.seeq.eclipse.importprojects.headlessimport -data $workspacePath -import $repositoryRoot || exit -1

$ECLIPSE_HOME/eclipse -nosplash -application org.eclipse.jdt.apt.core.aptBuild -data $workspacePath || exit -1

# Run Tycho
mvn clean compile || exit -1

mvn integration-test || exit -1

# Publish JUnit test results
find . -path "*/target/*/TEST*.xml" -exec cp {} /root/src/github.com/eMoflon/emoflon-core/shippable/testresults/ \;
