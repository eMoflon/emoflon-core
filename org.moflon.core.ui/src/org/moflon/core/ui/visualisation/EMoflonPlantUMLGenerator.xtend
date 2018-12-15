package org.moflon.core.ui.visualisation

import java.util.Collection
import java.util.Map
import java.util.Optional
import org.apache.commons.lang3.StringUtils
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.moflon.core.ui.visualisation.diagrams.Diagram
import org.moflon.core.ui.visualisation.diagrams.EdgeType
import org.moflon.core.ui.visualisation.diagrams.VisualEdge
import org.moflon.core.ui.visualisation.metamodels.ClassDiagram
import org.moflon.core.ui.visualisation.models.ObjectDiagram

class EMoflonPlantUMLGenerator {
	static final String REPL_STR = "…";
	static final int REPL_LEN = 11;
	
	static Map<EObject, String> instanceNames;
	
	static def String wrapInTags(String body){
		'''
			@startuml
			«body»
			@enduml
		'''
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
		«plantUMLPreamble(diagram)»
		«FOR c : diagram.getSelection»
			«IF(c.abstract)»abstract «ENDIF»class «identifierForClass(c, diagram)» as «identifierForClass(c)»
			«visualiseEcoreClassAttributes(c, diagram)»
			«visualiseEcoreClassOperations(c, diagram)»
		«ENDFOR»
		«FOR c : diagram.getNeighbourhood»
			«IF(c.abstract)»abstract «ENDIF»class «identifierForClass(c, diagram)» as «identifierForClass(c)»
			«visualiseEcoreClassAttributes(c, diagram)»
			«visualiseEcoreClassOperations(c, diagram)»
		«ENDFOR»
		«visualiseEdges(diagram.getEdges, diagram)»
		«IF diagram.getShowDocumentation()»
			«visualiseDocumentation(diagram.getDoumentation)»
		«ENDIF»
		'''
	}
	
	def private static visualiseDocumentation(Map<EAnnotation, Optional<EClass>> map) {
		'''
		«FOR a : map.keySet»
			«var d = a.details.get("documentation")»
			«IF map.get(a).isPresent»
				note "«identifierForAnnotation(d)»" as «aliasForDoc(a, d)»
			«ELSEIF a.EModelElement instanceof EPackage»
				center footer «fill(d)»
			«ENDIF»
		«ENDFOR»
		«FOR a : map.keySet»
			«var optC = map.get(a)»
			«IF optC.isPresent»
				«var d = a.details.get("documentation")»
				«aliasForDoc(a, d)» .. «identifierForClass(optC.get)»
			«ENDIF»
		«ENDFOR»
		'''
	}
	
	def static fill(String s) {
		val words = StringUtils.split(s)
		var filledString = ""
		for (var i = 0; i < words.length; i += REPL_LEN) {
			for(var j = i; j < Math.min(words.length, i + REPL_LEN); j++){
				filledString = filledString + " " + words.get(j)
			}
			filledString += "\\n"
		}
		
		return filledString.trim
	}
	
	def private static aliasForDoc(EAnnotation a, String doc) {
		'''«IF packageNameFor(a) !== ""»«packageNameFor(a)».«ENDIF»«doc.replaceAll("[\\W]", "_")»«a.hashCode()»'''
	}
	
	def private static identifierForAnnotation(String d) {
		'''«d.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "'")»'''
	}
	
	def static String visualiseModelElements(ObjectDiagram diagram){
		instanceNames = diagram.geteObjectsToNames;
		
		'''
		«plantUMLPreamble(diagram)»
		«FOR o : diagram.getSelection»
		object «identifierForObject(o, diagram)» as «identifierForObject(o)» {
			«visualiseAllAttributes(o, diagram)»
		}
		«ENDFOR»
		«FOR o : diagram.getNeighbourhood»
			object «identifierForObject(o, diagram)» as «identifierForObject(o)» {
			«visualiseAllAttributes(o, diagram)»
		}
		«ENDFOR»
		«visualiseEdges(diagram.getEdges, diagram)»
		'''
	}
	
	def private static String visualiseEdges(Collection<VisualEdge> edges, Diagram<?> diagram) {
		'''
		«FOR edge: edges»
			«IF(edge.edgeType == EdgeType.REFERENCE)»
				«var EReference ref = edge.type»
				«var EClass src = ref.EContainingClass»
				«var EClass trg = ref.EReferenceType»
				«IF(!edge.hasEOpposite)»
					«identifierForClass(src)»«IF ref.isContainment» *«ENDIF»--> "«multiplicityFor(ref)»" «identifierForClass(trg)» : "«nameFor(ref, diagram)»"
				«ELSE»
					«identifierForClass(src)»"«nameFor(ref.EOpposite, diagram)» «multiplicityFor(ref.EOpposite)»" «IF ref.isContainment»*«ELSE»<«ENDIF»--«IF ref.EOpposite.isContainment»*«ELSE»>«ENDIF» "«nameFor(ref, diagram)» «multiplicityFor(ref)»" «identifierForClass(trg)»
				«ENDIF»
			«ELSEIF(edge.edgeType == EdgeType.GENERALISATION)»
				«identifierForClass(edge.trg as EClass)» <|-- «identifierForClass(edge.src as EClass)»
			«ELSEIF(edge.edgeType == EdgeType.LINK)»
				«IF(!edge.hasEOpposite)»
					«identifierForObject(edge.src)» --> «identifierForObject(edge.trg)» : "«IF diagram.abbreviateLabels»«abbr(edge.name)»«ELSE»«edge.name»«ENDIF»"
				«ELSE»
					«identifierForObject(edge.src)» "«IF diagram.abbreviateLabels»«abbr(edge.oppositeName)»«ELSE»«edge.oppositeName»«ENDIF»" <--> "«IF diagram.abbreviateLabels»«abbr(edge.name)»«ELSE»«edge.name»«ENDIF»" «identifierForObject(edge.trg)»
				«ENDIF»
			«ENDIF»
		«ENDFOR»
		'''
	}
	
	private def static multiplicityFor(EReference r) {
		'''«IF r.lowerBound == -1»*«ELSE»«r.lowerBound»«ENDIF»..«IF r.upperBound == -1»*«ELSE»«r.upperBound»«ENDIF»'''
	}
	
	private def static String identifierForClass(EClass c, ClassDiagram diagram)
		'''"«nameFor(c, diagram)»"'''
		
	private def static String identifierForClass(EClass c)
		'''«nameFor(c.EPackage)».«nameFor(c)»'''
	
	private def static String visualiseEcoreClassAttributes(EClass eclass, ClassDiagram diagram) {
		'''
		«FOR a : eclass.EAllAttributes»
			«identifierForClass(eclass)» : «nameFor(a, diagram)» : «nameFor(a.EType, diagram)»
		«ENDFOR»
		'''
	}
	
	private def static String visualiseEcoreClassOperations(EClass eclass, ClassDiagram diagram) {
		'''
		«FOR op : eclass.EAllOperations» 
			«identifierForClass(eclass)» : «visualiseEcoreOperation(op, diagram)»
		«ENDFOR»
		'''
	}
	
	private def static String visualiseEcoreOperation(EOperation op, ClassDiagram diagram) {
		'''«nameFor(op, diagram)»«visualiseEcoreOperationParameterList(op, diagram)»«IF(op.EType !== null)» : «nameFor(op.EType, diagram)»«ENDIF»'''
	}
	
	private def static String visualiseEcoreOperationParameterList(EOperation op, ClassDiagram diagram) {
		'''«IF op.EParameters.size == 0»()«ENDIF»«FOR param : op.EParameters BEFORE '(' SEPARATOR ', ' AFTER ')'»«nameFor(param, diagram)» : «nameFor(param.EType, diagram)»«ENDFOR»'''
	}
	
	def static visualiseAllAttributes(EObject o, Diagram<?> diagram) {
		'''
		«FOR a : o.eClass.EAllAttributes»
			«nameFor(a, diagram)» = «IF o.eGet(a) !== null && diagram.abbreviateLabels»«abbr(o.eGet(a).toString)»«ELSE»«o.eGet(a)»«ENDIF»
		«ENDFOR»
		'''
	}
	
	def static visualiseAllAttributes(EObject o) {
		'''
		«FOR a : o.eClass.EAllAttributes»
			«a.name» = «o.eGet(a)»
		«ENDFOR»
		'''
	}
	
	private def static Object identifierForObject(EObject o, ObjectDiagram diagram){
		'''"«IF diagram.abbreviateLabels»«abbr(instanceNames.get(o))»«ELSE»«instanceNames.get(o)»«ENDIF» : «nameFor(o.eClass, diagram)»"'''	
	}
	
	private def static Object identifierForObject(EObject o){
		'''«instanceNames.get(o)».«nameFor(o.eClass)»'''	
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
	
	def static CharSequence plantUMLPreamble(Diagram<?> diagram){
		'''
			hide «IF(diagram.showFullModelDetails)»empty «ENDIF»members
			
