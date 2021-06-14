package org.moflon.smartemf.creators.templates

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.EClassifier
import java.io.File
import java.io.FileWriter
import java.util.LinkedList
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EEnumLiteral
import org.moflon.smartemf.creators.templates.util.TemplateUtil;

class SmartEMFObjectTemplate {
	
	var EClass eClass = null
	
	new(EClass eClass) {
		this.eClass = eClass
	}
	
	def createCode() {
		val className = eClass.name
		val ePackage = eClass.EPackage
		val packageClassName = ePackage.name.toFirstUpper + "Package"
		val FQPackagePath = TemplateUtil.getFQName(ePackage)
		
		return '''
		package «FQPackagePath».impl;
		
		«FOR packages : getImportPackages()»
		import «TemplateUtil.getFQName(packages)».«packages.name.toFirstUpper»Package;
		«ENDFOR»
		
		«FOR featureType : getImportTypes()»
		import «TemplateUtil.getFQName(featureType)».«featureType.name»;
		«ENDFOR»
		import org.moflon.smartemf.runtime.notification.SmartEMFNotification;
		import org.moflon.smartemf.runtime.*;
		import org.moflon.smartemf.runtime.collections.*;
		
		import java.util.function.Consumer;
		
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EcoreFactory;
		import org.eclipse.emf.ecore.EcorePackage;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.emf.ecore.EStructuralFeature;
		import org.eclipse.emf.ecore.resource.Resource;
		
		public class «className»Impl extends SmartObject implements «className» {
		
		    «FOR feature : eClass.EAllStructuralFeatures»
		    protected «TemplateUtil.getFieldTypeName(feature)» «TemplateUtil.getValidName(feature.name)» = «getDefaultValue(feature)»;
			«ENDFOR»
			
			protected «eClass.name»Impl() {
				super(«packageClassName».Literals.«TemplateUtil.getLiteral(eClass)»);
			}
			
		    «FOR feature : eClass.EAllStructuralFeatures»
		    @Override
		    public «IF feature.isMany»EList<«feature.EType.name»>«ELSE»«TemplateUtil.getFieldTypeName(feature)»«ENDIF» «getOrIs(feature)»«feature.name.toFirstUpper»() {
		    	return «TemplateUtil.getValidName(feature.name)»;
		    }
		    
		    «IF !feature.isUnsettable»
		    @Override
		    public void set«feature.name.toFirstUpper»(«TemplateUtil.getFieldTypeName(feature)» value) {
		    	«getSetterMethod(eClass, feature, FQPackagePath, packageClassName)»
		    }
		    «ENDIF»
		    «ENDFOR»
		
		    @Override
		    public void eSet(EStructuralFeature eFeature, Object newValue){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».equals(eFeature)) {
		    	 	set«feature.name.toFirstUpper»((«TemplateUtil.getFieldTypeName(feature)») newValue); 
		    	 	return;
		    	 }
		    	«ENDFOR»
		    }
		    
		    @Override
		    public void eUnset(EStructuralFeature eFeature){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».equals(eFeature)) {
		    	 	«IF feature.isMany»
		    	 	get«feature.name.toFirstUpper»().clear(); 
		    	 	«ELSE»
		    	 	set«feature.name.toFirstUpper»(«getDefaultValue(feature)»); 
		    	 	«ENDIF»
		    	 	return;
		    	 }
		    	«ENDFOR»
		    }
		
		    @Override
		    public String toString(){
		        StringBuilder result = new StringBuilder(super.toString() + "(name: «className») ");
		        result.append(" (");
	    		«FOR feature : eClass.EAllStructuralFeatures SEPARATOR 
	    		'''
	    		result.append(",\n");
	    		'''»
	    		«IF feature.EType.defaultValue === null»
	    		if(«TemplateUtil.getValidName(feature.name)» != null)
	    		«ENDIF»
				result.append("«feature.name»: «TemplateUtil.getValidName(feature.name)»");
		        «ENDFOR»
		        return result.toString();
		    }
		
		 	@Override
		    public Object eGet(EStructuralFeature eFeature){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».equals(eFeature))
		    	 	return «getOrIs(feature)»«feature.name.toFirstUpper»();
		    	«ENDFOR»
		        return null;
		    }
		
		    @Override
		    public Object eGet(int featureID, boolean resolve, boolean coreType){
		    	throw new UnsupportedOperationException("This method has been deactivated since it is not always safe to use.");
«««		        switch(featureID) {
«««	    			«FOR feature : eClass.EAllStructuralFeatures»
«««	    			case «getPackageClassName(feature)».«getLiteralID(feature)»:
«««	    				return «getOrIs(feature)»«feature.name.toFirstUpper»();
«««					«ENDFOR»
«««		        }
«««		        return null;
		    }
		    
		    @Override
		    /**
		    * This method sets the resource and generates REMOVING_ADAPTER and ADD notifications
		    */
		    public void setResource(Resource r) {
		    	// stop if we encounter the same resource already
	    		if(eResource() == null && r == null) 
			    	return;
			    
			    if(eResource() != null && eResource().equals(r))
			    	return;
	    			
				// send remove messages to old adapters
				// TODO lfritsche: should we optimize this and only do this if the adapters of both resources differ?
				sendRemoveAdapterNotification(this);
	    			
	    		super.setResource(r);
	    		
	    		Consumer<SmartObject> setResourceCall = (o) -> o.setResource(r);
	    		
	    		if(r != null) {
					sendNotification(SmartEMFNotification.createAddNotification(eContainer(), eContainingFeature(), this, -1));
					// if cascading is activated, we recursively generate add messages; else just this once
					if(!smartResource().getCascade())
	    				setResourceCall = (o) -> o.setResourceSilently(r);
				}
	    		
		    	«FOR feature : eClass.EAllContainments»
		    	«IF feature.isMany»
		    	for(Object obj : get«feature.name.toFirstUpper»()) {
		    		setResourceCall.accept(((SmartObject) obj));
	    		}
	    		«ELSE»
	    		if(get«feature.name.toFirstUpper»() != null)
	    			setResourceCall.accept((SmartObject) get«feature.name.toFirstUpper»());
	    		«ENDIF»
		    	«ENDFOR»
	    	}
	    	
	    	@Override
	    	/**
	    	* This method sets the resource and only generates REMOVING_ADAPTER notifications (no ADD messages)
	    	*/
		    public void setResourceSilently(Resource r) {
		    	// stop if we encounter the same resource already
	    		if(eResource() == null && r == null) 
			    	return;
			    
			    if(eResource() != null && eResource().equals(r))
			    	return;
	    			
    			// send remove messages to old adapters
				// TODO lfritsche: should we optimize this and only do this if the adapters of both resources differ?
				sendRemoveAdapterNotification(this);
	    			
	    		super.setResource(r);
	    		
		    	«FOR feature : eClass.EAllContainments»
		    	«IF feature.isMany»
		    	for(Object obj : get«feature.name.toFirstUpper»()) {
		    		((SmartObject) obj).setResourceSilently(r);
	    		}
	    		«ELSE»
	    		if(get«feature.name.toFirstUpper»() != null)
	    		    ((SmartObject) get«feature.name.toFirstUpper»()).setResourceSilently(r);
	    		«ENDIF»
		    	«ENDFOR»
	    	}
		}
		'''
	}
	
