package ua.edu.ucu.stream;

import ua.edu.ucu.function.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AsIntStream implements IntStream {
//    private int[] container;
    Iterator<Integer> integerIterator;

    private AsIntStream(Iterator<Integer> iterator) {
        integerIterator = iterator;

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
                return valuesCopy[counter++];
            }
        });
    }

    @Override
    public Double average() {
        int sum = 0;
        int size = 0;
        while (integerIterator.hasNext()) {
            sum += integerIterator.next();
            size++;
        }
        return (double) sum / size;
    }

    @Override
    public Integer max() {
        return reduce((int) Double.NEGATIVE_INFINITY, Math::max);
    }

    @Override
    public Integer min() {
        return reduce((int) Double.POSITIVE_INFINITY, Math::min);
    }

    @Override
    public long count() {
        return toArray().length;
    }

    @Override
    public Integer sum() {
        return reduce(0, Integer::sum);
    }

    @Override
    public IntStream filter(IntPredicate predicate) {
        return new AsIntStream(new Iterator<Integer>() {
            boolean nextValIsActual = false;
            int nextVal;
            @Override
            public boolean hasNext() {
                if (nextValIsActual) {
                    return true;
                }
                if (integerIterator.hasNext()) {
                    int val = integerIterator.next();
                    while (!predicate.test(val)) {
                        if (integerIterator.hasNext()) {
                            val = integerIterator.next();
                        } else {
                            return false;
                        }
                    }
                    nextVal = val;
                    nextValIsActual = true;
                    return true;
                }
                return false;
            }

            @Override
            public Integer next() {
                if (!nextValIsActual) {
                    hasNext();
                }
                nextValIsActual = false;
                return nextVal;
            };
        });
    }

    @Override
    public void forEach(IntConsumer action) {
        while (integerIterator.hasNext()) {
            action.accept(integerIterator.next());
        }
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
        return new AsIntStream(new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return integerIterator.hasNext();
            }

            @Override
            public Integer next() {
                return mapper.apply(integerIterator.next());
            }
        });
    }

    @Override
    public IntStream flatMap(IntToIntStreamFunction func) {
        return new AsIntStream(new Iterator<Integer>() {
            private int[] tmpArr;   // array, where mapped values are saved.
            private int counter = 0;    // represents number of vales that should be returned from tmpArr
                                        // if counter == 0 - then we should take next value from original
                                        // iterator
            @Override
            public boolean hasNext() {
                if (counter != 0) {
                    return true;
                }
                return integerIterator.hasNext();
            }

            @Override
            public Integer next() {
                if (counter == 0) {
                    tmpArr = func.applyAsIntStream(integerIterator.next()).toArray();
                    counter = tmpArr.length;
                }
                return tmpArr[tmpArr.length-(counter--)];
            }
        });
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
        int result = identity;
        while (integerIterator.hasNext()) {
            result = op.apply(result, integerIterator.next());
        }
        return result;
    }

    //To change body of generated methods, choose Tools | Templates
    @Override
    public int[] toArray() {
        List<Integer> res = new ArrayList<>();
        while (integerIterator.hasNext()) {
            res.add(integerIterator.next());
        }
        int[] finalRes = new int[res.size()];
        for (int i = 0; i < res.size(); i++) {
            finalRes[i] = res.get(i);
        }
        return finalRes;
    }

}
