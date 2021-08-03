package org.moflon.smartemf.runtime.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.moflon.smartemf.runtime.SmartObject;
import org.moflon.smartemf.runtime.SmartPackage;
import org.moflon.smartemf.runtime.collections.ReplacingIterator;
import org.moflon.smartemf.runtime.collections.SmartCollection;

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

	@SuppressWarnings("unchecked")
	private static void deleteNodes(Queue<EObject> queue, boolean recursive) {
		while (!queue.isEmpty()) {
			EObject obj = queue.poll();

			for (EReference ref : obj.eClass().getEAllReferences()) {
				if (ref.isContainment() && !recursive)
					continue;

				Object value = obj.eGet(ref);

				if (recursive && ref.isContainment()) {
					if (value == null)
						continue;

					if (value instanceof EObject) {
						queue.add((EObject) value);
					} else {
						queue.addAll((Collection<? extends EObject>) value);
					}
				}
				obj.eUnset(ref);
			}

//			// HOTFIX until default bidirectional edges works
//			EObject container = obj.eContainer();
//			EReference containmentFeature = obj.eContainmentFeature();
//			if (container != null && containmentFeature != null) {
//				if (containmentFeature.isMany())
//					((List<?>) container.eGet(containmentFeature)).remove(obj);
//				else
//					container.eUnset(containmentFeature);
//			}
//			// END HOTFIX

			((SmartObject) obj).resetContainment();
			if (recursive)
				deleteNodes(obj.eContents(), recursive);
		}
	}

	public static void resolveAll(ResourceSet resourceSet) {
		disconnectAdapters(resourceSet);

		for (Resource resource : resourceSet.getResources())
			for (EObject eObject : resource.getContents())
				resolveAll(eObject);

		reconnectAdapters(resourceSet);
	}

	public static void resolveAll(Resource resource) {
		disconnectAdapters(resource.getResourceSet());

		for (EObject eObject : resource.getContents())
			resolveAll(eObject);

		reconnectAdapters(resource.getResourceSet());
	}

	private static void resolveAll(EObject eObject) {
		resolveProxyContainer(eObject);
		resolveCrossReferences(eObject);
		for (Iterator<EObject> it = eObject.eAllContents(); it.hasNext();)
			resolveCrossReferences(it.next());
	}

	@SuppressWarnings("unchecked")
	private static void resolveCrossReferences(EObject eObject) {
		// for all but SmartEMF objects we assume the references are resolved by iterating over them
		if (eObject instanceof SmartObject) {
			for (EReference reference : eObject.eClass().getEAllReferences()) {
				if (reference.isContainment())
					continue;
				if (reference.isMany()) {
					SmartCollection<EObject, ?> values = (SmartCollection<EObject, ?>) eObject.eGet(reference);
					for (ReplacingIterator<EObject> it = values.replacingIterator(); it.hasNext();) {
						EObject value = it.next();
						if (value.eIsProxy()) {
							EObject resolved = EcoreUtil.resolve(value, eObject);
							if (value != resolved)
								it.replace(resolved);
						}
					}
				} else {
					EObject value = (EObject) eObject.eGet(reference);
					if (value != null && value.eIsProxy()) {
						EObject resolved = EcoreUtil.resolve(value, eObject);
						if (value != resolved)
							eObject.eSet(reference, resolved);
					}
				}
			}
		} else {
			for (Iterator<EObject> i = eObject.eCrossReferences().iterator(); i.hasNext(); i.next()) {
			}
		}
	}

	private static void resolveProxyContainer(EObject eObject) {
		// for all but SmartEMF objects we assume the container is resolved by calling EObject#eContainer
		EObject eContainer = eObject.eContainer();
		if (eContainer != null && eContainer instanceof SmartObject && eContainer.eIsProxy()) {
			EObject resolved = EcoreUtil.resolve(eContainer, eObject);
			if (resolved != eContainer)
				((SmartObject) eObject).setContainment(eObject, eObject.eContainingFeature());
		}
	}

	private static List<Adapter> resourceSetAdapters;
	private static Map<Resource, List<Adapter>> resource2adapters;

	private static void disconnectAdapters(ResourceSet resourceSet) {
		resourceSetAdapters = new LinkedList<>(resourceSet.eAdapters());
		resourceSet.eAdapters().clear();

		resource2adapters = new HashMap<>();
		for (Resource resource : resourceSet.getResources()) {
			List<Adapter> adapters = new LinkedList<>(resource.eAdapters());
			resource2adapters.put(resource, adapters);
			resource.eAdapters().clear();
		}
	}

	private static void reconnectAdapters(ResourceSet resourceSet) {
		resourceSet.eAdapters().addAll(resourceSetAdapters);

		for (Resource resource : resourceSet.getResources()) {
			List<Adapter> adapters = resource2adapters.get(resource);
			resource.eAdapters().addAll(adapters);
		}
	}

	public static List<EReference> getEAllNonDynamicReferences(EClass eClass) {
		EList<EReference> refs = eClass.getEAllReferences();

		if (eClass.getEPackage() instanceof SmartPackage) {
			SmartPackage smartPackage = (SmartPackage) eClass.getEPackage();
			return refs.stream() //
					.filter(r -> !smartPackage.isDynamicEStructuralFeature(eClass, r)) //
					.collect(Collectors.toList());
		}

		return refs;
	}

}
