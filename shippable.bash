#!/bin/bash

# This script contains the CI build workflow for this repository
# Author: Roland Kluge
# Date: 2018-02-27
#
# NOTE: Most of the CI script was devoted to invoking the eMoflon code generation on the repository.
#       However, this appears to work only in environments with a UI, currently, which we cannot properly emulate with Xvfb
#

# Emulate X server via X Window Virtual Framebuffer
#apt-get update && apt-get install --yes --no-install-recommends xvfb
#export DISPLAY=:99.0

# Prepare environment
#workspacePath=$(pwd)/../eclipseWorkspace
#repositoryRoot=$(pwd)
#if [ ! -f $repositoryRoot/shippable.yml ];
#then
#  echo "Expect to find emoflore-core repository at $repositoryRoot"
#  exit -1
#fi

# First trigger the MWE2 workflow using Tycho (not necessary, left here for documentation purposes)
#mvn generate-sources -pl org.moflon.emf.injection,org.moflon.core.releng.target

#echo "Import all projects."
#xvfb-run --server-args="-ac" $ECLIPSE_HOME/eclipse -nosplash -application com.seeq.eclipse.importprojects.headlessimport -data $workspacePath -import $repositoryRoot || exit -1

#echo "Run eMoflon codegen"
#xvfb-run --server-args="-ac" $ECLIPSE_HOME/eclipse -nosplash -application org.eclipse.jdt.apt.core.aptBuild -data $workspacePath || exit -1

echo "Run Tycho (clean compile)"
mvn -q clean compile || exit -1

echo "Run Tycho (integration-test)"
mvn -q integration-test || exit -1

echo "Publish JUnit test results"
find . -path "*/target/*/TEST*.xml" -exec cp {} /root/src/github.com/eMoflon/emoflon-core/shippable/testresults/ \;
