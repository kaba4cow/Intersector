package kaba4cow.engine.toolbox;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class VersionComparator implements Comparator<String> {
	
	public static final VersionComparator instance = new VersionComparator();

    @Override
    public int compare(String version1, String version2) {
        List<String> firstVersionElements = Arrays.asList(version1.split("\\."));
        List<String> secondVersionElements = Arrays.asList(version2.split("\\."));
        int maxVersionElements = getMaxNumber(firstVersionElements.size(), secondVersionElements.size(), 0);
        for (int counter = 0; counter < maxVersionElements; counter++) {
            if (firstVersionElements.size() == counter && secondVersionElements.size() == counter)
                return 0;
            if (firstVersionElements.size() == counter)
                return -1;
            if (secondVersionElements.size() == counter)
                return 1;
            int firstIntElement = Integer.valueOf(firstVersionElements.get(counter));
            int secondIntElement = Integer.valueOf(secondVersionElements.get(counter));
            if (firstIntElement < secondIntElement)
                return -1;
            if (firstIntElement > secondIntElement)
                return 1;
        }
        return 0;
    }
    
    private int getMaxNumber(int... ints) {
        int maximumInt = ints[0];
        for (int processInt : ints)
            if (processInt > maximumInt)
                maximumInt = processInt;
        return maximumInt;
    }

}
