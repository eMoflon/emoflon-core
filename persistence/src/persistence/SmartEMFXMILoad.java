package persistence;

import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;

public class SmartEMFXMILoad extends XMILoadImpl {

	public SmartEMFXMILoad(XMLHelper helper) {
		super(helper);
	}
	
	@Override
	public SmartEMFXMIHandler makeDefaultHandler() {
		return new SmartEMFXMIHandler(resource, helper, options);
	}

}
