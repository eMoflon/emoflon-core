# eMoflon Core

[![Run Status](https://api.shippable.com/projects/5975e2f66b05110700b064c3/badge?branch=master)](https://app.shippable.com/github/eMoflon/emoflon-core)
[![Coverage Badge](https://api.shippable.com/projects/5975e2f66b05110700b064c3/coverageBadge?branch=master)](https://app.shippable.com/github/eMoflon/emoflon-core)
[![codebeat badge](https://codebeat.co/badges/44f66b11-d661-4b6a-8d29-5a056976bba2)](https://codebeat.co/projects/github-com-emoflon-emoflon-core-master)
[![Project Stats](https://www.openhub.net/p/emoflon-core/widgets/project_thin_badge.gif)](https://www.openhub.net/p/emoflon-core)

This repository holds core components of the model-driven engineering tool eMoflon.

Install from http://emoflon.org/emoflon-core/org.moflon.core.releng.updatesite/

Capabilities of eMoflon Core:
* Basic EMF builder (An autobuilder that regenerates Java code whenever you modify an .ecore file)
* eMoflon perspective
* eMoflon console

https://emoflon.github.io/ hosts the eMoflon website with general information.

https://github.com/eMoflon/emoflon-docu provides documentation and the Wiki of eMoflon.

Visit https://github.com/eMoflon/emoflon-core/issues to report any issues.

Licensing information can be found in [LICENSE.md](LICENSE.md).

## How to set up an eMoflon Core developer workspace

* Install [*Eclipse Oxygen 2 with Modeling Tools*](http://www.eclipse.org/downloads/packages/eclipse-modeling-tools/oxygen2) (or higher)
* Install XText 2.12.0 (or higher)
  * See *Help -> Eclipse Marketplace... -> Find 'Eclipse XText'*
  * Note: The entry says '2.11.0,' but 2.12.0 will be installed.
* (Optional) Install the Maven integration *m2e* for Eclipse
  * See *Help -> Install new Software... -> Filter for 'm2e' or look into 'General Purpose Tools/m2e - Maven Integration for Eclipse'*
* Clone Git repository from here: https://github.com/eMoflon/emoflon-core.git
* Import all projects from the working copy (or just import the PSF file:  https://raw.githubusercontent.com/eMoflon/emoflon-core/master/projectSet.psf
* Run all MWE2 workflows to generate XText-specific code
  * See *"Open Resource" dialog (Ctrl+Shift+R) -> Filter for files ending with .mwe2*
* Make sure that autobuild is active so that the remaining code is generated and the Java compiler gets triggered.
  * See *Project -> Build Automatically*
* To test your setup,
  1. open a runtime Eclipse workspace,
  2. create a new eMoflon EMF project (*File -> New -> Other... -> Filter for 'eMoflon' -> New eMoflon EMF Project Wizard*),
  3. add a dummy class to the .ecore file in the */models* folder of the freshly created project, and
  4. observe how eMoflon generates the corresponding EMF-compliant Java code into the */gen* folder.

## How to provide the eMoflon Core update site
* Follow the instructions in *org.moflon.core.releng.updatesite/README.md* for building and releasing the update site
