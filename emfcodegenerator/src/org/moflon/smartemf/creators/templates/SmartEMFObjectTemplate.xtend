package org.moflon.smartemf.creators.templates

import java.io.File
import java.io.FileWriter
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EcorePackage
import org.moflon.smartemf.creators.FileCreator
import org.moflon.smartemf.creators.templates.util.CodeFormattingUtil
import org.moflon.smartemf.creators.templates.util.TemplateUtil

class SmartEMFObjectTemplate implements FileCreator {
	
	public val EClass eClass
	
	/**
	 * stores the fq-file name to which this interface shall be written to.
	 */
	var String file_path
	
	/**
	 * stores if this Creator was properly initialized
	 */
	var boolean is_initialized = false
	
	new(EClass eClass) {
		this.eClass = eClass
	}

	def String createCode() {
		val className = eClass.name
		val ePackage = eClass.EPackage
		val packageClassName = ePackage.name.toFirstUpper + "Package"
		val FQPackagePath = TemplateUtil.getFQName(ePackage)
		
		return '''
		package «FQPackagePath».impl;
		
		«FOR packages : getImportPackages()»
		import «TemplateUtil.getFQName(packages)».«packages.name.toFirstUpper»Package;
		«ENDFOR»
		
		import org.moflon.smartemf.runtime.*;
		import org.moflon.smartemf.runtime.collections.*;
		import org.moflon.smartemf.persistence.SmartEMFResource;
		import org.moflon.smartemf.runtime.notification.SmartEMFNotification;
		import org.moflon.smartemf.runtime.notification.NotifyStatus;
		
		import java.util.function.Consumer;
		
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EcoreFactory;
		import org.eclipse.emf.ecore.EcorePackage;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.emf.ecore.EStructuralFeature;
		import org.eclipse.emf.ecore.resource.Resource;
		
		public class «className»Impl extends SmartObject implements «TemplateUtil.getFQName(eClass)» {
		
		    «FOR feature : eClass.EAllStructuralFeatures»
		    protected «TemplateUtil.getFieldTypeName(feature)» «TemplateUtil.getValidName(feature.name)» = «getDefaultValue(feature)»;
			«ENDFOR»
			
			protected «eClass.name»Impl() {
				super(«packageClassName».Literals.«TemplateUtil.getLiteral(eClass)»);
			}
			
		    «FOR feature : eClass.EAllStructuralFeatures»
		    
		    @Override
		    public «TemplateUtil.getFieldTypeName(feature)» «getOrIs(feature)»«feature.name.toFirstUpper»() {
		    	return «TemplateUtil.getValidName(feature.name)»;
		    }
		    «IF !feature.isUnsettable»
		    
		    @Override
		    public void set«feature.name.toFirstUpper»(«TemplateUtil.getFieldTypeName(feature)» value) {
		    	«getSetterMethod(eClass, feature, FQPackagePath, packageClassName, true)»
		    }
		    «ENDIF»
		    
		    «IF feature instanceof EReference»
		    «IF feature.EOpposite !== null»
		    «IF feature.many»
		    private void add«feature.name.toFirstUpper»AsInverse(«TemplateUtil.getFQName(feature.EType)» value) {
		    	if(«TemplateUtil.getValidName(feature.name)».addInternal(value, false) == NotifyStatus.SUCCESS_NO_NOTIFICATION) {
		    		sendNotification(SmartEMFNotification.createAddNotification(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)», value, -1));
		    	} 
		    }
		    
		    private void remove«feature.name.toFirstUpper»AsInverse(«TemplateUtil.getFQName(feature.EType)» value) {
		    	«TemplateUtil.getValidName(feature.name)».removeInternal(value, false, true);
		    }
		    «ELSE»
		    private void set«feature.name.toFirstUpper»AsInverse(«TemplateUtil.getFQName(feature.EType)» value) {
			    «getSetterMethod(eClass, feature, FQPackagePath, packageClassName, false)»
		    }
		    «ENDIF»
		    «ENDIF»
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
		    	eDynamicSet(eFeature, newValue);
		    }
		    
		    @Override
		    public void eUnset(EStructuralFeature eFeature){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».equals(eFeature)) {
		    	 	«IF feature.isMany»
		    	 	get«feature.name.toFirstUpper»().clear(); 
		    	 	«ELSE»
		    	 	set«feature.name.toFirstUpper»((«TemplateUtil.getFieldTypeName(feature)»)«getDefaultValue(feature)»); 
		    	 	«ENDIF»
		    	 	return;
		    	 }
		    	«ENDFOR»
		    	eDynamicUnset(eFeature);
		    }
		
		    @Override
		    public String toString(){
				return «getToString()»
		    }
		
		 	@Override
		    public Object eGet(EStructuralFeature eFeature){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».equals(eFeature))
		    	 	return «getOrIs(feature)»«feature.name.toFirstUpper»();
		    	«ENDFOR»
		    	return eDynamicGet(eFeature);
		    }
		
		    @Override
		    public Object eGet(int featureID, boolean resolve, boolean coreType){
		    	throw new UnsupportedOperationException("This method has been deactivated since it is not always safe to use.");
		    }
		    
		    @Override
		    public void eInverseAdd(Object otherEnd, EStructuralFeature feature) {
			    «FOR ref : eClass.EAllReferences»
					«IF ref.EOpposite !== null»
			    	if («TemplateUtil.getPackageClassName(ref)».Literals.«TemplateUtil.getLiteral(ref)».equals(feature)) {
			    		«IF ref.isMany»
			    		add«ref.name.toFirstUpper»AsInverse((«TemplateUtil.getFQName(ref.EType)») otherEnd);
			    		«ELSE»
			    	 	set«ref.name.toFirstUpper»AsInverse((«TemplateUtil.getFQName(ref.EType)») otherEnd); 
			    		«ENDIF»
			    	 	return;
			        }	
			    	«ENDIF»
			    «ENDFOR»
			    if(feature == null)
			    	return;
			    	
		    	eDynamicInverseAdd(otherEnd, feature);
	    	}
		    	
		    @Override
	    	public void eInverseRemove(Object otherEnd, EStructuralFeature feature) {
		    	«FOR ref : eClass.EAllReferences»
			    	«IF ref.EOpposite !== null»
			    	if («TemplateUtil.getPackageClassName(ref)».Literals.«TemplateUtil.getLiteral(ref)».equals(feature)) {
			    		«IF ref.isMany»
			    		remove«ref.name.toFirstUpper»AsInverse((«TemplateUtil.getFQName(ref.EType)») otherEnd);
			    		«ELSE»
			    	 	set«ref.name.toFirstUpper»AsInverse(null); 
			    	 	«ENDIF»
			    	 	return;
			        }
			    	«ENDIF»
			    «ENDFOR»
			    if(feature == null)
			    	return;
			    		    		
		    	eDynamicInverseRemove(otherEnd, feature);
	    	}
		    
		    @Override
		    /**
		    * This method sets the resource and generates REMOVING_ADAPTER and ADD notifications
		    */
		    protected void setResourceOfContainments(Consumer<SmartObject> setResourceCall) {
		    	«FOR feature : eClass.EAllContainments»
		    	«IF feature.isMany && !"EFeatureMapEntry".equals(feature.EType.name) && !feature.EType.name.contains("MapEntry")»
		    	for(Object obj : get«feature.name.toFirstUpper»()) {
		    		setResourceCall.accept(((SmartObject) obj));
	    		}
	    		«ELSEIF "EFeatureMapEntry".equals(feature.EType.name) || feature.EType.name.contains("MapEntry")»
	    		for(Object obj : get«feature.name.toFirstUpper»().entrySet()) {
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
		    protected void setResourceOfContainmentsSilently(Resource r) { 		
		    	«FOR feature : eClass.EAllContainments»
		    	«IF feature.isMany && !"EFeatureMapEntry".equals(feature.EType.name) && !feature.EType.name.contains("MapEntry")»
		    	for(Object obj : get«feature.name.toFirstUpper»()) {
		    		((SmartObject) obj).setResourceSilently(r);
	    		}
	    		«ELSEIF "EFeatureMapEntry".equals(feature.EType.name) || feature.EType.name.contains("MapEntry")»
	    		for(Object obj : get«feature.name.toFirstUpper»().entrySet()) {
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
	
	def getToString() {
		var EAttribute nameAttribute = null
		var EAttribute firstStringAttribute = null
		for(attr : eClass.EAllAttributes) {
			if(attr.EType.equals(EcorePackage.eINSTANCE.EString)) {
				firstStringAttribute = attr
				if(attr.name.equals("name")) {
					nameAttribute = attr
				}
			}
		}
		if(firstStringAttribute === null)
			return '''super.toString();'''
		if(nameAttribute !== null)
			return '''super.toString() + "(name: " + getName() + ")";'''
		return '''super.toString() + "(name: " + get«firstStringAttribute.name.toFirstUpper»() + ")";'''
			
	}
	
	def getDefaultValue(EStructuralFeature feature) {
		if("EFeatureMapEntry".equals(feature.EType.name))
			return "new java.util.HashMap<Object, Object>()"
			
		
		if(feature.EType.name.contains("MapEntry"))
			return "new java.util.HashMap<Object, Object>()"
			
		if(feature.isMany)
			return '''new «TemplateUtil.getFieldTypeName(feature)»(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)»);'''
			
		val value = feature.defaultValue
		if(value === null)
			return "null"
		
		if(value instanceof EEnumLiteral)
			return TemplateUtil.getFQName(value.EEnum.EPackage) + "." + value.EEnum.name + "." + TemplateUtil.getLiteral(value)
			
		if(feature.EType.equals(EcorePackage.Literals.EDATE)) {
			return '''(java.util.Date) EcoreFactory.eINSTANCE.createFromString(EcorePackage.eINSTANCE.getEDate(), "«feature.defaultValueLiteral»")'''
		}
		
		if(feature.EType == EcorePackage.Literals.ESTRING)	
			return '''"«value»"'''
			
		return value;
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
	
	def getSetterMethod(EClass eClass, EStructuralFeature feature, String FQPackagePath, String packageClassName, boolean inverse) {
		if(feature instanceof EAttribute) {
			return '''
	        Object oldValue = «TemplateUtil.getValidName(feature.name)»;
	        «TemplateUtil.getValidName(feature.name)» = value;

