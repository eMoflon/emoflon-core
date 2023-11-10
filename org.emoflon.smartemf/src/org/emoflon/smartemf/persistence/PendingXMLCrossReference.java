package org.emoflon.smartemf.persistence;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

class PendingXMLCrossReference {
	final private Element element;
	final private Attribute attribute;
	final private String[] ids;
	final private boolean[] isHref;
	final Map<Integer, String> idx2name = new HashMap<>();
	final Map<Integer, String> idx2Type = new HashMap<>();
	private int insertedIds = 0;

	public PendingXMLCrossReference(final Element element, final Attribute attribute, int numOfIds) {
		this.element = element;
		this.attribute = attribute;
		ids = new String[numOfIds];
		isHref = new boolean[numOfIds];
	}

	public void insertID(final String id, int idx) {
		ids[idx] = id;
		insertedIds++;
	}

	public void elementIsHref(boolean isHref, int idx, final String name, final String type) {
		this.isHref[idx] = isHref;
		idx2name.put(idx, name);
		if (type != null)
			idx2Type.put(idx, type);
	}

	public boolean isCompleted() {
		return insertedIds == ids.length;
	}

	public void writeBack() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			if (!isHref[i]) {
				sb.append(ids[i]);
				sb.append(" ");
			} else {
				Element child = new Element(idx2name.get(i));
				if (idx2Type.containsKey(i)) {
					Attribute atr = new Attribute(XmiParserUtil.XSI_TYPE, idx2Type.get(i),
							Namespace.getNamespace(XmiParserUtil.XSI_NS, XmiParserUtil.XSI_URI));
					child.getAttributes().add(atr);
				}
				Attribute atr = new Attribute(XmiParserUtil.HREF_ATR, ids[i]);
				child.getAttributes().add(atr);
				element.getChildren().add(child);
			}
		}
		if (sb.toString().isBlank())
			return;

		attribute.setValue(sb.toString().trim());
		element.getAttributes().add(attribute);
	}
}
