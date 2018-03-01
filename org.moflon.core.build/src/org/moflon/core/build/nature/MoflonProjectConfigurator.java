package org.moflon.core.build.nature;

import java.util.Arrays;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.gervarro.eclipse.workspace.autosetup.WorkspaceAutoSetupModule;
import org.gervarro.eclipse.workspace.util.ProjectUtil;

/**
 * Parent class for Eclipse project natures that self-organize their nature and
 * builder IDs
 *
 * @author Gergely Varró - Initial implementation
 * @author Roland Kluge - Documentation
 */
public abstract class MoflonProjectConfigurator extends ProjectConfiguratorNature {
	private final String natureID;
	private final String builderID;

	/**
	 * Initializes the nature and builder IDs of this configurator
	 */
	public MoflonProjectConfigurator() {
		this.natureID = getNatureId();
		this.builderID = getBuilderId();
	}

	/**
	 * Returns the builder ID that corresponds to the type of project managed by the
	 * particular subclass
	 * 
	 * @return the builder ID
	 */
	protected abstract String getBuilderId();

	/**
	 * Returns the nature ID that corresponds to the type of project managed by the
	 * particular subclass
	 * 
	 * @return the nature ID
	 */
	protected abstract String getNatureId();

	/**
	 * Updates the given list of nature IDs to contain this configurator's nature ID
	 *
	 * @param natureIDs
	 *            the list of nature IDs to manipulate
	 * @param added
	 *            true if the nature ID shall be added, false if it shall be removed
	 * @see #getNatureId()
	 */
	@Override
	public String[] updateNatureIDs(String[] natureIDs, final boolean added) throws CoreException {
		if (added) {
			final int moflonNaturePosition = ProjectUtil.indexOf(natureIDs, natureID);
			if (moflonNaturePosition < 0) {
				final String[] oldNatureIDs = natureIDs;
				natureIDs = new String[oldNatureIDs.length + 1];
				System.arraycopy(oldNatureIDs, 0, natureIDs, 1, oldNatureIDs.length);
				natureIDs[0] = natureID;
			} else if (moflonNaturePosition > 0) {
				System.arraycopy(natureIDs, 0, natureIDs, 1, moflonNaturePosition);
				natureIDs[0] = natureID;
			}
		} else {
			int naturePosition = ProjectUtil.indexOf(natureIDs, natureID);
			if (naturePosition >= 0) {
				natureIDs = WorkspaceAutoSetupModule.remove(natureIDs, naturePosition);
			}
		}
		return natureIDs;
	}

	/**
	 * Updates the given build specification to contain this configurator's builder
	 * ID
	 *
	 * @param description
	 *            the description of the project to manipulate
	 * @param buildSpecs
	 *            the build specification to manipulate
	 * @param added
	 *            true if the builder ID shall be added, false if it shall be
	 *            removed
	 * @see #getBuilderId()()
	 */
	@Override
	public ICommand[] updateBuildSpecs(final IProjectDescription description, ICommand[] buildSpecs,
			final boolean added) throws CoreException {
		if (added) {
			int javaBuilderPosition = ProjectUtil.indexOf(buildSpecs, "org.eclipse.jdt.core.javabuilder");
			int moflonBuilderPosition = ProjectUtil.indexOf(buildSpecs, builderID);
			if (moflonBuilderPosition < 0) {
				final ICommand moflonBuilder = description.newCommand();
				moflonBuilder.setBuilderName(builderID);
				buildSpecs = Arrays.copyOf(buildSpecs, buildSpecs.length + 1);
				moflonBuilderPosition = buildSpecs.length - 1;
				buildSpecs[moflonBuilderPosition] = moflonBuilder;
			}
			if (javaBuilderPosition >= 0 && javaBuilderPosition < moflonBuilderPosition) {
				final ICommand moflonBuilder = buildSpecs[moflonBuilderPosition];
				System.arraycopy(buildSpecs, javaBuilderPosition, buildSpecs, javaBuilderPosition + 1,
						moflonBuilderPosition - javaBuilderPosition);
				moflonBuilderPosition = javaBuilderPosition++;
				buildSpecs[moflonBuilderPosition] = moflonBuilder;
			}
		} else {
			int moflonBuilderPosition = ProjectUtil.indexOf(buildSpecs, builderID);
			if (moflonBuilderPosition >= 0) {
				ICommand[] oldBuilderSpecs = buildSpecs;
				buildSpecs = new ICommand[oldBuilderSpecs.length - 1];
				if (moflonBuilderPosition > 0) {
					System.arraycopy(oldBuilderSpecs, 0, buildSpecs, 0, moflonBuilderPosition);
				}
				if (moflonBuilderPosition == buildSpecs.length) {
					System.arraycopy(oldBuilderSpecs, moflonBuilderPosition + 1, buildSpecs, moflonBuilderPosition,
							buildSpecs.length - moflonBuilderPosition);
				}
			}
		}
		return buildSpecs;
	}
}