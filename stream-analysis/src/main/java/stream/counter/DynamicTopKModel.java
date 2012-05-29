package stream.counter;

import java.util.Collection;

/**
 * <p>
 * Interface which extends the {@link Counter} interface by an
 * output method. This output method is intended for all stream counting
 * algorithms which return a collection of TopK elements counted in the stream
 * </p>
 * <p>There are two versions of this interface, {@link DynamicTopKModel} provides
 * a method where the parameter <code>k</code> has to be provided on method invokation
 * while {@link StaticTopKModel} requires this parameter on object instantiation.</p>
 * 
 * @author Marcin Skirzynski, Benedikt Kulmann
 *
 * @param <T> Generic class of the elements which should be counted
 */
public interface DynamicTopKModel<T> extends Counter<T> {

    /**
     * <p>Returns a collection with all Top K Elements. </p>
     *
     * @param k The number of elements to be returned
     * @return all Elements which fit the Top K condition.
     */
    public Collection<T> getTopK(int k);
}
