package com.mantono.argumentloader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * SettingsLoader is meant to simplify the use of loading configurations and
 * settings flags through the standard arguments vector (String[] args) in a
 * program entry point class and test configurations files. Any class
 * implementing the {@link ProgramOption} interface will also have to define a
 * default option for every setting, in case neither command line flags or a
 * configuration file is used.
 * 
 * @author Anton &Ouml;sterberg
 *
 * @param <T> the {@link Enum} class that is used for defining the settings
 * flags.
 */
public class SettingsLoader<T extends Enum<T> & ProgramOption>
{
	private final Class<T> enumClass;
	private final EnumMap<T, String> settings;

	/**
	 * @param enumClass the {@link Enum} class that will be used with this
	 * instance of this class.
	 */
	public SettingsLoader(Class<T> enumClass)
	{
		this.enumClass = enumClass;
		this.settings = loadDefaultSettings();
	}

	/**
	 * Reads the default settings of {@link Enum} class <code>T</code>.
	 * 
	 * @return the default settings as an {@link EnumMap}.
	 */
	private EnumMap<T, String> loadDefaultSettings()
	{
		final EnumMap<T, String> defaultSettings = new EnumMap<T, String>(enumClass);
		for(T op : values())
			defaultSettings.put(op, op.defaultValue());
		return defaultSettings;
	}

	/**
	 * Takes the {@link String} representation of a implementing
	 * {@link ProgramOption} and retrieves its corresponding enumerate key.
	 * 
	 * @param string the {@link String} for which an option is sought.
	 * @return the option of type <code>T</code>.
	 */
	private T getOption(String string)
	{
		for(T option : values())
			if(option.matches(string))
				return option;
		return null;
	}

	/**
	 * @return all the {@link Enum} keys for this class instantiated enum class.
	 */
	private T[] values()
	{
		return enumClass.getEnumConstants();
	}

	/**
	 * Apply the command line flags that are sent through the argument vector,
	 * overwriting any default options or options from any configuration file.
	 * Options set through the argument vector has the highest priority.
	 * 
	 * @param args the arguments that will be applied to the current
	 * configuration.
	 */
	public void applyAgrumentVector(String[] args)
	{
		for(int i = 0; i < args.length; i += 2)
		{
			T option = getOption(args[i]);
			nullCheck(option, args[i]);
			if(option.equals(getOption("--help")))
			{
				for(T op : values())
					System.out.println(op);
				System.exit(0);
			}
			try
			{
				settings.put(option, args[i + 1]);
			}
			catch(ArrayIndexOutOfBoundsException exception)
			{
				System.err.println("Flag " + args[i] + " requires an argument.");
				System.exit(3);
			}
		}
	}

	/**
	 * Check whether a found option actually exists or is null. If it is null,
	 * an error message is written to {@link System#err} and the program exits
	 * with exit code <tt>1</tt>.
	 * 
	 * @param option that will be checked whether it is <tt>null</tt> or not.
	 * @param flag the input {@link String} that was given as an argument.
	 */
	private void nullCheck(final T option, final String flag)
	{
		if(option == null)
		{
			System.err.println("Argument " + flag + " is not a valid flag. See --help for options.");
			System.exit(1);
		}
	}

	/**
	 * Reads and parses the content of a config file.
	 * 
	 * @param fileName name of the file that will be read.
	 * @return true if a file was found, read and successfully parsed, else
	 * false.
	 * @throws IOException if there was a problem reading from the file.
	 */
	public boolean readConfig(String fileName) throws IOException
	{
		final File file = new File(fileName);
		if(!file.exists())
			return false;

		List<String> list = Files.readAllLines(file.toPath(), Charset.defaultCharset());
		for(String line : list)
			parseLine(line);

		return true;
	}

	/**
	 * Parses the content of a line from a config file, making sure it
	 * correspondents to valid option as defined by the implementing
	 * {@link ProgramOption} class.
	 * 
	 * Options are read a key value pairs, which are split by the used of
	 * '&#61;'.
	 * 
	 * @param line the line that will be parsed.
	 */
	private void parseLine(String line)
	{
		final String[] keyValue = line.split("=", 2);
		final String key = "--" + keyValue[0].toLowerCase().trim();
		final String value = keyValue[1].trim();
		T option = getOption(key);
		nullCheck(option, key);
		settings.put(option, value);
	}

	/**
	 * Returns an unmodifiable copy of the content of the current settings, as
	 * the settings are only meant to be modified by command line flags or the
	 * config file.
	 * 
	 * @return the loaded settings.
	 */
	public Map<T, String> getSettings()
	{
		return java.util.Collections.unmodifiableMap(settings);
	}
}