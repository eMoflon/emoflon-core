package persistence;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public class XtendXMIResourceFactoryImpl implements Resource.Factory {
	
	  /**
	   * Constructor for XtendXMIResourceFactoryImpl
	   */
	  public XtendXMIResourceFactoryImpl()
	  {
	    super();
	  }

	  @Override
	  public Resource createResource(URI uri)
	  {
	    return new XtendXMIResource(uri);
	  }
}


