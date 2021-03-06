package org.cishell.reference.gui.log;

import java.util.Collection;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

/**
 * * This logger will log to {@link System#out}.
 * 
 * @author David M. Coe (david.coe@gmail.com)
 * 
 */
public class LogToConsole implements LogListener {
	private static final String NEWLINE = System.getProperty("line.separator");
	private boolean detailedMessages;
	private int minLevel;
	private ImmutableCollection<String> ignoredPrefixes;

	/**
	 * 
	 * @param detailedMessages
	 *            If {@code false}, only the message and the level will be
	 *            printed. If {@code true} then more details will be given.
	 */
	public LogToConsole(boolean detailedMessages) {
		this(detailedMessages, LogService.LOG_DEBUG,
				Utilities.DEFAULT_IGNORED_PREFIXES);
	}

	/**
	 * This logger will log to {@link System#out}.
	 * 
	 * @param detailedMessages
	 *            If {@code false}, only the message and the level will be
	 *            printed. If {@code true} then more details will be given.
	 * @param minLevel
	 *            The minimum OSGi {@link LogService} level that should be
	 *            logged.
	 * @param ignoredPrefixes
	 *            No message starting with one of these prefixes should be
	 *            logged.
	 */
	public LogToConsole(boolean detailedMessages, int minLevel, Collection<String> ignoredPrefixes) {
		if (ignoredPrefixes == null) {
			throw new IllegalArgumentException("ignoredPrefixes must not be null.");
		}
		
		this.detailedMessages = detailedMessages;
		this.minLevel = minLevel;
		this.ignoredPrefixes = ImmutableList.copyOf(ignoredPrefixes);
	}

	@Override
	public void logged(LogEntry entry) {
		if (!Utilities.shouldLogMessage(entry,
				this.ignoredPrefixes)) {
			return;
		}

		if (entry.getLevel() > this.minLevel) {
			return;
		}
		
		String level = "";
		switch (entry.getLevel()) {
			case LogService.LOG_DEBUG:
				level = "DEBUG";
				break;
			case LogService.LOG_ERROR:
				level = "ERROR";
				break;
			case LogService.LOG_INFO:
				level = "INFO";
				break;
			case LogService.LOG_WARNING:
				level = "WARNING";
				break;
			default:
				level = "UNKNOWN LEVEL";
				break;
		}

		StringBuilder logEntry = new StringBuilder();
		if (this.detailedMessages) {
			logEntry.append("[" + entry.getBundle().getSymbolicName() + "]: ");
			logEntry.append(level + " " + entry.getMessage());

			if (entry.getException() != null) {
				logEntry.append(NEWLINE + "Exception: " + NEWLINE);
				logEntry.append(entry.getException());
			}
		} else {
			logEntry.append(level + ": " + entry.getMessage());
		}

		System.out.println(logEntry.toString());
	}
}
