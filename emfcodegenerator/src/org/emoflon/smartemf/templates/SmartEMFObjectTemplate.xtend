package org.emoflon.smartemf.templates

import java.util.LinkedList
import java.util.List
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EcorePackage
import org.emoflon.smartemf.templates.util.TemplateUtil

class SmartEMFObjectTemplate implements CodeTemplate{
	
	var GenPackage genPack
	var EClass eClass
	var String path
	
	new(GenPackage genPack, EClass eClass, String path) {
		this.genPack = genPack
		this.eClass = eClass
		this.path = path
	}
	
	override createCode() {
		val className = eClass.name
		val ePackage = eClass.EPackage
		val packageClassName = ePackage.name.toFirstUpper + "Package"
		val FQPackagePath = TemplateUtil.getFQName(ePackage)
		
		var code = '''
		package «TemplateUtil.getImplSuffix(genPack)»;
		
		import «TemplateUtil.getMetadataSuffix(genPack)».«TemplateUtil.getPackageClassName(genPack)»;
		«FOR importedGenPack : TemplateUtil.getImportPackages(eClass)»
		import «TemplateUtil.getMetadataSuffix(importedGenPack)».«TemplateUtil.getPackageClassName(importedGenPack)»;
 		«ENDFOR»
		
		import org.emoflon.smartemf.runtime.*;
		import org.emoflon.smartemf.runtime.collections.*;
		import org.emoflon.smartemf.persistence.SmartEMFResource;
		import org.emoflon.smartemf.runtime.notification.SmartEMFNotification;
		import org.emoflon.smartemf.runtime.notification.NotifyStatus;
		
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
		    public «TemplateUtil.getFieldTypeName(feature)» «TemplateUtil.getOrIs(feature)»«feature.name.toFirstUpper»() {
		    	return this.«TemplateUtil.getValidName(feature.name)»;
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
		    private void add«TemplateUtil.getFeatureName(feature)»AsInverse(«TemplateUtil.getFQName(feature.EType)» value) {
		    	if(this.«TemplateUtil.getValidName(feature.name)».addInternal(value, false) == NotifyStatus.SUCCESS_NO_NOTIFICATION) {
		    sendNotification(SmartEMFNotification.createAddNotification(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)», value, -1));
		    	} 
		    }
		    
		    private void remove«TemplateUtil.getFeatureName(feature)»AsInverse(«TemplateUtil.getFQName(feature.EType)» value) {
		    	«TemplateUtil.getValidName(feature.name)».removeInternal(value, false, true);
		    }
		    «ELSE»
		    private void set«TemplateUtil.getFeatureName(feature)»AsInverse(«TemplateUtil.getFQName(feature.EType)» value) {
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
		    	 	set«TemplateUtil.getFeatureName(feature)»((«TemplateUtil.getFieldTypeName(feature)») newValue); 
		    	 	return;
		    	 }
		    	«ENDFOR»
		    	eDynamicSet(eFeature, newValue);
		    }
		    
		    @Override
		    public void eUnset(EStructuralFeature eFeature){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».equals(eFeature)) {
		    	 	«IF feature.isMany && feature instanceof EReference»
		    	 	get«TemplateUtil.getFeatureName(feature)»().clear(); 
		    	 	«ELSE»
		    	 	set«TemplateUtil.getFeatureName(feature)»((«TemplateUtil.getFieldTypeName(feature)»)«getDefaultValue(feature)»); 
		    	 	«ENDIF»
		    	 	return;
		    	 }
		    	«ENDFOR»
		    	eDynamicUnset(eFeature);
		    }
		
		    @Override
		    public String toString(){
				«getToString()»
		    }
		
		 	@Override
		    public Object eGet(EStructuralFeature eFeature){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)».equals(eFeature))
		    	 	return «TemplateUtil.getOrIs(feature)»«feature.name.toFirstUpper»();
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
		TemplateUtil.writeToFile(path + TemplateUtil.getFQImplName(genPack, eClass).replace(".", "/") + "Impl.java", code);
	}
	
	def getToString() {
		var EAttribute nameAttr = null
		var printableAttributes = new LinkedList
		for (attr : eClass.EAllAttributes) {
			if (attr.name.equals("name") && attr.EType.equals(EcorePackage.eINSTANCE.EString))
				nameAttr = attr
			else if (!attr.isMany)
				printableAttributes.add(attr)
		}
		
		if (nameAttr === null && printableAttributes.empty)
			return '''return super.toString();'''
		
		return '''
		StringBuilder b = new StringBuilder();
		b.append(super.toString());
		b.append(" (");
		if (SmartEMFConfig.simpleStringRepresentations()) {
			«getSimpleAttrRepresentation(nameAttr, printableAttributes)»
		} else {
			«getDefaultAttrRepresentation(nameAttr, printableAttributes)»
		}
		b.append(")");
		return b.toString();
		'''
	}
	
	def getDefaultAttrRepresentation(EAttribute nameAttr, List<EAttribute> printableAttributes) {
		'''
		«IF nameAttr !== null»
			b.append("name: ");
			b.append(getName());
		«ENDIF»
		«IF nameAttr !== null && !printableAttributes.empty»
			b.append(", ");
		«ENDIF»
		«FOR attr : printableAttributes SEPARATOR '''b.append(", ");'''»
			b.append("«attr.name»: ");
			b.append(«TemplateUtil.getOrIs(attr)»«attr.name.toFirstUpper»());
		«ENDFOR»
		'''
	}
	
	def getSimpleAttrRepresentation(EAttribute nameAttr, List<EAttribute> printableAttributes) {
		if (nameAttr !== null)
			return '''b.append(getName());'''
		
		var firstAttr = printableAttributes.get(0)
		return '''
		b.append("«firstAttr.name»: ");
		b.append(«TemplateUtil.getOrIs(firstAttr)»«firstAttr.name.toFirstUpper»());
		'''
	}
	
	def getDefaultValue(EStructuralFeature feature) {
		if("EFeatureMapEntry".equals(feature.EType.name))
			return "new java.util.HashMap<Object, Object>()"
			
		if(feature.EType.name.contains("MapEntry"))
			return "new java.util.HashMap<Object, Object>()"
			
		if(feature.isMany && feature instanceof EReference)
			return '''new «TemplateUtil.getFieldTypeName(feature)»(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)»)'''
			
		val value = feature.defaultValue
		if(value === null) {
			switch(feature.EType.name) {
				case "boolean" : return "false"
				case "double" : return "0.0"
				case "float" : return "0.0f"
				case "int" : return "0"
				case "Boolean" : return "false"
				case "Double" : return "0.0"
				case "Float" : return "0.0f"
				case "Int" : return "0"
				default : return "null"
			}
		}
		switch(feature.EType.name) {
			case "float" : return '''«value»f'''
			case "Float" : return '''«value»f'''
			case "EFloat" : return '''«value»f'''
		}

		
		if(value instanceof EEnumLiteral)
			return TemplateUtil.getFQName(value.EEnum.EPackage) + "." + value.EEnum.name + "." + TemplateUtil.getLiteral(value)
			
		if(feature.EType.equals(EcorePackage.Literals.EDATE)) {
			return '''(java.util.Date) EcoreFactory.eINSTANCE.createFromString(EcorePackage.eINSTANCE.getEDate(), "«feature.defaultValueLiteral»")'''
		}
		
		if(feature.EType == EcorePackage.Literals.ESTRING)	
			return '''"«value»"'''
			
		return value;
	}
	
	def getSetterMethod(EClass eClass, EStructuralFeature feature, String FQPackagePath, String packageClassName, boolean inverse) {
		if(feature instanceof EAttribute) {
			return '''
	        Object oldValue = this.«TemplateUtil.getValidName(feature.name)»;
	        this.«TemplateUtil.getValidName(feature.name)» = value;

