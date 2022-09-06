package reactive.duality;

import java.util.Iterator;

public class IterableCustom {
    public static void main(String[] args) {
        Iterable<Integer> iter = () -> new Iterator<>() {
            final int MAX = 10;
            int cursor = 0;
            @Override public boolean hasNext() {
                return cursor < MAX;
            }
            @Override public Integer next() {
                return ++cursor; }
        };

        for (Integer integer : iter) {
            System.out.println(integer);
        }
    }
}