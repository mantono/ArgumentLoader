package com.mantono.argumentloader;

/**
 * Interface for program options that can be loaded through the command line
 * argument vector. The purpose of this interface, and its implementing classes,
 * is to simplify argument loading and validation with settings that are sent to
 * the main method at application launch.
 *
 * @author Anton &Ouml;sterberg
 *
 */
public interface ProgramOption
{
	/**
	 * @return returns the short flag representation for this option that is
	 * combined with a single dash ("-").
	 */
	char getShortFlag();

	/**
	 *
	 * @return returns the long flag representation for this option that is
	 * combined with a double dash ("--").
	 */
	String getLongFlag();

	/**
	 *
	 * @return a description that describes what this option does.
	 */
	String getDescription();

	/**
	 *
	 * @return the default value.
	 */
	String defaultValue();
	
	/**
	 * 
	 * @return true if this option takes an argument, else false.
	 */
	boolean takesArgument();

	/**
	 * Checks whether a {@link String} matches any of the options for this
	 * class.
	 *
	 * @param input the {@link String} that should be checked whether it matched
	 * or not.
	 * @return true if the input is equal to the short flag or the long flag
	 * without the dash prefix.
	 */
	default boolean matches(final String input)
	{
		return input.equals("-" + getShortFlag()) || input.equals("--" + getLongFlag());
	}

	/**
	 * @return a text that is printed whenever a user calls the help flag, if
	 * such a flag exists for the implementing class or enumerate.
	 */
	default String helpDescription()
	{
		final StringBuilder str = new StringBuilder();
		str.append("-" + getShortFlag() + ", --" + getLongFlag() + "\n\t" + getDescription() + "\n");
		return str.toString();
	}
}