        	sendNotification(SmartEMFNotification.createSetNotification(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)», oldValue, value, -1));'''
		}
		if(feature instanceof EReference) {
			if(feature.isMany)
					return '''throw new UnsupportedOperationException("Set methods for SmartEMF collections are not supported.");'''
			
			return '''
			
			Object oldValue = this.«TemplateUtil.getValidName(feature.name)»;
			
			if(value == null && oldValue == null)
				return;
				
			if(value != null && value.equals(oldValue))
				return;
				
			
			«IF feature.containment»
			Resource.Internal resource = (Resource.Internal) eResource();
	        if(oldValue != null && value != null) {
	        	setResourceWithoutChecks(null);
	        }
	        
	        NotifyStatus status = NotifyStatus.SUCCESS_NO_NOTIFICATION;
			if(oldValue != null) {
        		status = ((MinimalSObjectContainer) oldValue).resetContainment();
			}	
			«ENDIF»

	        this.«TemplateUtil.getValidName(feature.name)» = value;

	        «IF feature.isMany && !"EFeatureMapEntry".equals(feature.EType.name) && !feature.EType.name.contains("MapEntry")»
	        
			if(value instanceof «TemplateUtil.getListTypeName(feature)»){
	        	this.«TemplateUtil.getValidName(feature.name)» = («TemplateUtil.getFieldTypeName(feature)») value;
			} else {
			    throw new IllegalArgumentException();
			}
	        «ENDIF»
	        «IF "EFeatureMapEntry".equals(feature.EType.name) || feature.EType.name.contains("MapEntry")»
	        this.«TemplateUtil.getValidName(feature.name)» = («TemplateUtil.getFieldTypeName(feature)») value;
	        «ENDIF»

			«IF feature.containment»
			if(value != null)
				status = ((MinimalSObjectContainer) this.«TemplateUtil.getValidName(feature.name)»).setContainment(this, «TemplateUtil.getPackageClassName(feature)».Literals.«TemplateUtil.getLiteral(feature)»);
			
		 	if(oldValue != null && value != null) {
	        	setResourceWithoutChecks(resource);
	        }
			
			if(status == NotifyStatus.SUCCESS_NO_NOTIFICATION || oldValue != null && value != null)
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
        	'''
		}
	}
}