	def getDefaultValue(EStructuralFeature feature) {
		if(feature.isMany)
			return '''new «TemplateUtil.getListTypeName(feature)»<«feature.EType.name»>(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)»);'''
			
		val value = feature.defaultValue
		if(value === null)
			return "null"
		
		if(value instanceof EEnumLiteral)
			return value.EEnum.name + "." + TemplateUtil.getLiteral(value)
			
		if(feature.EType.equals(EcorePackage.Literals.EDATE)) {
			return '''(java.util.Date) EcoreFactory.eINSTANCE.createFromString(EcorePackage.eINSTANCE.getEDate(), "«feature.defaultValueLiteral»")'''
		}
			
		return value
	}
	
	def getOrIs(EStructuralFeature feature) {
		if(feature.EType.equals(EcorePackage.Literals.EBOOLEAN))
			return "is"
		else
			return "get"
	}
	
	def getImportPackages() {
		var packages = eClass.EAllSuperTypes.map[c|c.EPackage].toSet
		packages.add(eClass.EPackage)
		return packages
	}
	
	def getImportTypes() {
		// estructural feature types
		val types = eClass.EAllStructuralFeatures.map[c|c.EType].filter[c|!c.EPackage.equals(EcorePackage.eINSTANCE)].toSet
		// add this eclass
		types.add(eClass)
		return types
	}
	
	def getSetterMethod(EClass eClass, EStructuralFeature feature, String FQPackagePath, String packageClassName) {
		if(feature instanceof EAttribute) {
			return '''
	        Object oldValue = «TemplateUtil.getValidName(feature.name)»;
	        «TemplateUtil.getValidName(feature.name)» = value;

        	sendNotification(SmartEMFNotification.createSetNotification(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)», oldValue, value, -1));'''
		}
		if(feature instanceof EReference) {
			return '''
			Object oldValue = «TemplateUtil.getValidName(feature.name)»;
	        «IF feature.containment»
			if(«TemplateUtil.getValidName(feature.name)» != null) {
				((MinimalSObjectContainer) «TemplateUtil.getValidName(feature.name)»).resetContainment();
			}
	        «ENDIF»
	        «TemplateUtil.getValidName(feature.name)» = value;
			
	        «IF feature.isMany»
			if(value instanceof «TemplateUtil.getListTypeName(feature)»){
	        	«TemplateUtil.getValidName(feature.name)» = («TemplateUtil.getFieldTypeName(feature)») value;
			} else {
			    throw new IllegalArgumentException();
			}
	        «ENDIF»

			«IF feature.containment»
			if(value != null)
				((MinimalSObjectContainer) «TemplateUtil.getValidName(feature.name)»).setContainment(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)»);
			«ENDIF»

        	sendNotification(SmartEMFNotification.createSetNotification(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)», oldValue, value, -1));'''
		}
	}
	
	def writeToFile(String path) {
		var class_file = new File('''«path»/impl/«eClass.name»Impl.java''')
		class_file.getParentFile().mkdirs()
		var class_fw = new FileWriter(class_file , false)
		class_fw.write(createCode)
		class_fw.close
	} 
	
	def getPackage() {
		return eClass.EPackage
	}
}