package entity;

import java.util.Arrays;

final class DialogueComponent {
    private final String[][] lines;
    private int currentSet = 0;

    DialogueComponent(int setCount, int lineCount) {
        lines = new String[Math.max(1, setCount)][Math.max(1, lineCount)];
    }

    void defineLine(int setIndex, int lineIndex, String text) {
        validate(setIndex, lineIndex);
        lines[setIndex][lineIndex] = text;
    }

    void chooseSet(int setIndex) {
        if (setIndex < 0 || setIndex >= lines.length) {
            throw new IndexOutOfBoundsException("Invalid dialogue set: " + setIndex);
        }
        currentSet = setIndex;
    }

    String[] getCurrentSet() {
        return Arrays.copyOf(lines[currentSet], lines[currentSet].length);
    }

    private void validate(int setIndex, int lineIndex) {
        if (setIndex < 0 || setIndex >= lines.length) {
            throw new IndexOutOfBoundsException("Invalid dialogue set: " + setIndex);
        }
        if (lineIndex < 0 || lineIndex >= lines[setIndex].length) {
            throw new IndexOutOfBoundsException("Invalid dialogue line: " + lineIndex);
        }
    }
}
