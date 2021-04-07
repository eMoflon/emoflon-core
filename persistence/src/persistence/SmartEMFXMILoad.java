package persistence;

import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;

/**
 * This is a version of {@link XMILoadImpl} that uses {@link SmartEMFXMIHandler}
 * instead of {@link org.eclipse.emf.ecore.xmi.impl.SAXXMIHandler SAXXMIHandler}.
 * This makes it compatible with EObjects that do not implement {@link org.eclipse.emf.ecore.InternalEObject InternalEObject}.
 * @author paulschiffner
 */
public class SmartEMFXMILoad extends XMILoadImpl {

	public SmartEMFXMILoad(XMLHelper helper) {
		super(helper);
	}
	
	@Override
	public SmartEMFXMIHandler makeDefaultHandler() {
		return new SmartEMFXMIHandler(resource, helper, options);
	}

}
