package org.moflon.emf.ui.wizard

/**
 * This class provides the default content of generated Ecore files
 */
class DefaultContentGenerator {
    
    /**
     * Generates an XMI representation of the EPackage corresponding to the given
     * project name
     *
     * @param projectName the name of the containing project
     * @param packageName the name of the EPackage
     * @param packageUri the NS URI of the EPackage 
     * @return the raw XMI file content
     */
    static def String generateDefaultEPackageForProject(String projectName, String packageName, String packageUri) {
        '''
        <?xml version="1.0" encoding="ASCII"?>
        <ecore:EPackage xmi:version="2.0"
          xmlns:xmi="http://www.omg.org/XMI"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore"
          name="«packageName»"
          nsURI="«packageUri»"
          nsPrefix="«projectName»">
          <eAnnotations source="http://www.eclipse.org/emf/2002/GenModel">
            <details key="documentation" value="TODO: Add documentation for «packageName». Hint: You may copy this element in the Ecore editor to add documentation to EClasses, EOperations, ..."/>
          </eAnnotations>
        </ecore:EPackage>
        '''
    }
    
     /**
     * Generates a default Emfatic file representation of the EPackage corresponding to the given
     * project name
     *
     * @param projectName the name of the containing project
     * @param packageName the name of the EPackage
     * @param packageUri the NS URI of the EPackage 
     * @return the default Emfatic file's content as a string
     */
    static def String generateDefaultEmfaticFileForProject(String projectName, String packageName, String packageUri) {
    	'''
    	@GenModel(documentation="TODO: Add documentation for Blup")
    	@namespace(uri="«packageUri»", prefix="«projectName»")
    	package «packageName»;
    	'''
    }
}