        	sendNotification(SmartEMFNotification.createSetNotification(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)», oldValue, value, -1));'''
		}
		if(feature instanceof EReference) {
			if(feature.isMany)
					return '''throw new UnsupportedOperationException("Set methods for SmartEMF collections are not supported.");'''
			
			return '''
			
			Object oldValue = «TemplateUtil.getValidName(feature.name)»;
	        «TemplateUtil.getValidName(feature.name)» = value;
			if(value == null && oldValue == null)
				return;
				
			if(value != null && value.equals(oldValue))
				return;

	        «IF feature.containment»
	        NotifyStatus status = NotifyStatus.SUCCESS_NOTIFICATION_SEND;
	        «ENDIF»
	        «IF feature.isMany && !"EFeatureMapEntry".equals(feature.EType.name) && !feature.EType.name.contains("MapEntry")»
	        
			if(value instanceof «TemplateUtil.getListTypeName(feature)»){
	        	«TemplateUtil.getValidName(feature.name)» = («TemplateUtil.getFieldTypeName(feature)») value;
			} else {
			    throw new IllegalArgumentException();
			}
	        «ENDIF»
	        «IF "EFeatureMapEntry".equals(feature.EType.name) || feature.EType.name.contains("MapEntry")»
	        «TemplateUtil.getValidName(feature.name)» = («TemplateUtil.getFieldTypeName(feature)») value;
	        «ENDIF»

			«IF feature.containment»
			if(value != null)
				status = ((MinimalSObjectContainer) «TemplateUtil.getValidName(feature.name)»).setContainment(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)»);
			
			if(status == NotifyStatus.SUCCESS_NO_NOTIFICATION)
			«ENDIF»
        	sendNotification(SmartEMFNotification.createSetNotification(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)», oldValue, value, -1));
        	
        	«IF inverse && feature.EOpposite !== null»
        	if(oldValue != null) {
        		((SmartObject) oldValue).eInverseRemove(this, «TemplateUtil.getPackageClassName(feature.EOpposite)».Literals.«TemplateUtil.getLiteral(feature.EOpposite)»);
        	}
        	if(value != null) {
        		((SmartObject) value).eInverseAdd(this, «TemplateUtil.getPackageClassName(feature.EOpposite)».Literals.«TemplateUtil.getLiteral(feature.EOpposite)»);
        	}
        	«ELSE»
        	«IF feature instanceof EReference && inverse»
        	if(«TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».getEOpposite() != null) {
        		if(oldValue != null) {
        			((SmartObject) oldValue).eInverseRemove(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».getEOpposite());
        		}
        		if(value != null) {
        		    ((SmartObject) value).eInverseAdd(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».getEOpposite());
        		}
        	}
        	«ENDIF»
        	«ENDIF»
        	«IF feature.containment»
        	if(oldValue != null) {
				status = ((MinimalSObjectContainer) oldValue).resetContainment();
				«IF inverse && feature.EOpposite !== null»
        		((SmartObject) oldValue).eInverseRemove(this, «TemplateUtil.getPackageClassName(feature.EOpposite)».Literals.«TemplateUtil.getLiteral(feature.EOpposite)»);
				«ENDIF»
				return;
			}
        	«ENDIF»'''
		}
	}
	
	def getPackage() {
		return eClass.EPackage
	}
	
	override initialize_creator(String fq_file_path) {
		file_path = fq_file_path
		is_initialized = true;
	}
	
	override write_to_file() {
		if(!is_initialized)
			throw new RuntimeException('''The «this.class» was not initialized.'''.toString)
			
//		var class_file = new File('''«path»/impl/«eClass.name»Impl.java''')
		var class_file = new File(file_path)
		class_file.getParentFile().mkdirs()
		var class_fw = new FileWriter(class_file , false)
		class_fw.write(CodeFormattingUtil.format(createCode));
		class_fw.close
	}
}