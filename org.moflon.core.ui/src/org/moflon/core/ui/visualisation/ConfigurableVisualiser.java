package org.moflon.core.ui.visualisation;

import org.moflon.core.ui.visualisation.Configurator.StrategyPart;
import org.moflon.core.ui.visualisation.strategy.DiagramStrategy;

/**
 * This interface defines the required method signatures for a visualiser (such
 * as all classes inheriting from {@link EMoflonVisualiser}) to be configurable.
 * 
 * A visualiser is configurable, if style bits can be set to influence the
 * PlantUML DSL code generation, and if a strategy can be set for the
 * computation of the diagram nodes and edges that are to be visualised.
 * Furthermore, a configurable visualiser is required to implement a check
 * method, such that a {@link Configurator} instance might check, whether or not
 * a specific diagram type is supported by this visualiser.
 * 
 * @author Johannes Brandt
 *
 * @param <T>
 *            The specific type for which a strategy can be applied. Usually
 *            this type encapsulates all the information necessary to describe a
 *            diagram that is to be visualised by this visualiser. Examples for
 *            such a type are {@link Diagram}, and all its inheriting classes,
 *            such as {@link ClassDiagram} and {@link ObjectDiagram}. However,
 *            the type parameter does not necessarily have to be or extend
 *            {@link Diagram}.
 */
public interface ConfigurableVisualiser<T> {

	/**
	 * Sets the style bits that are to be employed during the PlantUML diagram text
	 * generation.
	 * 
	 * Style bits define how a diagram is visualised, e.g. which elements of the
	 * diagram are shown or in which color. For the declaration of some general
	 * style bits see {@link EMoflonPlantUMLGenerator}.
	 * 
	 * @param style
	 *            The style bits.
	 */
	void setDiagramStyle(int style);

	/**
	 * Sets a strategy for processing diagrams of type <code>T</code>, i.e. the
	 * diagram type that is supported by this visualiser.
	 * 
	 * @param strategy
	 *            The strategy that is to be applied to diagrams handled by this
	 *            visualiser.
	 */
	void setDiagramStrategy(DiagramStrategy<T> strategy);

	/**
	 * This method is used by configurator units, such as {@link Configurator}. If
	 * the given {@link Class} instance encapsulates the diagram type supported by
	 * this visualiser, this method is expected to return <code>true</code>. This
	 * method is called by configurators, to ensure, that a specific strategy can be
	 * applied to this visualiser.
	 * 
	 * @param diagramClass
	 *            The {@link Class} instance resembling a diagram type, for which it
	 *            is to be checked, whether it is supported by this visualiser.
	 * @return <code>true</code>, if the given diagram class describes a diagram
	 *         type, which is supported by this visualiser, <code>false</code>
	 *         otherwise.
	 */
	boolean supportsDiagramType(Class<?> diagramClass);

	/**
	 * Provides a default strategy for every {@link StrategyPart}.
	 * 
	 * <p>
	 * <b>Note:</b> The default implementation of this methods returns
	 * {@link DiagramStrategy#identity()} for every {@link StrategyPart}.
	 * </p>
	 * 
	 * @param part
	 *            The strategy part, for which a partial default strategy is to be
	 *            returned.
	 * @return The default strategy for the given strategy part.
	 */
	default DiagramStrategy<T> getDefaultStrategy(StrategyPart part) {
		return DiagramStrategy.identity();
	}
}
