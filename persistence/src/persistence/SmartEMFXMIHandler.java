package persistence;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler;

public class SmartEMFXMIHandler extends SAXXMIHandler {

	public SmartEMFXMIHandler(XMLResource xmiResource, XMLHelper helper, Map<?, ?> options) {
		super(xmiResource, helper, options);
	}
	
	@Override
	protected void handleObjectAttribs(EObject obj) {
		if (attribs != null) {
			for (int i = 0, size = attribs.getLength(); i < size; ++i)
		      {
		        String name = attribs.getQName(i);
		        if (name.equals(ID_ATTRIB))
		        {
		          xmlResource.setID(obj, attribs.getValue(i));
		        }
		        else if (name.equals(hrefAttribute) && (!recordUnknownFeature || types.peek() != UNKNOWN_FEATURE_TYPE || obj.eClass() != anyType))
		        {
		          throw new UnsupportedOperationException("Can't handle proxies");
		        }
		        else if (isNamespaceAware)
		        {
		          String namespace = attribs.getURI(i);
		          if (!ExtendedMetaData.XSI_URI.equals(namespace) && !notFeatures.contains(name))
		          {
		            setAttribValue(obj, name, attribs.getValue(i));
		          }
		        }
		        else if (!name.startsWith(XMLResource.XML_NS) && !notFeatures.contains(name))
		        {
		          setAttribValue(obj, name, attribs.getValue(i));
		        }
		      }
		}
	}

}
