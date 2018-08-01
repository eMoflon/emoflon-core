package org.moflon.core.ui.visualisation

import java.util.Collection
import java.util.HashMap
import java.util.Map
import org.apache.commons.lang3.StringUtils
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EOperation
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EPackage
import java.util.Optional

class EMoflonPlantUMLGenerator {
	
	public static final int SHOW_MODEL_DETAILS = 1<<0;
	public static final int ABBR_LABELS = 1<<1;
	public static final int SHOW_DOCUMENTATION = 1<<2;
	
	private static final String REPL_STR = "…";
	private static final int REPL_LEN = 11;
	
	static var idMap = new HashMap<EObject, String>();
	private static Map<EObject, String> instanceNames;
	
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

	def static String visualiseEcoreElements(ClassDiagram diagram, int diagramStyle){
		'''
		«plantUMLPreamble(diagramStyle)»
		«FOR c : diagram.getSelection»
			«IF(c.abstract)»abstract «ENDIF»class «identifierForClass(c, diagramStyle)» as «identifierForClass(c)»
			«visualiseEcoreClassAttributes(c, diagramStyle)»
			«visualiseEcoreClassOperations(c, diagramStyle)»
		«ENDFOR»
		«FOR c : diagram.getNeighbourhood»
			«IF(c.abstract)»abstract «ENDIF»class «identifierForClass(c, diagramStyle)» as «identifierForClass(c)»
			«visualiseEcoreClassAttributes(c, diagramStyle)»
			«visualiseEcoreClassOperations(c, diagramStyle)»
		«ENDFOR»
		«visualiseEdges(diagram.getEdges, diagramStyle)»
		«IF diagramStyle.bitwiseAnd(SHOW_DOCUMENTATION) > 0»
			«visualiseDocumentation(diagram.getDoumentation)»
		«ENDIF»
		'''
	}
	
