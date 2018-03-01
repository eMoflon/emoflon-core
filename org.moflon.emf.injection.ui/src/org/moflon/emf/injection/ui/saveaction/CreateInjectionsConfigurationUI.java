package org.moflon.emf.injection.ui.saveaction;

import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUpConfigurationUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;

// Inspired by: http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Fguide%2Fjdt_api_contributing_a_cleanup.htm
public class CreateInjectionsConfigurationUI implements ICleanUpConfigurationUI {

	private PixelConverter fPixelConverter;

	private CleanUpOptions options;

	@Override
	public void setOptions(final CleanUpOptions options) {
		this.options = options;
	}

	@Override
	public Composite createContents(final Composite parent) {
		final int numColumns = 4;

		if (fPixelConverter == null) {
			fPixelConverter = new PixelConverter(parent);
		}

		final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setFont(parent.getFont());

		Composite scrollContainer = new Composite(sashForm, SWT.NONE);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrollContainer.setLayoutData(gridData);

		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		scrollContainer.setLayout(layout);

		ScrolledComposite scroll = new ScrolledComposite(scrollContainer, SWT.V_SCROLL | SWT.H_SCROLL);
		scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);

		final Composite settingsContainer = new Composite(scroll, SWT.NONE);
		settingsContainer.setFont(sashForm.getFont());

		scroll.setContent(settingsContainer);

		settingsContainer.setLayout(new PageLayout(scroll, 400, 400));
		settingsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite settingsPane = new Composite(settingsContainer, SWT.NONE);
		settingsPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		layout = new GridLayout(numColumns, false);
		layout.verticalSpacing = (int) (1.5
				* fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING));
		layout.horizontalSpacing = fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.marginHeight = fPixelConverter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = fPixelConverter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		settingsPane.setLayout(layout);
		doCreatePreferences(settingsPane);

		settingsContainer.setSize(settingsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		scroll.addControlListener(new ControlListener() {

			@Override
			public void controlMoved(final ControlEvent e) {
			}

			@Override
			public void controlResized(final ControlEvent e) {
				settingsContainer.setSize(settingsContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
		return sashForm;
	}

	/**
	 * Creates the preferences for the tab page.
	 * 
	 * @param composite
	 *            Composite to create in
	 */
	protected void doCreatePreferences(final Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(new GridLayout(1, false));
		group.setText("Auto-create injections"); //$NON-NLS-1$

		final Button checkbox = new Button(group, SWT.CHECK);
		checkbox.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		checkbox.setText("Enable injection extraction on save"); //$NON-NLS-1$
		checkbox.setSelection(options.isEnabled(CreateInjectionsSaveAction.KEY_CREATE_INJECTIONS)); // $NON-NLS-1$
		checkbox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				options.setOption(CreateInjectionsSaveAction.KEY_CREATE_INJECTIONS,
						checkbox.getSelection() ? CleanUpOptions.TRUE : CleanUpOptions.FALSE); // $NON-NLS-1$
			}
		});
	}

	@Override
	public int getCleanUpCount() {
		return 1;
	}

	@Override
	public int getSelectedCleanUpCount() {
		int count = 0;
		count += CleanUpOptions.TRUE.equals(options.getValue(CreateInjectionsSaveAction.KEY_CREATE_INJECTIONS)) ? 1 : 0;
		return count;
	}

	@Override
	public String getPreview() {
		return "No representative code";
	}

	/**
	 * Layout used for the settings part. Makes sure to show scrollbars if
	 * necessary. The settings part needs to be layouted on resize.
	 */
	private static class PageLayout extends Layout {

		private final ScrolledComposite fContainer;

		private final int fMinimalWidth;

		private final int fMinimalHight;

		private PageLayout(final ScrolledComposite container, final int minimalWidth, final int minimalHight) {
			fContainer = container;
			fMinimalWidth = minimalWidth;
			fMinimalHight = minimalHight;
		}

		@Override
		public Point computeSize(final Composite composite, final int wHint, final int hHint, final boolean force) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}

			int x = fMinimalWidth;
			int y = fMinimalHight;
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				Point size = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				x = Math.max(x, size.x);
				y = Math.max(y, size.y);
			}

			Rectangle area = fContainer.getClientArea();
			if (area.width > x) {
				fContainer.setExpandHorizontal(true);
			} else {
				fContainer.setExpandHorizontal(false);
			}

			if (area.height > y) {
				fContainer.setExpandVertical(true);
			} else {
				fContainer.setExpandVertical(false);
			}

			if (wHint != SWT.DEFAULT) {
				x = wHint;
			}
			if (hHint != SWT.DEFAULT) {
				y = hHint;
			}

			return new Point(x, y);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite,
		 * boolean)
		 */
		@Override
		public void layout(final Composite composite, final boolean force) {
			Rectangle rect = composite.getClientArea();
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				children[i].setSize(rect.width, rect.height);
			}
		}
	}
}
