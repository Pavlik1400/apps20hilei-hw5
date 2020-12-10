package ua.edu.ucu.stream;


import ua.edu.ucu.function.IntBinaryOperator;
import ua.edu.ucu.function.IntPredicate;
import ua.edu.ucu.function.IntConsumer;
import ua.edu.ucu.function.IntToIntStreamFunction;
import ua.edu.ucu.function.IntUnaryOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;


public class AsIntStream implements IntStream {
    private final Iterator<Integer> intIter;

    private AsIntStream(Iterator<Integer> iterator) {
        intIter = iterator;
    }

    public static IntStream of(int... values) {
        int[] valuesCopy = Arrays.copyOf(values, values.length);
        return new AsIntStream(new Iterator<Integer>() {
            private int counter = 0;
            @Override
            public boolean hasNext() {
                return counter < valuesCopy.length;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return valuesCopy[counter++];
            }
        });
    }

    @Override
    public double average() {
        int sum = 0;
        int size = 0;
        while (intIter.hasNext()) {
            sum += intIter.next();
            size++;
        }
        return (double) sum / size;
    }

    @Override
    public int max() {
        return reduce((int) Double.NEGATIVE_INFINITY, Math::max);
    }

    @Override
    public int min() {
        return reduce((int) Double.POSITIVE_INFINITY, Math::min);
    }

    @Override
    public long count() {
        return toArray().length;
    }

    @Override
    public int sum() {
        return reduce(0, Integer::sum);
    }

    @Override
    public IntStream filter(IntPredicate predicate) {
        return new AsIntStream(new Iterator<Integer>() {
            private int nextVal;
            @Override
            public boolean hasNext() {
                if (intIter.hasNext()) {
                    nextVal = intIter.next();
                    while (!predicate.test(nextVal)) {
                        if (intIter.hasNext()) {
                            nextVal = intIter.next();
                        } else {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return nextVal;
            }
        });
    }

    @Override
    public void forEach(IntConsumer action) {
        while (intIter.hasNext()) {
            action.accept(intIter.next());
        }
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
        return new AsIntStream(new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return intIter.hasNext();
            }

            @Override
            public Integer next() {
                return mapper.apply(intIter.next());
            }
        });
    }

    @Override
    public IntStream flatMap(IntToIntStreamFunction func) {
        // counter represents number of vales that should be returned from
        // tmpArr if counter == 0 - then we should take next value from
        // original iterator
        return new AsIntStream(new Iterator<Integer>() {
            private int[] tmpArr;   // array, where mapped values are saved.
            private int counter = 0;
            @Override
            public boolean hasNext() {
                if (counter != 0) {
                    return true;
                }
                return intIter.hasNext();
            }

            @Override
            public Integer next() {
                if (counter == 0) {
                    tmpArr = func.applyAsIntStream(intIter.next()).toArray();
                    counter = tmpArr.length;
                }
                return tmpArr[tmpArr.length-(counter--)];
            }
        });
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
        int result = identity;
        while (intIter.hasNext()) {
            result = op.apply(result, intIter.next());
        }
        return result;
    }

    //To change body of generated methods, choose Tools | Templates
    @Override
    public int[] toArray() {
        List<Integer> res = new ArrayList<>();
        while (intIter.hasNext()) {
            res.add(intIter.next());
        }
        int[] finalRes = new int[res.size()];
        for (int i = 0; i < res.size(); i++) {
            finalRes[i] = res.get(i);
        }
        return finalRes;
    }

}
