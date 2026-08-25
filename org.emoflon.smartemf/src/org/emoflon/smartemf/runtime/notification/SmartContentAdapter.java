package org.emoflon.smartemf.runtime.notification;

import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;

public class SmartContentAdapter extends EContentAdapter {

	@Override
	protected void unsetTarget(Resource target) {
		basicUnsetTarget(target);
		List<EObject> contents = target.getContents();
		for (EObject e : contents) {
			Notifier notifier = e;
			removeAdapter(notifier, true, false);
		}
	}

}
