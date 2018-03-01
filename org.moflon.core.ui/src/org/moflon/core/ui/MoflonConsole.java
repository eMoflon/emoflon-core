package org.moflon.core.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class MoflonConsole extends WriterAppender {

	private static final String MOFLON_CONSOLE = "eMoflon";
	private static final String PATTERN_KEY = "log4j.appender." + MOFLON_CONSOLE + ".layout.ConversionPattern";
	private static final String DEFAULT_PATTERN = "%5p [%c{2}::%L]: %m";

	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);

	private static final Color BLUE = new Color(Display.getDefault(), 0, 0, 128);

	private static final Color YELLOW = new Color(Display.getDefault(), 239, 155, 15);

	public MoflonConsole(final URL configFile) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configFile.toURI())));
		} catch (Exception e) {
			// ignore load exceptions
		}

		final String pattern = properties.containsKey(PATTERN_KEY) ? properties.getProperty(PATTERN_KEY)
				: DEFAULT_PATTERN;
		setLayout(new PatternLayout(pattern));
	}

	private static MessageConsole findConsole(final String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		// No console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	/**
	 * Prints a message on the MoflonConsole.
	 * 
	 * @param message
	 *            The message to print.
	 */
	private static void printMessage(final String message) {
		printMessage(message, null);
	}

	/**
	 * Prints a message in red on the MoflonConsole.
	 * 
	 * @param message
	 *            The message to print.
	 */
	private static void printErrorMessage(final String message) {
		printMessage(message, RED);
	}

	/**
	 * Prints a message in blue on the MoflonConsole.
	 * 
	 * @param message
	 *            The message to print.
	 */
	private static void printInfoMessage(final String message) {
		printMessage(message, BLUE);
	}

	/**
	 * Prints a message on the MoflonConsole.
	 * 
	 * @param message
	 *            The message to print.
	 */
	private static synchronized void printMessage(final String message, final Color color) {
		Display device = Display.getDefault();
		device.asyncExec(new Runnable() {
			@Override
			public void run() {
				_printMessage(message, color);
			}
		});
	}

	private static void _printMessage(final String message, final Color color) {
		MessageConsole myConsole = findConsole(MOFLON_CONSOLE);
		MessageConsoleStream out = myConsole.newMessageStream();
		if (color != null) {
			out.setColor(color);
		}
		try {
			out.println(message);
		} finally {
			try {
				out.close();
			} catch (IOException ioe) {
			}
		}
	}

	/**
	 * Gets the output stream of the MoflonConsole.
	 * 
	 * @return
	 */
	public static MessageConsoleStream getOutputStream() {
		MessageConsole myConsole = findConsole(MOFLON_CONSOLE);
		return myConsole.newMessageStream();
	}

	public static void clear() {
		MessageConsole myConsole = findConsole(MOFLON_CONSOLE);
		myConsole.clearConsole();
	}

	@Override
	public void append(final LoggingEvent event) {
		if (event.getLevel().equals(Level.ERROR) || event.getLevel().equals(Level.FATAL))
			printErrorMessage(layout.format(event));
		else if (event.getLevel().equals(Level.WARN))
			printWarningMessage(layout.format(event));
		else if (event.getLevel().equals(Level.INFO))
			printInfoMessage(layout.format(event));
		else
			printMessage(layout.format(event));
	}

	private void printWarningMessage(final String message) {
		printMessage(message, YELLOW);
	}
}
