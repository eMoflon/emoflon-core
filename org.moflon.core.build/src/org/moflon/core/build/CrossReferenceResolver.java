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

/**
 * Collects all cross references of a given {@link Resource}, which is
 * configured in the constructor
 * 
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge - Docu
 */
public class CrossReferenceResolver implements ITask {
	private final Resource resource;
	private final UniqueEList<Resource> resources = new UniqueEList<Resource>();

	/**
	 * Configures the original {@link Resource} from which all cross-references
	 * shall be explored.
	 * 
	 * @param resource
	 *            the {@link Resource} to explore
	 */
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
			exploreResource(resource, crossReferenceResolutionStatus);
		}
		return crossReferenceResolutionStatus.isOK() ? Status.OK_STATUS : crossReferenceResolutionStatus;
	}

	/**
	 * Traverses the given {@link Resource} and adds all Resources to
	 * {@link #getResources()} that are cross-references by some {@link EClass} in
	 * the given resource
	 * 
	 * @param anchorResource
	 * @param crossReferenceResolutionStatus
	 */
	private void exploreResource(final Resource anchorResource, final MultiStatus crossReferenceResolutionStatus) {
		for (final TreeIterator<EObject> j = anchorResource.getAllContents(); j.hasNext();) {
			final EObject eObject = j.next();
			if (eObject instanceof EDataType) {
				j.prune();
			}
			for (final EObject eCrossReference : eObject.eCrossReferences()) {
				if (eCrossReference instanceof EClass) {
					if (eCrossReference.eIsProxy()) {
						final Status status = createProxyReportingStatus(eObject, eCrossReference);
						crossReferenceResolutionStatus.add(status);
					} else {
						final EPackage referencedEPackage = ((EClass) eCrossReference).getEPackage();
						if (anchorResource != referencedEPackage.eResource()) {
							resources.add(referencedEPackage.eResource());
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the task name
	 */
	@Override
	public final String getTaskName() {
		return "Cross-reference resolving";
	}

	/**
	 * The set of all cross-referenced {@link Resource}s of the configured resource
	 * 
	 * @return
	 */
	public final List<Resource> getResources() {
		return resources;
	}

	/**
	 * Creates a {@link Status} that reports about an unresolved proxy
	 * 
	 * @param eObject
	 *            the object
	 * @param eCrossReference
	 *            the unresolved proxy
	 * @return the status
	 */
	private Status createProxyReportingStatus(final EObject eObject, final EObject eCrossReference) {
		final String proxyURI = eCrossReference instanceof InternalEObject
				? ((InternalEObject) eCrossReference).eProxyURI().toString() + " "
				: "";
		Status status = new Status(IStatus.ERROR, WorkspaceHelper.getPluginId(getClass()),
				"Unresolved cross reference " + proxyURI + "in " + EcoreUtil.getURI(eObject));
		return status;
	}
}