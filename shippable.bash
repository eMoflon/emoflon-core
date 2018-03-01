#!/bin/bash

# This script contains the CI build workflow for this repository
# Author: Roland Kluge
# Date: 2018-02-27

# Emulate X server via X Window Virtual Framebuffer
#apt-get update && apt-get install -y --no-install-recommends xvfb
#Xvfb :1 -screen 0 1360x1024x24 &
#export DISPLAY=:1

# Prepare environment
workspacePath=.
repositoryRoot=.
if [ ! -f $repositoryRoot/shippable.yml ];
then
  echo "Expect to find emoflore-core repository at $repositoryRoot"
  exit -1
fi

# First trigger the MWE2 workflow using Tycho (not necessary, left here for documentation purposes)
#mvn generate-sources -pl org.moflon.emf.injection,org.moflon.core.releng.target

echo "Create empty workspace"
$ECLIPSE_HOME/eclipse -nosplash -application org.eclipse.jdt.apt.core.aptBuild -data $workspacePath

echo "Import only those projects that require eMoflon EMF codegen."
importSpecification=""
for project in org.moflon.core.utilities org.moflon.core.propertycontainer;
do
  projectFolder="$repositoryRoot/$project"
  if [ ! -f "$projectFolder/.project" ];
  then
    echo "Expected to find project folder '$projectFolder'"
    exit -1
  fi
  importSpecification="$importSpecification -import $projectFolder"
done
importSpecification="-import $repositoryRoot"

echo "  Import specification: '$importSpecification'"
$ECLIPSE_HOME/eclipse -nosplash -application com.seeq.eclipse.importprojects.headlessimport -data $workspacePath $importSpecification || exit -1

echo "Run eMoflon codegen"
$ECLIPSE_HOME/eclipse -nosplash -application org.eclipse.jdt.apt.core.aptBuild -data $workspacePath || exit -1

echo "Run Tycho (clean compile)"
mvn clean compile || exit -1

echo "Run Tycho (integration-test)"
mvn integration-test || exit -1

echo "Publish JUnit test results"
find . -path "*/target/*/TEST*.xml" -exec cp {} /root/src/github.com/eMoflon/emoflon-core/shippable/testresults/ \;
