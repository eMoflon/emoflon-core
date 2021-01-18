package emfcodegenerator.util

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EFactory
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.common.util.EList
import java.lang.reflect.InvocationTargetException
import org.eclipse.emf.common.notify.Notification
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.EPackage.Descriptor
import org.eclipse.emf.ecore.EPackage.Registry
import org.eclipse.emf.ecore.impl.EClassImpl
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.impl.EReferenceImpl
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.impl.EAttributeImpl
import org.eclipse.emf.ecore.impl.EDataTypeImpl
import org.eclipse.emf.ecore.impl.EOperationImpl

abstract class SmartPackage implements EPackage {

	/**########################Attributes########################*/

	var protected EcorePackage ecorePackage

	var protected EcoreFactory ecoreFactory

	var protected EFactory eFactoryInstance

	var protected static boolean isInited = false

	var protected boolean isCreated = false

	var protected boolean isInitialized = false

	var protected EList<EPackage> SUB_PACKAGES
	
	var protected EPackage SUPER_PACKAGE

	/**########################Constructor########################*/

	new(String packageURI, EFactory factory){
		var Object registration = Registry.INSTANCE.get(packageURI);
		if (registration instanceof Descriptor){
  			val Descriptor descriptor = registration as Descriptor
  			val long threadId = Thread.currentThread().getId();
  			Registry.INSTANCE.put(
  				packageURI,
     			new Descriptor(){
       				override EPackage getEPackage() {
         				return (Thread.currentThread().getId() === threadId) ?
         					SmartPackage.this : descriptor.getEPackage()
   					}
       				override EFactory getEFactory() {
         				return (Thread.currentThread().getId() === threadId) ?
         					factory : descriptor.getEFactory()
       				}
   				}
			)
		} else {
  			Registry.INSTANCE.put(packageURI, this)
		}
		setEFactoryInstance(factory)

		if (factory == EcoreFactory.eINSTANCE) {
  			this.ecorePackage = this as EcorePackage
  			this.ecoreFactory = factory as EcoreFactory
		} else {
  			this.ecorePackage = EcorePackage.eINSTANCE;
  			this.ecoreFactory = EcoreFactory.eINSTANCE;
		}
	}

	/**########################Abstract Methods########################*/

	//is generated
	abstract override getNsPrefix()

	//is generated
	abstract override setNsPrefix(String value)

	//is generated
	abstract override getNsURI()

	//is generated
	abstract override setNsURI(String value)

	//is generated
	abstract override getName()

	//is generated
	abstract override setName(String value)

	//is generated
	abstract override getEClassifiers()

	//is generated
	abstract override getEClassifier(String name)

	override getESubpackages(){
		return this.SUB_PACKAGES
	}

	override getESuperPackage(){
		return this.SUPER_PACKAGE
	}
	
	abstract override eContents()

	abstract override eAllContents()

	/**########################Creators Methods########################*/

	def protected EClassImpl createEClass(int ID){
		var EClassImpl c = ecoreFactory.createEClass() as EClassImpl;
    	c.setClassifierID(ID)
    	getEClassifiers().add(c)
    	return c
	}
	
	def protected EDataType createEDataType(int ID){
		var EDataTypeImpl d = ecoreFactory.createEDataType() as EDataTypeImpl
		d.setClassifierID(ID)
		getEClassifiers().add(d)
		return d
  	}

	def protected void createEAttribute(EClass owner, int ID){
    	var EAttributeImpl a = ecoreFactory.createEAttribute() as EAttributeImpl
		a.setFeatureID(ID)
		owner.getEStructuralFeatures().add(a)
	}

	def protected void createEReference(EClass owner, int ID){
    	var EReferenceImpl r = ecoreFactory.createEReference() as EReferenceImpl
    	r.setFeatureID(ID);
		owner.getEStructuralFeatures().add(r);
	}

	def protected void createEOperation(EClass owner, int ID) {
	    var EOperationImpl o = ecoreFactory.createEOperation() as EOperationImpl
	    o.setOperationID(ID);
	    owner.getEOperations().add(o);
	}

	override getEFactoryInstance() {
		return this.eFactoryInstance
	}

	override setEFactoryInstance(EFactory value) {
		this.eFactoryInstance = value
	}

	/**########################Other Methods########################*/

	override getEAnnotation(String source) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override getEAnnotations() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eClass() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eContainer() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eContainingFeature() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eContainmentFeature() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eCrossReferences() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eGet(EStructuralFeature feature) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eGet(EStructuralFeature feature, boolean resolve) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eIsProxy() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eIsSet(EStructuralFeature feature) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eResource() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eSet(EStructuralFeature feature, Object newValue) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eUnset(EStructuralFeature feature) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eAdapters() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eDeliver() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eNotify(Notification notification) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override eSetDeliver(boolean deliver) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
}