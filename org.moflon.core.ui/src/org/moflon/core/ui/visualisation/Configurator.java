package org.moflon.core.ui.visualisation;

import java.util.HashMap;
import org.moflon.core.ui.visualisation.strategy.DiagramStrategy;

/**
 * Configures {@link EMoflonVisualiser}s with regard to their diagram processing
 * strategy (manipulation of nodes and edges) and diagram style (PlantUML code
 * generation).
 * 
 * For an {@link EMoflonVisualiser} to be configured by this class, it needs to
 * be implement the {@link ConfigurableVisualiser} interface. Then, the diagram
 * style is set for all {@link ConfigurableVisualiser} instances, if it is
 * changed via {@link #setDiagramStyle(int, boolean)}. The diagram processing
 * strategy (= diagram strategy) can only be applied to visualisers, which
 * support a certain diagram type (see
 * {@link #setDiagramStrategy(Class, StrategyPart, DiagramStrategy)}. A complete
 * diagram strategy consists of multiple partial strategies. Each part of a
 * complete diagram strategy is identified by a value of {@link StrategyPart}.
 * If one partial strategy is changed via
 * {@link #setDiagramStrategy(Class, StrategyPart, DiagramStrategy)}, the
 * complete diagram strategy is recomposed and applied to all visualisers, which
 * support the diagram type associated with the strategy.
 * 
 * <p>
 * <b>Note:</b> This class is singleton.
 * </p>
 * 
 * @author Johannes Brandt
 *
 */
public class Configurator {

	/**
	 * Used to identify a specific part of a strategy.
	 * 
	 * A strategy, as implied by this enum, consists of two parts, initialization
	 * and neighbourhood calculation. The first is associated with
	 * {@link StrategyPart#INIT}, the latter with
	 * {@link StrategyPart#NEIGHBOURHOOD}. Therefore, this enum enables e.g. command
	 * handlers to call
	 * {@link Configurator#setDiagramStrategy(Class, StrategyPart, DiagramStrategy)},
	 * and precisely state which part of the strategy they wish to change.
	 * 
	 * @author Johannes Brandt
	 *
	 */
	public static enum StrategyPart {
		// TODO: Improve mechanism to support the isolation of strategy parts, which are
		// defined elsewhere, i.e. make the configurator "unaware" of the specific
		// strategy parts.
		INIT, NEIGHBOURHOOD;
	}

	/**
	 * The monitor used to synchronize access to visualiser style or strategy.
	 */
	private static final Object MONITOR = new Object();

	/**
	 * Stores all {@link EMoflonVisualiser} instances registered to this Eclipse
	 * Platform.
	 */
	private HashMap<Class<? extends EMoflonVisualiser>, EMoflonVisualiser> visualisers;

	/**
	 * Style bits for the {@link EMoflonVisualiser}s.
	 */
	private int style = 1;

	/**
	 * Stores the strategy for the part {@link StrategyPart#INIT} for each diagram
	 * type.
	 */
	private HashMap<Class<?>, DiagramStrategy<?>> diagramTypeToInitStrategy;

	/**
	 * Stores the strategy for the part {@link StrategyPart#NEIGHBOURHOOD} for each
	 * diagram type.
	 */
	private HashMap<Class<?>, DiagramStrategy<?>> diagramTypeToNeighbourhoodStrategy;

	/**
	 * Provides the {@link Configurator} singleton instance.
	 */
	private static class InstanceHolder {
		private static final Configurator INSTANCE = new Configurator();
	}

	/**
	 * Private constructor to prevent instance creation.
	 */
	private Configurator() {
		visualisers = new HashMap<>();
		diagramTypeToInitStrategy = new HashMap<>();
		diagramTypeToNeighbourhoodStrategy = new HashMap<>();
	}

	/**
	 * Provides thread-safe singleton access.
	 * 
	 * @return The singleton instance for {@link Configurator}.
	 */
	public static Configurator getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * Registers a visualiser for configuration.
	 * 
	 * Any {@link EMoflonVisualiser} can be registered with this configurator, such
	 * that diagram style and strategy configuration can be passed that visualiser.
	 * 
	 * <p>
	 * <b>Note:</b> For an {@link EMoflonVisualiser} to be actually handled in terms
	 * of diagram style and diagram strategy configuration, it needs to implement
	 * the {@link ConfigurableVisualiser} interface.
	 * </p>
	 * 
	 * @param eMoflonVisualiser
	 *            The visualiser that is to be configured by this configurator.
	 */
	public void registerVisualiser(EMoflonVisualiser eMoflonVisualiser) {
		synchronized (MONITOR) {
			visualisers.put(eMoflonVisualiser.getClass(), eMoflonVisualiser);
		}
	}

