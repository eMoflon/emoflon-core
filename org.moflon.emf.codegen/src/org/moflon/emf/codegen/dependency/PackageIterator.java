package org.moflon.emf.codegen.dependency;

import java.util.Iterator;

import org.eclipse.emf.common.util.AbstractTreeIterator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EContentsEList;

public class PackageIterator extends AbstractTreeIterator<EPackage> {
	private static final long serialVersionUID = 3213481216027600632L;

	private static final EStructuralFeature[] SUBPACKAGES = new EStructuralFeature[] {
			EcorePackage.Literals.EPACKAGE__ESUBPACKAGES };

	public PackageIterator(EPackage ePackage) {
		super(ePackage);
	}

	@Override
	protected Iterator<? extends EPackage> getChildren(Object object) {
		if (EcorePackage.Literals.EPACKAGE.isInstance(object)) {
			return new EContentsEList<EPackage>((EPackage) object, SUBPACKAGES).iterator();
		} else {
			EContentsEList<EPackage> list = EContentsEList.emptyContentsEList();
			return list.iterator();
		}
	}

}
