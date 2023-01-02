package kaba4cow.engine.fontMeshCreator;

import java.util.ArrayList;
import java.util.List;

public class Line {

	private float maxLength;
	private float spaceSize;

	private List<Word> words = new ArrayList<Word>();
	private float currentLineLength = 0;

	protected Line(float spaceWidth, float fontSize, float maxLength) {
		this.spaceSize = spaceWidth * fontSize;
		this.maxLength = maxLength;
	}

	protected boolean attemptToAddWord(Word word) {
		float additionalLength = word.getWordWidth();
		additionalLength += words.isEmpty() ? 0f : spaceSize;
		if (currentLineLength + additionalLength <= maxLength) {
			words.add(word);
			currentLineLength += additionalLength;
			return true;
		} else
			return false;
	}

	protected float getMaxLength() {
		return maxLength;
	}

	protected float getLineLength() {
		return currentLineLength;
	}

	protected List<Word> getWords() {
		return words;
	}

}
