package com.lututui.diariodehumor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedArrayList<T> extends ArrayList<T> {
    private Comparator<T> comparator;

    public SortedArrayList(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;

        this.sort(this.comparator);
    }

    public int findInsertPosition(T newItem) {
        int index = Collections.binarySearch(this, newItem, comparator);

        if (index < 0) return -(index + 1);

        return index;
    }

    public int addSorted(T newItem) {
        int index = findInsertPosition(newItem);

        this.add(index, newItem);

        return index;
    }

    @Override
    @Deprecated
    public boolean add(T t) {
        this.addSorted(t);

        return true;
    }
}
