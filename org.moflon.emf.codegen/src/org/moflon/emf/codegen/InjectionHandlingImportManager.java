package org.moflon.emf.codegen;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import org.eclipse.emf.codegen.util.ImportManager;
import org.moflon.emf.injection.unparsing.InjectionConstants;

public class InjectionHandlingImportManager extends ImportManager {
	private StringBuilder importStringBuilder;
	private StringBuffer importStringBuffer;
	private int importInsertionPoint;

	public final Collection<String> injectedImports;

	public InjectionHandlingImportManager(String compilationUnitPackage) {
		this(compilationUnitPackage, false);
	}

	public InjectionHandlingImportManager(String compilationUnitPackage, final boolean useOrderInInjectionFile) {
		super(compilationUnitPackage);
		this.injectedImports = useOrderInInjectionFile ? new LinkedList<String>() : new TreeSet<String>();
	}

	public void emitSortedImports() {
		if (importStringBuilder != null) {
			importStringBuilder.insert(importInsertionPoint, computeSortedImports());
		} else if (importStringBuffer != null) {
			importStringBuffer.insert(importInsertionPoint, computeSortedImports());
		}
	}

	public void markImportLocation(StringBuilder stringBuilder) {
		importStringBuffer = null;
		importStringBuilder = stringBuilder;
		importInsertionPoint = stringBuilder.length();
		addCompilationUnitImports(stringBuilder.toString());
	}

	public void markImportLocation(StringBuffer stringBuffer) {
		importStringBuilder = null;
		importStringBuffer = stringBuffer;
		importInsertionPoint = stringBuffer.length();
		addCompilationUnitImports(stringBuffer.toString());
	}

	public String computeSortedImports() {
		String NL = getLineDelimiter();
		String previousPackageName = null;
		StringBuffer result = new StringBuffer();

		for (String importName : getImports()) {
			if (!injectedImports.contains(importName)) {
				String packageName = getPackageName(importName);
				if (previousPackageName != null && !previousPackageName.equals(packageName)) {
					result.append(NL);
				}
				previousPackageName = packageName;
				result.append(NL + "import " + importName + ";");
			}
		}

		result.append(NL + InjectionConstants.USER_IMPORTS_BEGIN);
		for (String importName : injectedImports) {
			result.append(NL + "import " + importName + ";");
		}
		// Uncomment this code instead of the for cycle above if imports have to be
		// grouped (EMF strategy)
		// previousPackageName = null;
		// for (String importName : injectedImports) {
		// String packageName = getPackageName(importName);
		// if (previousPackageName != null && !previousPackageName.equals(packageName))
		// {
		// result.append(NL);
		// }
		// previousPackageName = packageName;
		// result.append(NL + "import " + importName + ";");
		// }
		result.append(NL + InjectionConstants.USER_IMPORTS_END);
		return result.toString();
	}

	private String getPackageName(String qualifiedName) {
		int j = qualifiedName.lastIndexOf('.');
		return j == -1 ? "" : collapse(qualifiedName.substring(0, j));
	}

	private String collapse(String s) {
		char[] src = s.toCharArray();
		char[] result = null;
		int srcLength = src.length;
		int resultLenth = -1;

		for (int i = 0; i < srcLength; i++) {
			if (Character.isWhitespace(src[i])) {
				if (result == null) {
					result = new char[srcLength];
					System.arraycopy(src, 0, result, 0, i);
					resultLenth = i;
				}
			} else if (result != null) {
				result[resultLenth++] = src[i];
			}
		}
		return result != null ? new String(result, 0, resultLenth) : s;
	}
}