	def private static visualiseDocumentation(Map<EAnnotation, Optional<EClass>> map) {
		'''
		«FOR a : map.keySet»
			«var d = a.details.get("documentation")»
			note "«identifierForAnnotation(d)»" as «aliasForDoc(a, d)»
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
	
	def private static aliasForDoc(EAnnotation a, String doc) {
		'''«IF packageNameFor(a) !== ""»«packageNameFor(a)».«ENDIF»«doc.replaceAll("[\\W]", "_")»«a.hashCode()»'''
	}
	
	def private static identifierForAnnotation(String d) {
		'''«d.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "'")»'''
	}
	
	def static String visualiseModelElements(ObjectDiagram diagram, int diagramStyle){
		idMap.clear
		instanceNames = diagram.eObjectsToNames;
		
		'''
		«plantUMLPreamble(diagramStyle)»
		«FOR o : diagram.getSelection»
		object «identifierForObject(o, diagramStyle)» as «identifierForObject(o)» {
			«visualiseAllAttributes(o, diagramStyle)»
		}
		«ENDFOR»
		«FOR o : diagram.getNeighbourhood»
			object «identifierForObject(o, diagramStyle)» as «identifierForObject(o)» {
			«visualiseAllAttributes(o, diagramStyle)»
		}
		«ENDFOR»
		«visualiseEdges(diagram.getEdges, diagramStyle)»
		'''
	}
	
	def private static String visualiseEdges(Collection<VisualEdge> edges, int style) {
		'''
		«FOR edge: edges»
			«IF(edge.edgeType == EdgeType.REFERENCE)»
				«var EReference ref = edge.type»
				«var EClass src = ref.EContainingClass»
				«var EClass trg = ref.EReferenceType»
				«IF(!edge.hasEOpposite)»
					«identifierForClass(src)»«IF ref.isContainment» *«ENDIF»--> "«multiplicityFor(ref)»" «identifierForClass(trg)» : "«nameFor(ref, style)»"
				«ELSE»
					«identifierForClass(src)»"«nameFor(ref.EOpposite, style)» «multiplicityFor(ref.EOpposite)»" «IF ref.isContainment»*«ELSE»<«ENDIF»--«IF ref.EOpposite.isContainment»*«ELSE»>«ENDIF» "«nameFor(ref, style)» «multiplicityFor(ref)»" «identifierForClass(trg)»
				«ENDIF»
			«ELSEIF(edge.edgeType == EdgeType.GENERALISATION)»
				«identifierForClass(edge.trg as EClass)»<|--«identifierForClass(edge.src as EClass)»
			«ELSEIF(edge.edgeType == EdgeType.LINK)»
				«IF(!edge.hasEOpposite)»
					«identifierForObject(edge.src)» --> «identifierForObject(edge.trg)» : "«IF style.bitwiseAnd(ABBR_LABELS) > 0»«abbr(edge.name)»«ELSE»«edge.name»«ENDIF»"
				«ELSE»
					«identifierForObject(edge.src)» "«IF style.bitwiseAnd(ABBR_LABELS) > 0»«abbr(edge.oppositeName)»«ELSE»«edge.oppositeName»«ENDIF»" <--> "«IF style.bitwiseAnd(ABBR_LABELS) > 0»«abbr(edge.name)»«ELSE»«edge.name»«ENDIF»" «identifierForObject(edge.trg)»
				«ENDIF»
			«ENDIF»
		«ENDFOR»
		'''
	}
	
	private def static multiplicityFor(EReference r) {
		'''«IF r.lowerBound == -1»*«ELSE»«r.lowerBound»«ENDIF»..«IF r.upperBound == -1»*«ELSE»«r.upperBound»«ENDIF»'''
	}
	
	private def static String identifierForClass(EClass c, int style)
		'''"«nameFor(c, style)»"'''
		
	private def static String identifierForClass(EClass c)
		'''«nameFor(c.EPackage)».«nameFor(c)»'''
	
	private def static String visualiseEcoreClassAttributes(EClass eclass, int style) {
		'''
		«FOR a : eclass.EAllAttributes»
			«identifierForClass(eclass)» : «nameFor(a, style)» : «nameFor(a.EType, style)»
		«ENDFOR»
		'''
	}
	
	private def static String visualiseEcoreClassOperations(EClass eclass, int style) {
		'''
		«FOR op : eclass.EAllOperations» 
			«identifierForClass(eclass)» : «visualiseEcoreOperation(op, style)»
		«ENDFOR»
		'''
	}
	
	private def static String visualiseEcoreOperation(EOperation op, int style) {
		'''«nameFor(op, style)»«visualiseEcoreOperationParameterList(op, style)»«IF(op.EType !== null)» : «nameFor(op.EType, style)»«ENDIF»'''
	}
	
	private def static String visualiseEcoreOperationParameterList(EOperation op, int style) {
		'''«IF op.EParameters.size == 0»()«ENDIF»«FOR param : op.EParameters BEFORE '(' SEPARATOR ', ' AFTER ')'»«nameFor(param, style)» : «nameFor(param.EType, style)»«ENDFOR»'''
	}
	
	def static visualiseAllAttributes(EObject o, int style) {
		'''
		«FOR a : o.eClass.EAllAttributes»
			«nameFor(a, style)» = «IF o.eGet(a) !== null && style.bitwiseAnd(ABBR_LABELS) > 0»«abbr(o.eGet(a).toString)»«ELSE»«o.eGet(a)»«ENDIF»
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
	
	private def static Object identifierForObject(EObject o, int style){
		'''"«IF style.bitwiseAnd(ABBR_LABELS) > 0»«abbr(instanceNames.get(o))»«ELSE»«instanceNames.get(o)»«ENDIF» : «nameFor(o.eClass, style)»"'''	
	}
	
	private def static Object identifierForObject(EObject o){
		'''«instanceNames.get(o)».«nameFor(o.eClass)»'''	
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
	
	def static CharSequence plantUMLPreamble(int style){
		'''
			hide «IF(style.bitwiseAnd(SHOW_MODEL_DETAILS) > 0)»empty «ENDIF»members
			
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
	
	def private static String nameFor(ENamedElement elem, int style) {
		'''«IF style.bitwiseAnd(ABBR_LABELS) > 0»«abbr(elem.name)»«ELSE»«elem.name»«ENDIF»'''
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
