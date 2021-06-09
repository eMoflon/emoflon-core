package emfcodegenerator.creators.util

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

class SmartEMFObjectCreator {
	
	var EClass eClass = null
	
	new(EClass eClass) {
		this.eClass = eClass
	}
	
	def createCode() {
		val className = eClass.name
		val ePackage = eClass.EPackage
		val packageClassName = ePackage.name.toFirstUpper + "Package"
		val FQPackagePath = getFQName(ePackage)
		
		return '''
		package «FQPackagePath».impl;
		
		«FOR packages : getImportPackages()»
		import «getFQName(packages)».«packages.name.toFirstUpper»Package;
		«ENDFOR»
		
		«FOR featureType : getImportTypes()»
		import «getFQName(featureType)».«featureType.name»;
		«ENDFOR»
		import emfcodegenerator.notification.SmartEMFNotification;
		import emfcodegenerator.util.SmartObject;
		import emfcodegenerator.util.collections.LinkedESet;
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.emf.ecore.EStructuralFeature;
		
		public class «className»Impl extends SmartObject implements «className» {
		
		    «FOR feature : eClass.EAllStructuralFeatures»
		    «IF feature.isMany»
		    protected «getFieldTypeName(feature)» «getValidName(feature.name)» = new «getFieldTypeName(feature)»(this, «getPackageClassName(feature)».Literals.«getLiteral(feature)»);
			«ELSE»
		    protected «getFieldTypeName(feature)» «getValidName(feature.name)»;
			«ENDIF»
			«ENDFOR»
			
			protected «eClass.name»Impl() {
				super(«packageClassName».Literals.«getLiteral(eClass)»);
			}
			
		    «FOR feature : eClass.EAllStructuralFeatures»
		    @Override
		    public «getFieldTypeName(feature)» «getOrIs(feature)»«feature.name.toFirstUpper»() {
		    	return «getValidName(feature.name)»;
		    }
		    
«««		    @Override
		    public void set«feature.name.toFirstUpper»(«getFieldTypeName(feature)» value) {
		    	«getSetterMethod(eClass, feature, FQPackagePath, packageClassName)»
		    }
		    «ENDFOR»
		
		    @Override
		    public boolean eIsSet(int featureID){
		        switch(featureID) {
		        }
		        return super.eIsSet(featureID);
		    }
		
		    @Override
		    public void eSet(EStructuralFeature eFeature, Object newValue){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («FQPackagePath».«getPackageClassName(feature)».Literals.«getLiteral(feature)».equals(eFeature)) {
		    	 	set«feature.name.toFirstUpper»((«getFieldTypeName(feature)») newValue); 
		    	 	return;
		    	 }
		    	«ENDFOR»
		        super.eSet(eFeature, newValue);
		    }
		
		    @Override
		    public void eSet(int featureID, Object newValue){
		        switch(featureID) {
		    		«FOR feature : eClass.EAllStructuralFeatures»
		    		case «getPackageClassName(feature)».«getLiteralID(feature)»:
		    			set«feature.name.toFirstUpper»((«getFieldTypeName(feature)») newValue); 
		    			return;
	        		«ENDFOR»
		        }
		        super.eSet(featureID, newValue);
		    }
		
		    @Override
		    public String toString(){
		        StringBuilder result = new StringBuilder(super.toString() + "(name: «className») ");
		        result.append(" (");
	    		«FOR feature : eClass.EAllStructuralFeatures SEPARATOR '''
	    		result.append(", ");
	    		'''»
				result.append("«feature.name»:");
	    		result.append(SmartObject.toStringIfNotNull(«getValidName(feature.name)»));
		        «ENDFOR»
		        return result.toString();
		    }
		
		 	@Override
		    public Object eGet(EStructuralFeature eFeature){
		    	«FOR feature : eClass.EAllStructuralFeatures»
		    	 if («getPackageClassName(feature)».Literals.«getLiteral(feature)».equals(eFeature))
		    	 	return «getOrIs(feature)»«feature.name.toFirstUpper»();
		    	«ENDFOR»
		        return super.eGet(eFeature);
		    }
		
		    @Override
		    public Object eGet(int featureID, boolean resolve, boolean coreType){
		        switch(featureID) {
	    			«FOR feature : eClass.EAllStructuralFeatures»
	    			case «getPackageClassName(feature)».«getLiteralID(feature)»:
	    				return «getOrIs(feature)»«feature.name.toFirstUpper»();
					«ENDFOR»
		        }
		        return super.eGet(featureID, resolve, coreType);
		    }
		
		    @Override
		    public void eUnset(int featureID){
		        switch(featureID) {
		        }
		        super.eUnset(featureID);
		    }
		
		}
		
		
		'''
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
	        Object oldValue = «getValidName(feature.name)»;
	        «getValidName(feature.name)» = value;

	        if (eNotificationRequired()) 
	        	eNotify(SmartEMFNotification.set(this, «getPackageClassName(feature)».Literals.«getLiteral(feature)», oldValue, value, -1));'''
		}
		if(feature instanceof EReference) {
			return '''
			Object oldValue = «getValidName(feature.name)»;
	        «IF feature.containment»
	        if(«getValidName(feature.name)» != null) 
				((emfcodegenerator.util.MinimalSObjectContainer) «getValidName(feature.name)»).reset_containment();
	        «ENDIF»
	        «getValidName(feature.name)» = value;
			
	        «IF feature.isMany»
	        if(value instanceof «getListTypeName(feature)»){
	        	«getValidName(feature.name)» = («getFieldTypeName(feature)») value;
	        } else {
	        	throw new IllegalArgumentException();
	        }
	        «ENDIF»

			«IF feature.containment»
			((emfcodegenerator.util.MinimalSObjectContainer) «getValidName(feature.name)»).set_containment(
				this,
	            «getPackageClassName(feature)».Literals.«getLiteral(feature)»
			);
			«ENDIF»

			if (eNotificationRequired()) 
				eNotify(SmartEMFNotification.set(this, «packageClassName».Literals.«getLiteral(feature)», oldValue, value, -1));'''
		}
	}
	
	def getListTypeName(EStructuralFeature feature) {
		val isOrdered = feature.ordered
		val isUnique = feature.unique
		if(isOrdered && isUnique)
			return '''LinkedESet'''
		if(isUnique)
			return '''HashESet'''
		return '''LinkedEList'''
	}
	
	def getFieldTypeName(EStructuralFeature feature) {
		if(!feature.isMany) {
			switch feature.EType.name {
				case "EString" : return "String"
				case "EInt" : return "int"
				case "EBoolean" : return "boolean"
				case "EChar" : return "char"
				case "EDouble" : return "double"
			}
			return feature.EType.name
		}
		
		return '''«getListTypeName(feature)»<«feature.EType.name»>'''
	}
	
	static def splitNameAtUppercases(String name) {
		var splittedName = new LinkedList
		var from = 0
		for(var i=1; i<name.length; i++) {
			if(Character.isUpperCase(name.charAt(i))) {
				splittedName.add(name.substring(from, i))
				from = i;
			}
		}
		
		// add last segment
		splittedName.add(name.substring(from, name.length))
			
		return splittedName
	}
	
	// ids are always generated for the current eClass
	def getLiteralID(ENamedElement feature) {
		val containingClass = feature.eContainer as EClassifier
		if(feature instanceof EStructuralFeature)
			return '''«FOR part : splitNameAtUppercases(containingClass.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»__«FOR part : splitNameAtUppercases(feature.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»'''		
		else {
			return '''«FOR part : splitNameAtUppercases(feature.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»'''
		}
	}
	
	static def getLiteral(ENamedElement elt) {
		if(elt instanceof EStructuralFeature)
			return '''«FOR part : splitNameAtUppercases(elt.EContainingClass.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»__«FOR part : splitNameAtUppercases(elt.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»'''		
		else {
			return '''«FOR part : splitNameAtUppercases(elt.name) SEPARATOR "_"»«part.toUpperCase»«ENDFOR»'''
		}
	}
	
	def getFQName(EPackage ePackage) {
		var currentPackage = ePackage
		var FQPackagePath = currentPackage.name
		while(currentPackage.eContainer != null) {
			currentPackage = currentPackage.eContainer as EPackage
			FQPackagePath = currentPackage.name + "." + FQPackagePath
		}
		
		return FQPackagePath
	}
	
	def getFQName(EClassifier eClass) {
		return getFQName(eClass.EPackage)
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
	
	def getPackageClassName(EPackage pkg) {
		return pkg.name.toFirstUpper + "Package"
	}
	
	def getPackageClassName(EClassifier c) {
		return getPackageClassName(c.EPackage)
	}
	
	def getPackageClassName(EStructuralFeature f) {
		return getPackageClassName(f.eContainer as EClassifier)
	}
	
	def getValidName(String name) {
		val keywords = newLinkedList("package", "class", "public", "private", "protected", "int", "double", "char", "boolean", "import")
		if(keywords.contains(name)) {
			return "__" + name
		}
		else
			return name
	}
}