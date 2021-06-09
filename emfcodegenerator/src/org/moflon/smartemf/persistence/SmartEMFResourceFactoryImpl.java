package persistence;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public class SmartEMFResourceFactoryImpl implements Resource.Factory {
	
	  /**
	   * Constructor for XtendXMIResourceFactoryImpl
	   */
	  public SmartEMFResourceFactoryImpl()
	  {
	    super();
	  }

	  @Override
	  public Resource createResource(URI uri)
	  {
	    return new SmartEMFResource(uri);
	  }
}


