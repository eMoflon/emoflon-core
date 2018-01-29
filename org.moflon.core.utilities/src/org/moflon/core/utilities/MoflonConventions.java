package org.moflon.core.utilities;

/**
 * This class captures all conventions used by the EMF build process of eMoflon
 *
 * Conventions include
 * * The Bundle-SymbolicName and the project name of projects created with eMoflon are identical
 * * The generated Ecore file is named after the last segment of the project name
 *   * For instance, in project P/x.y.z.mymodel, the Ecore file is located here: P/x.y.z.mymodel/model/Mymodel.ecore
 * * The default NS URIs use the platform:/resource schema.
 *   * For instance, the metamodel of project P/x.y.z.mymodel should have
 *     (i)   root package 'mymodel',
 *     (ii)  NS prefix 'x.y.z.mymodel' and
 *     (iii) NS URI 'platform:/resource/x.y.z.mymodel/model/Mymodel.ecore'
 *
 * @author Roland Kluge - Initial implementation
 */
public class MoflonConventions
{

}
