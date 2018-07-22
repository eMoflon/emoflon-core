package org.moflon.core.ui.visualisation

import java.util.Collection
import java.util.HashMap
import org.apache.commons.lang3.StringUtils
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference

class EMoflonPlantUMLGenerator {
	static var idMap = new HashMap<EObject, String>();
	
	static def String wrapInTags(String body){
		'''
			@startuml
			«body»
			@enduml
		'''
	}
	
	private def static Object identifierForObject(EObject o, char separator){
		if(!idMap.containsKey(o))
			idMap.put(o, '''o«idMap.keySet.size + 1»''')
			
		'''«idMap.get(o)»«separator»«o.eClass.name»'''	
	}
	
	static def String emptyDiagram(){
		'''
			title Choose an element that can be visualised
		'''
	}
	
	static def String errorDiagram(){
		'''
			title I'm having problems visualising the current selection (check your console).
		'''
	}

	static def String toBigDiagram(){
		'''
			title This diagram would be so big, trying to render it would fry your Eclipse instance
		'''
	}

	def static String visualiseEcoreElements(Collection<EClass> eclasses, Collection<VisualEdge> refs){
		'''
		«FOR c : eclasses»
			«IF(c.abstract)»abstract «ENDIF»class «identifierForClass(c)»
			«FOR s : c.ESuperTypes»
			«IF(s.abstract)»abstract «ENDIF»class «identifierForClass(s)»
			«identifierForClass(s)»<|--«identifierForClass(c)»
			«ENDFOR»
		«ENDFOR»
		«FOR ref : refs»
			«var EReference r = ref.getType()»
			«var EClass src = r.EContainingClass»
			«var EClass trg = r.EReferenceType»
			«IF(!ref.hasEOpposite)»
				«identifierForClass(src)»«IF r.isContainment» *«ENDIF»--> "«multiplicityFor(r)»" «identifierForClass(trg)» : "«r.name»"
			«ELSE»
				«identifierForClass(src)»"«r.EOpposite.name» «multiplicityFor(r.EOpposite)»" «IF r.isContainment»*«ELSE»<«ENDIF»--«IF r.EOpposite.isContainment»*«ELSE»>«ENDIF» "«r.name» «multiplicityFor(r)»" «identifierForClass(trg)»
			«ENDIF»
			«IF(trg.abstract)»abstract «ENDIF»class «identifierForClass(trg)»
		«ENDFOR»
		'''
	}
	
	def static String visualiseModelElements(Collection<EObject> objects, Collection<VisualEdge> links){
		idMap.clear
		
		'''
		«FOR o : objects»
		object «identifierForObject(o)»{
			«visualiseAllAttributes(o)»
		}
		«ENDFOR»
		«FOR l : links»
			«IF(!l.hasEOpposite)»
				«identifierForObject(l.src)» --> «identifierForObject(l.trg)» : "«l.name»"
			«ELSE»
				«identifierForObject(l.src)» "«l.oppositeName»" <-->  "«l.name»" «identifierForObject(l.trg)»
			«ENDIF»
		«ENDFOR»
		'''
	}
	
	private def static multiplicityFor(EReference r) {
		'''«IF r.lowerBound == -1»*«ELSE»«r.lowerBound»«ENDIF»..«IF r.upperBound == -1»*«ELSE»«r.upperBound»«ENDIF»'''
	}
	
	private def static String identifierForClass(EClass c)
		'''"«c.EPackage.name».«c.name»"'''
	
	def static visualiseAllAttributes(EObject o) {
		'''
		«FOR a : o.eClass.EAllAttributes»
			«a.name» = «o.eGet(a)»
		«ENDFOR»
		'''
	}
	
	private def static Object identifierForObject(EObject o){
		if(!idMap.containsKey(o))
			idMap.put(o, '''o«idMap.keySet.size + 1»''')
			
		'''«idMap.get(o)».«o.eClass.name»'''	
	}
	
	def static String visualiseCorrModel(Collection<EObject> corrObjects, Collection<EObject> sourceObjects, Collection<EObject> targetObjects, Collection<VisualEdge> links)
	{	
		idMap.clear
		'''
		«plantUMLPreamble»
		together {
		«FOR so : sourceObjects»
		class «identifierForObject(so,'_')» <<BLACK>> <<SRC>>{
			«visualiseAllAttributes(so)»
			}
		«ENDFOR»	
		}
		
		together {
		«FOR to : targetObjects»
		class «identifierForObject(to,'_')» <<BLACK>> <<TRG>>{
			«visualiseAllAttributes(to)»
			}
		«ENDFOR»
		}
				
		«var i = 0»
		«FOR o : corrObjects»		
			«identifierForObject(sourceObjects.get(i),'_')» <..> «identifierForObject(targetObjects.get(i++),'_')» : "«StringUtils.abbreviate(":" + o.eClass.name, 11)»"	
		«ENDFOR»
		
		«FOR l : links»
			«identifierForObject(l.src,'_')» --> «identifierForObject(l.trg,'_')» : "«l.name»"
		«ENDFOR»
		'''
	} 
	
	def static CharSequence plantUMLPreamble(){
		'''
			hide empty members
			hide circle
			hide stereotype
			
			skinparam shadowing false
			
			skinparam class {
				BorderColor<<GREEN>> SpringGreen
				BorderColor<<BLACK>> Black
				BorderColor<<KERN>> LightGray
				BackgroundColor<<TRG>> MistyRose
				BackgroundColor<<SRC>> LightYellow
				BackgroundColor<<CORR>> LightCyan 
				ArrowColor Black
			}	
		'''
	}
}
