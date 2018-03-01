package org.moflon.core.ui;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.console.IConsoleConstants;

/**
 * The perspective is responsible for choosing and arranging relevant UI
 * elements for the user. New extensions to Moflon can add their elements to
 * this basic perspective, reconfiguring if necessary, and do not need to define
 * a new perspective.
 *
 * The Moflon Perspective encompasses the normal Java Perspective and adds
 * typical elements for Java development.
 *
 * @author Anthony Anjorin
 */
public class MoflonPerspective implements IPerspectiveFactory {
	private IPageLayout layout;

	// BEGIN of constants that must be synchronized with plugin.xml
	public static final String MOFLON_PERSPECTIVE_ID = "org.moflon.ide.ui.perspective";
	// END of constants that must be synchronized with plugin.xml

	private static final String LAUNCH_ACTION_SET_ID = "org.eclipse.debug.ui.launchActionSet";
	private static final String MOFLON_ACTION_SET_ID = "org.moflon.ide.ui.actionSet";
	private static final String JAVA_PACKAGE_EXPLORER_ID = "org.eclipse.jdt.ui.PackageExplorer";
	private static final String PLANT_UML_VIEW = "net.sourceforge.plantuml.eclipse.views.PlantUmlView";

	/**
	 * Opens the eMoflon perspective in the given {@link IWorkbench}
	 * 
	 * @param workbench
	 */
	public static void switchToMoflonPerspective(final IWorkbench workbench) {
		try {
			workbench.showPerspective(MOFLON_PERSPECTIVE_ID, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			// Ignore
		}
	}

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		this.layout = layout;

		addViews();
		addActionSets();
		addShortcuts();
	}

	/**
	 * Configures the default views (e.g., Package Explorer)
	 */
	private void addViews() {
		// Note that each new Folder uses a percentage of the remaining EditorArea.

		// Java package explorer
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, layout.getEditorArea());

		topLeft.addView(MoflonPerspective.JAVA_PACKAGE_EXPLORER_ID);

		// Problem view and console
		IFolderLayout bottom = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f, layout.getEditorArea());

		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		// Outline
		IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.75f, layout.getEditorArea());
		topRight.addView(IPageLayout.ID_OUTLINE);
		topRight.addView(PLANT_UML_VIEW);
	}

	/**
	 * Configures the default views (esp. the eMoflon actions)
	 */
	private void addActionSets() {
		layout.addActionSet(MoflonPerspective.MOFLON_ACTION_SET_ID);

		// Java
		layout.addActionSet(MoflonPerspective.LAUNCH_ACTION_SET_ID);
		layout.addActionSet(JavaUI.ID_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);

		// Navigation
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
	}

	/**
	 * Configures the default action icons to show
	 */
	private void addShortcuts() {
		this.layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.JavaProjectWizard");
		this.layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard");
		this.layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard");
		this.layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard");
		this.layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewEnumCreationWizard");
		this.layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewAnnotationCreationWizard");
		this.layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard");
		this.layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewJavaWorkingSetWizard");
		this.layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		this.layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		this.layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");
		this.layout.addNewWizardShortcut("org.eclipse.jdt.junit.wizards.NewTestCaseCreationWizard");

		this.layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaPerspective");
		this.layout.addPerspectiveShortcut("org.eclipse.debug.ui.DebugPerspective");
		this.layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaBrowsingPerspective");

		this.layout.addShowViewShortcut("org.eclipse.jdt.ui.PackageExplorer");
		this.layout.addShowViewShortcut("org.eclipse.jdt.ui.TypeHierarchy");
		this.layout.addShowViewShortcut("org.eclipse.jdt.ui.SourceView");
		this.layout.addShowViewShortcut("org.eclipse.jdt.ui.JavadocView");
		this.layout.addShowViewShortcut("org.eclipse.search.ui.views.SearchView");
		this.layout.addShowViewShortcut("org.eclipse.ui.console.ConsoleView");
		this.layout.addShowViewShortcut("org.eclipse.ui.views.ProblemView");
		this.layout.addShowViewShortcut("org.eclipse.ui.views.ResourceNavigator");
		this.layout.addShowViewShortcut("org.eclipse.ui.views.TaskList");
		this.layout.addShowViewShortcut("org.eclipse.ui.views.ProgressView");
		this.layout.addShowViewShortcut("org.eclipse.ui.navigator.ProjectExplorer");
		this.layout.addShowViewShortcut("org.eclipse.ui.texteditor.TemplatesView");
		this.layout.addShowViewShortcut("org.eclipse.ant.ui.views.AntView");
		this.layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
	}
}
