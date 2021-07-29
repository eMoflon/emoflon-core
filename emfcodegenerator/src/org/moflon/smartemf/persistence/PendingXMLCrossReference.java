package org.moflon.smartemf.persistence;

import org.jdom2.Attribute;
import org.jdom2.Element;

class PendingXMLCrossReference {
	final private Element element;
	final private Attribute attribute;
	final private String[] ids;
	private int insertedIds = 0;
	
	public PendingXMLCrossReference(final Element element, final Attribute attribute, int numOfIds) {
		this.element = element;
		this.attribute = attribute;
		ids = new String[numOfIds];
	}
	
	public void insertID(final String id, int idx) {
		ids[idx] = id;
		insertedIds++;
	}
	
	public boolean isCompleted() {
		return insertedIds == ids.length;
	}
	
	public void writeBack() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<ids.length; i++) {
			sb.append(ids[i]);
			if(i < ids.length-1) {
				sb.append(" ");
			}
		}
		if(sb.toString().isBlank())
			return;
		
		attribute.setValue(sb.toString());
		element.getAttributes().add(attribute);
	}
}
