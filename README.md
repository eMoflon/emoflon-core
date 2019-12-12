# eMoflon Core

This repository holds core components of the model-driven engineering tool eMoflon.

Capabilities of eMoflon Core:
* Basic EMF builder (An autobuilder that regenerates Java code whenever you modify an .ecore file)
* eMoflon perspective
* eMoflon console

## How to install

Eclipse update site available at https://emoflon.org/emoflon-core-updatesite/stable/updatesite/

## Supplementary information

https://emoflon.org/ hosts the eMoflon website with general information.

https://github.com/eMoflon/emoflon-docu provides documentation and the Wiki of eMoflon.

Visit https://github.com/eMoflon/emoflon-core/issues to report any issues.

Licensing information can be found in [LICENSE.md](LICENSE.md).

## How to set up an eMoflon Core developer workspace

* Install [*Eclipse Oxygen 2 with Modeling Tools*](http://www.eclipse.org/downloads/packages/eclipse-modeling-tools/oxygen2) (or higher)
* Install XText 2.12.0 (or higher)
  * See *Help -> Eclipse Marketplace... -> Find 'Eclipse XText'*
  * Note: The entry says '2.11.0,' but 2.12.0 will be installed.
* Clone Git repository from here: https://github.com/eMoflon/emoflon-core.git
* Import the PSF file located here: https://raw.githubusercontent.com/eMoflon/emoflon-core/master/projectSet.psf)
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

## How to work with Tycho
* Make sure that Maven is installed
    * See *Help -> Install new Software... -> Filter for 'm2e' or look into 'General Purpose Tools/m2e - Maven Integration for Eclipse'*
* Launch the launch configuration located here: /org.moflon.core.tycho.parent/.launch/emoflon-core.launch
