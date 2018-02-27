#!/bin/bash

# This script contains the CI build workflow for this repository
# Author: Roland Kluge
# Date: 2018-02-27

# Prepare environment
workspacePath=.
repositoryRoot=.
if [ ! -f $repositoryRoot/shippable.yml ];
then
  echo "Expect to find emoflore-core repository at $repositoryRoot"
  exit -1
fi

# Run eMoflon codegen
$ECLIPSE_HOME/eclipse -nosplash -application org.eclipse.jdt.apt.core.aptBuild -data $workspacePath
$ECLIPSE_HOME/eclipse -nosplash -application com.seeq.eclipse.importprojects.headlessimport -data $workspacePath -import $repositoryRoot
$ECLIPSE_HOME/eclipse -nosplash -application org.eclipse.jdt.apt.core.aptBuild -data $workspacePath

# Run Tycho
mvn clean compile
mvn integration-test

# Publish JUnit test results
find . -path "*/target/*/TEST*.xml" -exec cp {} /root/src/github.com/eMoflon/emoflon-core/shippable/testresults/ \;
