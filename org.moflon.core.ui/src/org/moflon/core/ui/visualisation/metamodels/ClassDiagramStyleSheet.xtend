package org.moflon.core.ui.visualisation.metamodels

class ClassDiagramStyleSheet {
	def static String getStyleSheet() {
		return '''edge {
					size: 2px;
					fill-color: black;
					fill-mode: dyn-plain;
					text-alignment: along;
					text-background-mode: plain;
					text-size: 14;
					text-style: bold;
				}
				
				node {
					size-mode: fit;
					shape: box;
					fill-color: grey, red, blue, green;
					stroke-mode: plain;
					stroke-color: black;
					stroke-width: 1.5px;
					text-size: 16px;
					text-mode: normal;
					text-background-color: grey;
					padding: 3px, 3px;
					text-background-mode: plain;
					fill-mode: dyn-plain;
				}
				sprite {
					size-mode: fit;
					shape: box;
					fill-color: grey, red, blue, green;
					stroke-mode: plain;
					stroke-color: black;
					stroke-width: 1.5px;
					text-size: 16px;
					text-mode: normal;
					text-background-color: grey;
					text-background-mode: plain;
					fill-mode: none;
				}
				'''
	}
}