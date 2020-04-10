package org.moflon.core.ui.visualisation.diagrams;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class VisualEdge {
	private EReference type;
	private EdgeType edgeType;
	private EObject src;
	private EObject trg;
	private String name;
	
	public VisualEdge(EReference type, EdgeType edgeType, EObject src, EObject trg) {
		this.type = type;
		this.edgeType = edgeType;
		this.src = src;
		this.trg = trg;
	}
	
	public VisualEdge(EdgeType edgeType, EObject src, EObject trg, String name) {
		this.edgeType = edgeType;
		this.src = src;
		this.trg = trg;
		this.name = name;
	}

	public EReference getType() {
		return type;
	}

	public EdgeType getEdgeType() {
		return edgeType;
	}

	public EObject getSrc() {
		return src;
	}

	public EObject getTrg() {
		return trg;
	}

	public String getName() {
		if(name != null)
			return name;
					
		if (edgeType == EdgeType.GENERALISATION || type == null) {
			return "";
		}
		return type.getName();
	}

	public String getOppositeName() {
		assert (hasEOpposite());
		return type.getEOpposite().getName();
	}

	public boolean hasEOpposite() {
		if (edgeType == EdgeType.GENERALISATION || type == null) {
			return false;
		}
		return type.getEOpposite() != null;
	}

	public Optional<VisualEdge> findEOpposite(Collection<VisualEdge> links) {
		assert (hasEOpposite());
		VisualEdge opposite = new VisualEdge(type.getEOpposite(), edgeType, trg, src);
		if (links.contains(opposite))
			return Optional.of(opposite);
		else
			return Optional.empty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((src == null) ? 0 : src.hashCode());
		result = prime * result + ((trg == null) ? 0 : trg.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((edgeType == null) ? 0 : edgeType.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "--" + getName() + "-->";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VisualEdge other = (VisualEdge) obj;
		if (edgeType != other.edgeType)
			return false;
		if (src == null) {
			if (other.src != null)
				return false;
		} else if (!src.equals(other.src))
			return false;
		if (trg == null) {
			if (other.trg != null)
				return false;
		} else if (!trg.equals(other.trg))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
