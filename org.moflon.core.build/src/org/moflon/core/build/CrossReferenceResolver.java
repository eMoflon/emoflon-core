package org.moflon.core.build;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.gervarro.eclipse.task.ITask;
import org.moflon.core.utilities.WorkspaceHelper;

public class CrossReferenceResolver implements ITask {
	private static final String TASK_NAME = "Cross-reference resolving";

	private final Resource resource;
	private final UniqueEList<Resource> resources = new UniqueEList<Resource>();

	public CrossReferenceResolver(final Resource resource) {
		this.resource = resource;
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		final MultiStatus crossReferenceResolutionStatus = new MultiStatus(WorkspaceHelper.getPluginId(getClass()), 0,
				"Cross-reference resolution failed", null);
		resources.add(resource);
		for (int i = 0; i < resources.size(); i++) {
			final Resource resource = resources.get(i);
			for (final TreeIterator<EObject> j = resource.getAllContents(); j.hasNext();) {
				final EObject eObject = j.next();
				if (eObject instanceof EDataType) {
					j.prune();
				}
				for (final EObject eCrossReference : eObject.eCrossReferences()) {
					if (eCrossReference instanceof EClass) {
						if (eCrossReference.eIsProxy()) {
							final String proxyURI = eCrossReference instanceof InternalEObject
									? ((InternalEObject) eCrossReference).eProxyURI().toString() + " "
									: "";
							crossReferenceResolutionStatus.add(new Status(IStatus.ERROR,
									WorkspaceHelper.getPluginId(getClass()),
									"Unresolved cross reference " + proxyURI + "in " + EcoreUtil.getURI(eObject)));
						} else {
							final EPackage referencedEPackage = ((EClass) eCrossReference).getEPackage();
							if (resource != referencedEPackage.eResource()) {
								resources.add(referencedEPackage.eResource());
							}
						}
					}
				}
			}
		}
		return crossReferenceResolutionStatus;
	}

	@Override
	public final String getTaskName() {
		return TASK_NAME;
	}

	public final List<Resource> getResources() {
		return resources;
	}
}