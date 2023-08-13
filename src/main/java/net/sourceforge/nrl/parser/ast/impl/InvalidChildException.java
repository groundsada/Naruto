package net.sourceforge.nrl.parser.ast.impl;

import org.antlr.runtime.IntStream;

import org.antlr.runtime.RecognitionException;

public class InvalidChildException extends RecognitionException {

	private static final long serialVersionUID = -2606913596220660713L;
	private final String expectedChildType;

	public InvalidChildException(IntStream input, String expectedChildType) {
		super(input);
		this.expectedChildType = expectedChildType;
	}

	public String getErrorDescription() {
		String article = startsWithVowel(expectedChildType);
		return String.format("Invalid child. Should be %s %s.", article,
				expectedChildType);
	}

	private String startsWithVowel(String noun) {
		char initial = noun.charAt(0);
		if (initial == 'a' || initial == 'e' || initial == 'i'
				|| initial == 'o' || initial == 'u') {
			return "an";
		} else {
			return "a";
		}
	}

}