			skinparam shadowing false
			skinparam StereotypeABackgroundColor White
			skinparam StereotypeCBackgroundColor White
			
			skinparam class {
				BorderColor Black
				BackgroundColor White
				ArrowColor Black
				StereotypeABackgroundColor White
				StereotypeCBackgroundColor White
			}
			
			skinparam package {
				BackgroundColor GhostWhite
				BorderColor LightSlateGray
				Fontcolor LightSlateGray
			}
			
			skinparam object {
				BorderColor Black
				BackgroundColor White
				ArrowColor Black
			}
			
			skinparam note {
				BorderColor Black
				BackgroundColor White
				ArrowColor Black
			}
		'''
	}
	
	def private static String nameFor(ENamedElement elem, Diagram<?> diagram) {
		'''«IF diagram.abbreviateLabels»«abbr(elem.name)»«ELSE»«elem.name»«ENDIF»'''
	}
	
	def private static String nameFor(ENamedElement elem) {
		'''«elem.name»'''
	}
	
	def private static String packageNameFor(EObject obj) {
		'''«IF obj instanceof EPackage»«nameFor(obj as EPackage)»«ELSE»«IF obj.eContainer !== null»«packageNameFor(obj.eContainer)»«ENDIF»«ENDIF»'''
	}
	
	def private static String abbr(String longString) {
		'''«StringUtils.abbreviateMiddle(longString, REPL_STR, REPL_LEN)»'''
	}
}
