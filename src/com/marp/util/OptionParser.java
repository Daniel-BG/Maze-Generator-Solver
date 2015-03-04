package com.marp.util;

public class OptionParser {

	private String options;
	
	/**
	 * Constructor for the class. The options must come with the following syntax:
	 * 	option1='argument1'[spacing1]option2='argument2'[spacing2]...[spacingn-1]optionn='argumentn'
	 * @param options
	 */
	public OptionParser(String options) {
		this.setOptions(options);
	}
	
	/**
	 * Sets or changes the option String
	 * @param options
	 */
	public void setOptions(String options) {
		if (options == null)
			throw new IllegalArgumentException("The options String cannot be empty");
		this.options = options;
	}
	
	/**
	 * Gets the argument of option, assuming it is an integer. Will throw excpetion if not
	 * @param option
	 * @return
	 * @throws NonExistantOptionException 
	 */
	public int getArgumentValue(String option) throws NonExistantOptionException {
		String argument = this.getArgument(option);
		return Integer.parseInt(argument);
	}
	
	/**
	 * Gets the argument associated to the option option
	 * @param option
	 * @return
	 * @throws NonExistantOptionException when the option wasn't specified
	 */
	public String getArgument(String option) throws NonExistantOptionException {
		int index = OptionParser.getEndIndexOfSequence(this.options, option);
		if (index == -1)
			throw new NonExistantOptionException();	//la opción no está en la lista
		if (index >= this.options.length()-4)
			throw new BadlyFormattedOption();		//la opción no tiene la longitud necesara
		if (this.options.charAt(index+1) != '=' || this.options.charAt(index+2) != '\'')
			throw new BadlyFormattedOption();		//la opción no tiene el formato adecuado "opcion='..."
		return OptionParser.getUntilNextChar(this.options, index+3, '\'');
	}
	
	/**
	 * Gets all the chars between the char at the position start (inclusive) and the first ocurrence of the
	 * char end (exclusive)
	 * @param source
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getUntilNextChar(String source, int start, char end) {
		if (source == null || source.length() <= start)
			throw new IndexOutOfBoundsException();
		String ret = "";
		int i = 0;
		while (start+i < source.length() && source.charAt(start+i) != end) {
			ret+=source.charAt(start+i);
			i++;
		}
		return ret;
	}
	
	/**
	 * Gets the index where the end of the first ocurrence of the String target is located within source.
	 * Index points at the location of the last character of target, not the next, to ensure pointing to a 
	 * valid location not out of bounds.
	 * @param source
	 * @param target
	 * @return the location or -1 if it was not found
	 */
	public static int getEndIndexOfSequence(String source, String target) {
		if (source == null || target == null || source.length() == 0 || target.length() == 0)
			return -1;
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == target.charAt(0)) {
				for (int j = 0; j < target.length(); j++) {
					try {
						if (source.charAt(i+j) != target.charAt(j))
							continue;
					} catch (IndexOutOfBoundsException e) {
						return -1;
					}
					if (j == target.length()-1)
						return i+j;
				}
			}
		}
		return -1;
	}

}

