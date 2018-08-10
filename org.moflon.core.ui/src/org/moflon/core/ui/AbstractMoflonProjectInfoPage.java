package org.moflon.core.ui;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.moflon.core.utilities.WorkspaceHelper;

public abstract class AbstractMoflonProjectInfoPage extends WizardPage {
	private String projectName;

	private boolean useDefaultLocation;

	private String projectLocation;

	private Button defaultLocationCheckbox;

	private Label projectLocationLabel;

	private Text projectLocationTextfield;

	private Button browseTargetDirectoryButton;

	private Text projectNameTextfield;

	private boolean generateDefaultEmfatic = true;

	public AbstractMoflonProjectInfoPage(String name, String title, String desc) {
		super(name);
		projectName = "";
		useDefaultLocation = true;
		setProjectLocation(null);

		setTitle(title);
		setDescription(desc);
		setImageDescriptor(UiUtilities.getImage(WorkspaceHelper.getPluginId(getClass()),
				"resources/icons/metamodelProjectWizard.gif"));
	}

	@Override
	public void createControl(final Composite parent) {
		// Create root container
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;

		createControlsForProjectName(container);

		createControlsForProjectLocation(container);

		// Place cursor in textfield
		projectNameTextfield.setFocus();

		// Set controls and update
		setControl(container);
		dialogChanged();
	}

	public void createControlsForProjectLocation(final Composite container) {
		defaultLocationCheckbox = new Button(container, SWT.CHECK);
		defaultLocationCheckbox.setText("Use default location");
		createDummyLabel(container);
		createDummyLabel(container);

		projectLocationLabel = createDummyLabel(container);
		projectLocationLabel.setText("&Location:");
		projectLocationTextfield = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		projectLocationTextfield.setLayoutData(gd2);

		projectLocationTextfield.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final String text = projectLocationTextfield.getText();
				if (text.isEmpty()) {
					setErrorMessage("Project location must not be empty");
				} else {
					setProjectLocation(projectLocationTextfield.getText());
				}

				dialogChanged();
			}
		});

		browseTargetDirectoryButton = new Button(container, SWT.PUSH);
		browseTargetDirectoryButton.setText("Browse...");
		GridData gd3 = new GridData();
		browseTargetDirectoryButton.setLayoutData(gd3);
		browseTargetDirectoryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(container.getShell(), SWT.SAVE);
				dialog.setText("Select location for metamodel");
				File initialDeploymentDirectory = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
				dialog.setFilterPath(initialDeploymentDirectory.getAbsolutePath());

				String destinationDirectory = dialog.open();

				if (destinationDirectory != null) {
					projectLocationTextfield.setText(destinationDirectory + File.separator + projectName);
				}
			}

		});

		defaultLocationCheckbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				defaultLocationSelectionChanged();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				defaultLocationSelectionChanged();
			}

		});
		defaultLocationCheckbox.setSelection(useDefaultLocation);
		defaultLocationSelectionChanged();

		Button generateDefaultEmfaticButton = new Button(container, SWT.RADIO);
		generateDefaultEmfaticButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				generateDefaultEmfatic = generateDefaultEmfaticButton.getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				generateDefaultEmfatic = generateDefaultEmfaticButton.getSelection();
			}
		});
		generateDefaultEmfaticButton.setText("Generate default Emfatic file (.emf) in project");
		generateDefaultEmfaticButton.setSelection(true);

		createDummyLabel(container);
		createDummyLabel(container);

		Button generateDefaultEcore = new Button(container, SWT.RADIO);
		generateDefaultEcore.setText("Generate default Ecore file (.ecore) in project");
	}

	public void createControlsForProjectName(final Composite container) {
		// Create control for entering project name
		Label label = createDummyLabel(container);
		label.setText("&Project name:");

		projectNameTextfield = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		projectNameTextfield.setLayoutData(gd);
		projectNameTextfield.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				projectName = projectNameTextfield.getText();

				if (!useDefaultLocation && projectName.isEmpty()) {
					setErrorMessage("Project name must not be empty.");
				}

				dialogChanged();
			}
		});
	}

	public Label createDummyLabel(final Composite container) {
		return new Label(container, SWT.NULL);
	}

	public void defaultLocationSelectionChanged() {
		useDefaultLocation = defaultLocationCheckbox.getSelection();
		projectLocationLabel.setEnabled(!useDefaultLocation);
		projectLocationTextfield.setEnabled(!useDefaultLocation);
		browseTargetDirectoryButton.setEnabled(!useDefaultLocation);

		final String desiredLocation = useDefaultLocation ? null : projectLocationTextfield.getText();
		setProjectLocation(desiredLocation);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && getErrorMessage() == null;
	}

	/***
	 * Returns the project name
	 * 
	 * @return
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Returns the selected project location.
	 *
	 * If the default location should be used, null is returned.
	 *
	 * @return
	 */
	public IPath getProjectLocation() {
		return projectLocation == null ? null : new Path(projectLocation);
	}

	/**
	 * Sets the project path to be used
	 *
	 * @param projectLocation the desired project location. <code>null</code>
	 *                        indicates the default location
	 */
	private void setProjectLocation(final String projectLocation) {
		this.projectLocation = projectLocation;
	}

	/**
	 * Sets the given error message to indicate a validation problem
	 * 
	 * @param message the message or <code>null</code> if no problems were detected
	 */
	private final void updateStatus(final String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * Triggers project name validation
	 */
	private void dialogChanged() {
		final IStatus validity = validateProjectName(projectName);

		if (validity.isOK())
			updateStatus(null);
		else
			updateStatus(validity.getMessage());
	}

	/**
	 * Checks if given name is a valid name for a new project in the current
	 * workspace.
	 *
	 * @param projectName Name of project to be created in current workspace
	 * @param pluginId    ID of bundle
	 * @return A status object indicating success or failure and a relevant message.
	 */
	private static IStatus validateProjectName(final String projectName) {
		final String pluginId = WorkspaceHelper.getPluginId(AbstractMoflonProjectInfoPage.class);
		// Check if anything was entered at all
		if (projectName.isEmpty())
			return new Status(IStatus.ERROR, pluginId, "Name must be specified");

		// Check if name is a valid path for current platform
		final IStatus validity = ResourcesPlugin.getWorkspace().validateName(projectName, IResource.PROJECT);
		if (!validity.isOK())
			return new Status(IStatus.ERROR, pluginId, validity.getMessage());

		// Check if no other project with the same name already exists in workspace
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (Arrays.stream(projects).anyMatch(project -> project.getName().equals(projectName))) {
			return new Status(IStatus.ERROR, pluginId, "A project with this name exists already.");
		}

		return new Status(IStatus.OK, pluginId, "Project name is valid");
	}

	public boolean generateDefaultEmfaticFile() {
		return generateDefaultEmfatic;
	}
}