package org.moflon.core.build;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.gervarro.eclipse.workspace.util.WorkspaceTask;

public final class ProjectBuilderTask extends WorkspaceTask {
	private final IBuildConfiguration[] buildConfigurations;
	private final int kind;

	public ProjectBuilderTask() {
		this(IncrementalProjectBuilder.INCREMENTAL_BUILD);
	}

	public ProjectBuilderTask(final int kind) {
		this.buildConfigurations = null;
		this.kind = kind;
	}

	public ProjectBuilderTask(final IBuildConfiguration... buildConfigurations) {
		this(buildConfigurations, IncrementalProjectBuilder.INCREMENTAL_BUILD);
	}

	public ProjectBuilderTask(final IBuildConfiguration[] buildConfigurations, final int kind) {
		if (buildConfigurations == null) {
			throw new NullPointerException();
		}
		this.buildConfigurations = buildConfigurations;
		this.kind = kind;
	}

	@Override
	public String getTaskName() {
		final String buildName = getBuildKindName();
		final StringBuilder builder = new StringBuilder();
		builder.append("Performing ");
		if (buildName.length() > 0) {
			builder.append(buildName);
			builder.append(" ");
		}
		builder.append("build");
		return builder.toString();
	}

	@Override
	public ISchedulingRule getRule() {
		return ResourcesPlugin.getWorkspace().getRuleFactory().buildRule();
	}

	@Override
	public void run(final IProgressMonitor monitor) throws CoreException {
		if (buildConfigurations != null) {
			ResourcesPlugin.getWorkspace().build(buildConfigurations, kind, true, monitor);
		} else {
			ResourcesPlugin.getWorkspace().build(kind, monitor);
		}
	}

	private final String getBuildKindName() {
		switch (kind) {
		case IncrementalProjectBuilder.AUTO_BUILD:
			return "auto";
		case IncrementalProjectBuilder.CLEAN_BUILD:
			return "clean";
		case IncrementalProjectBuilder.FULL_BUILD:
			return "full";
		case IncrementalProjectBuilder.INCREMENTAL_BUILD:
			return "incremental";
		default:
			return "";
		}
	}
}