	/**
	 * Sets the given style bits in the current diagram style and applies them to
	 * all registered visualisers.
	 * 
	 * @param styleBits
	 *            The style bits that are to be set or unset.
	 * @param doSet
	 *            If <code>true</code>, the 1-bits in styleBits are set in the
	 *            diagram style, otherwise the 1-bits in styleBits are unset in the
	 *            diagram style.
	 */
	public void setDiagramStyle(int styleBits, boolean doSet) {
		synchronized (MONITOR) {
			if (doSet) {
				style |= styleBits;
			} else {
				style &= ~styleBits;
			}
			applyStyle(style);
		}
	}

	/**
	 * Sets the given strategy for the specified strategy part and diagram class,
	 * composes the full strategy, and applies it to all registered visualisers,
	 * that support the given diagram clas.
	 * 
	 * @param diagramClass
	 *            The diagram class for which this partial strategy is to be set.
	 * @param part
	 *            Identifies the part of a full strategy that is to be set.
	 * @param strategy
	 *            The partial strategy, that is to be set and applied to all
	 *            registered and supported visualisers.
	 */
	@SuppressWarnings("unchecked")
	public <T> void setDiagramStrategy(Class<? extends T> diagramClass, StrategyPart part,
			DiagramStrategy<T> strategy) {
		if (diagramClass == null || part == null || strategy == null) {
			return;
		}

		synchronized (MONITOR) {
			switch (part) {
			case INIT:
				if (!diagramTypeToNeighbourhoodStrategy.containsKey(diagramClass)) {
					diagramTypeToNeighbourhoodStrategy.put(diagramClass,
							getDefaultStrategy(diagramClass, StrategyPart.NEIGHBOURHOOD));
				}
				diagramTypeToInitStrategy.put(diagramClass, strategy);
				break;
			case NEIGHBOURHOOD:
				if (!diagramTypeToInitStrategy.containsKey(diagramClass)) {
					diagramTypeToInitStrategy.put(diagramClass, getDefaultStrategy(diagramClass, StrategyPart.INIT));
				}
				diagramTypeToNeighbourhoodStrategy.put(diagramClass, strategy);
				break;
			default:
				return;
			}

			DiagramStrategy<T> fullStrategy = (DiagramStrategy<T>) diagramTypeToInitStrategy.get(diagramClass);
			fullStrategy = fullStrategy
					.andThen((DiagramStrategy<T>) diagramTypeToNeighbourhoodStrategy.get(diagramClass));
			applyStrategy(diagramClass, fullStrategy);
		}
	}

	/**
	 * Applies the given diagram style to all registered {@link EMoflonVisualiser}s.
	 * 
	 * @param style
	 *            The style that is to be applied.
	 */
	private void applyStyle(int style) {
		synchronized (MONITOR) {
			visualisers.values().stream()//
					.filter(ConfigurableVisualiser.class::isInstance)//
					.map(ConfigurableVisualiser.class::cast)//
					.forEach(configurable -> configurable.setDiagramStyle(style));
		}
	}

	/**
	 * Used to determine the default partial strategies for all registered
	 * {@link EMoflonVisualiser}s, which support a specified diagram class.
	 * 
	 * @param diagramClass
	 *            The diagram class that needs to be supported by at least one of
	 *            the visualisers.
	 * @param part
	 *            The identifier for the part of a strategy, for which a default
	 *            implementation is wanted.
	 * @return The default implementation for the partial strategy, which supports
	 *         the processing of the specified diagram class.
	 */
	@SuppressWarnings("unchecked")
	private <T> DiagramStrategy<T> getDefaultStrategy(Class<? extends T> diagramClass, StrategyPart part) {
		// TODO: Improve mechanism. Only one visualiser gets to determine the default
		// strategy. If there are multiple visualisers supporting the same diagram type,
		// then this implementation returns the first. Any other is not taken into
		// consideration.
		return visualisers.values().stream()//
				.filter(ConfigurableVisualiser.class::isInstance)//
				.map(ConfigurableVisualiser.class::cast)//
				.filter(cVis -> cVis.supportsDiagramType(diagramClass))//
				.map(cVis -> cVis.getDefaultStrategy(part))//
				.findFirst()//
				.orElse(DiagramStrategy.identity());
	}

	/**
	 * Applies the specified strategy to all visualisers that support the given
	 * diagram class.
	 * 
	 * @param diagramClass
	 *            Identifies the diagram type that is supported by the given
	 *            strategy.
	 * @param strategy
	 *            The strategy that is to be applied to all visualisers, which
	 *            support the specified diagram class.
	 */
	@SuppressWarnings("unchecked")
	private <T> void applyStrategy(Class<? extends T> diagramClass, DiagramStrategy<T> strategy) {
		synchronized (MONITOR) {
			visualisers.values().stream()//
					.filter(ConfigurableVisualiser.class::isInstance)//
					.map(ConfigurableVisualiser.class::cast)//
					.filter(cVis -> cVis.supportsDiagramType(diagramClass))//
					.forEach(cVis -> cVis.setDiagramStrategy(strategy));
		}
	}
}
