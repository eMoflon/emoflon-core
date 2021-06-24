package org.moflon.smartemf.runtime.util;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.moflon.smartemf.runtime.SmartObject;

public class SmartEMFUtil {

	public static void deleteNode(EObject node, boolean recursive) {
		Queue<EObject> queue = new LinkedBlockingDeque<>();
		queue.add(node);
		deleteNodes(queue, recursive);
	}
	
	public static void deleteNodes(Collection<EObject> objs, boolean recursive) {
		Queue<EObject> queue = new LinkedBlockingDeque<>();
		queue.addAll(objs);
		deleteNodes(queue, recursive);
	}

	private static void deleteNodes(Queue<EObject> queue, boolean recursive) {
		while(!queue.isEmpty()) {
			EObject obj = queue.poll();
			
			for(EReference ref : obj.eClass().getEAllReferences()) {
				Object value = obj.eGet(ref);
				obj.eUnset(ref);
				if(recursive && ref.isContainment()) {
					if(value == null)
						continue;
					
					if(value instanceof EObject) {
						queue.add((EObject) value);
					}
					else {
						queue.addAll((Collection<? extends EObject>) value);
					}
				}
			}
			((SmartObject) obj).resetContainment();
			if(recursive)
				deleteNodes(obj.eContents(), recursive);
		}
	}
	
}
