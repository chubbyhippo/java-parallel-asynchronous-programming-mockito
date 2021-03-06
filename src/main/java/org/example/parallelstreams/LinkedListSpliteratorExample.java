package org.example.parallelstreams;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.example.util.CommonUtil.*;

public class LinkedListSpliteratorExample {
    public List<Integer> multiplyEachValue(LinkedList<Integer> inputList, int multiplyValue, boolean isParallel) {
        startTimer();
        Stream<Integer> integerStream = inputList.stream();

        if (isParallel) {
            integerStream.parallel();
        }

        List<Integer> resultList = integerStream
                .map(integer -> integer * multiplyValue)
                .toList();

        timeTaken();
        stopWatchReset();
        return resultList;
    }
}
