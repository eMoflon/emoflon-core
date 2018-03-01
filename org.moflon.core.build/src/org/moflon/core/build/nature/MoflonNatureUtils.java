package org.moflon.core.build.nature;

import java.util.Arrays;

import org.gervarro.eclipse.workspace.util.ProjectUtil;
import org.moflon.core.utilities.UtilityClassNotInstantiableException;

/**
 * Utility class for manipulating the project nature
 * 
 * @author Roland Kluge - Initial implementation
 *
 */
public final class MoflonNatureUtils {
	// Disabled constructor of utility class
	private MoflonNatureUtils() {
		throw new UtilityClassNotInstantiableException();
	}

	/**
	 * Inserts the given natureID at the end of the given natureIDs if the nature is
	 * missing.
	 * 
	 * @param natureIDs
	 *            the original list of nature IDs
	 * @param natureID
	 *            the nature ID to add if missing
	 * @return the new list of nature IDs
	 */
	public static String[] appendIfMissing(final String[] natureIDs, final String natureID) {
		if (ProjectUtil.indexOf(natureIDs, natureID) < 0) {
			final String[] newNatureIDs = Arrays.copyOf(natureIDs, natureIDs.length + 1);
			newNatureIDs[newNatureIDs.length - 1] = natureID;
			return newNatureIDs;
		} else {
			return natureIDs;
		}
	}

	/**
	 * Returns a list of IDs that results from removing the given ID 'id' from the
	 * given list of IDs 'inputIDs'.
	 * 
	 * @param inputNatureIDs
	 * @param natureID
	 * @return the updated list of IDs
	 */
	public static String[] removeNatureID(final String[] inputNatureIDs, final String natureID) {
		final int positionInInputIDs = ProjectUtil.indexOf(inputNatureIDs, natureID);
		if (positionInInputIDs >= 0) {
			final Object[] newNatureIDsTmp = ProjectUtil.remove(inputNatureIDs, positionInInputIDs);
			// Workaround for https://github.com/eMoflon/emoflon-tool/issues/177
			final String[] newNatureIDs = Arrays.copyOf(inputNatureIDs, newNatureIDsTmp.length);
			System.arraycopy(newNatureIDsTmp, 0, newNatureIDs, 0, newNatureIDsTmp.length);
			return newNatureIDs;
		} else {
			return inputNatureIDs;
		}
	}
}
