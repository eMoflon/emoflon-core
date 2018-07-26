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

	def static String visualiseEcoreElements(ClassDiagram diagram){
		'''
		«FOR c : diagram.getSelection»
			«IF(c.abstract)»abstract «ENDIF»class «identifierForClass(c)»
		«ENDFOR»
		«FOR c : diagram.getNeighbourhood»
			«IF(c.abstract)»abstract «ENDIF»class «identifierForClass(c)»
		«ENDFOR»
		«visualiseEdges(diagram.getEdges)»
		'''
	}
	
	def static String visualiseModelElements(ObjectDiagram diagram){
		idMap.clear
		
		'''
		«FOR o : diagram.getSelection»
		object «identifierForObject(o)»{
			«visualiseAllAttributes(o)»
		}
		«ENDFOR»
		«FOR o : diagram.getNeighbourhood»
			object «identifierForObject(o)»{
			«visualiseAllAttributes(o)»
		}
		«ENDFOR»
		«visualiseEdges(diagram.getEdges)»
		'''
	}
	
	def private static String visualiseEdges(Collection<VisualEdge> edges) {
		'''
		«FOR edge: edges»
			«IF(edge.edgeType == EdgeType.REFERENCE)»
				«var EReference ref = edge.type»
				«var EClass src = ref.EContainingClass»
				«var EClass trg = ref.EReferenceType»
				«IF(!edge.hasEOpposite)»
					«identifierForClass(src)»«IF ref.isContainment» *«ENDIF»--> "«multiplicityFor(ref)»" «identifierForClass(trg)» : "«ref.name»"
				«ELSE»
					«identifierForClass(src)»"«ref.EOpposite.name» «multiplicityFor(ref.EOpposite)»" «IF ref.isContainment»*«ELSE»<«ENDIF»--«IF ref.EOpposite.isContainment»*«ELSE»>«ENDIF» "«ref.name» «multiplicityFor(ref)»" «identifierForClass(trg)»
				«ENDIF»
			«ELSEIF(edge.edgeType == EdgeType.GENERALISATION)»
				«identifierForClass(edge.trg as EClass)»<|--«identifierForClass(edge.src as EClass)»
			«ELSEIF(edge.edgeType == EdgeType.LINK)»
				«IF(!edge.hasEOpposite)»
					«identifierForObject(edge.src)» --> «identifierForObject(edge.trg)» : "«edge.name»"
				«ELSE»
					«identifierForObject(edge.src)» "«edge.oppositeName»" <-->  "«edge.name»" «identifierForObject(edge.trg)»
				«ENDIF»
			«ELSE»
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
