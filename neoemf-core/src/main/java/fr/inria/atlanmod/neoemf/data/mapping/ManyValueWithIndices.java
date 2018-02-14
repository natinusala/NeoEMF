/*
 * Copyright (c) 2013-2018 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.data.mapping;

import fr.inria.atlanmod.commons.collect.MoreIterables;
import fr.inria.atlanmod.commons.collect.SizedIterator;
import fr.inria.atlanmod.neoemf.data.bean.ManyFeatureBean;
import fr.inria.atlanmod.neoemf.data.bean.SingleFeatureBean;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static fr.inria.atlanmod.commons.Preconditions.checkGreaterThanOrEqualTo;
import static fr.inria.atlanmod.commons.Preconditions.checkNotContainsNull;
import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;
import static fr.inria.atlanmod.commons.Preconditions.checkPositionIndex;

/**
 * A {@link ManyValueMapper} that provides a default behavior to represent the "multi-valued" directly with their
 * position.
 * <p>
 * Indices are persisted with dedicated {@link SingleFeatureBean}s containing the index of the element to store. Using
 * this approach avoid to deserialize entire {@link java.util.Collection}s to retrieve a single element, which can be an
 * important bottleneck in terms of execution time and memory consumption if the underlying model contains very large
 * {@link java.util.Collection}s.
 */
@ParametersAreNonnullByDefault
public interface ManyValueWithIndices extends ManyValueMapper {

    @Nonnull
    @Override
    default <V> Stream<V> allValuesOf(SingleFeatureBean key) {
        final int size = sizeOfValue(key).orElse(0);
        final Iterator<V> iter = new SizedIterator<>(size, i -> this.<V>valueOf(key.withPosition(i)).orElse(null));

        return MoreIterables.stream(() -> iter);
    }

    @Nonnull
    @Override
    default <V> Optional<V> valueFor(ManyFeatureBean key, V value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        Optional<V> previousValue = valueOf(key);
        if (!previousValue.isPresent()) {
            throw new NoSuchElementException();
        }

        innerValueFor(key, value);

        return previousValue;
    }

    @Override
    default <V> void addValue(ManyFeatureBean key, V value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        int size = sizeOfValue(key.withoutPosition()).orElse(0);
        checkPositionIndex(key.position(), size);

        for (int i = size - 1; i >= key.position(); i--) {
            innerValueFor(key.withPosition(i + 1), valueOf(key.withPosition(i)).<IllegalStateException>orElseThrow(IllegalStateException::new));
        }

        sizeForValue(key.withoutPosition(), size + 1);

        innerValueFor(key, value);
    }

    @Override
    default <V> void addAllValues(ManyFeatureBean key, List<? extends V> collection) {
        checkNotNull(key, "key");
        checkNotNull(collection, "collection");
        checkNotContainsNull(collection);

        int firstPosition = key.position();

        IntStream.range(0, collection.size())
                .forEachOrdered(i -> addValue(key.withPosition(firstPosition + i), collection.get(i)));
    }

    @Nonnull
    @Override
    default <V> Optional<V> removeValue(ManyFeatureBean key) {
        checkNotNull(key, "key");

        int size = sizeOfValue(key.withoutPosition()).orElse(0);
        if (size == 0) {
            return Optional.empty();
        }

        Optional<V> previousValue = valueOf(key);

        for (int i = key.position(); i < size - 1; i++) {
            innerValueFor(key.withPosition(i), valueOf(key.withPosition(i + 1)).<IllegalStateException>orElseThrow(IllegalStateException::new));
        }

        innerValueFor(key.withPosition(size - 1), null);

        sizeForValue(key.withoutPosition(), size - 1);

        return previousValue;
    }

    @Override
    default void removeAllValues(SingleFeatureBean key) {
        IntStream.range(0, sizeOfValue(key).orElse(0))
                .forEachOrdered(i -> innerValueFor(key.withPosition(i), null));

        removeValue(key);
    }

    @Nonnull
    @Nonnegative
    @Override
    default Optional<Integer> sizeOfValue(SingleFeatureBean key) {
        checkNotNull(key, "key");

        return this.<Integer>valueOf(key)
                .filter(s -> s > 0);
    }

    /**
     * Defines the number of values of the specified {@code key}.
     *
     * @param key  the key identifying the multi-valued attribute
     * @param size the number of values
     *
     * @throws NullPointerException     if the {@code key} is {@code null}
     * @throws IllegalArgumentException if {@code size < 0}
     */
    default void sizeForValue(SingleFeatureBean key, @Nonnegative int size) {
        checkNotNull(key, "key");
        checkGreaterThanOrEqualTo(size, 0, "size (%d) must not be negative", size);

        if (size > 0) {
            valueFor(key, size);
        }
        else {
            removeValue(key);
        }
    }

    /**
     * Defines the {@code value} of the specified {@code key} at a defined position.
     * <p>
     * This method behaves like: {@link #valueFor(ManyFeatureBean, Object)}, without checking whether the multi-valued
     * feature already exists, in order to replace it. If {@code value == null}, the key is removed.
     *
     * @param key   the key identifying the multi-valued attribute
     * @param value the value to set
     * @param <V>   the type of value
     *
     * @throws NullPointerException if the {@code key} is {@code null}
     */
    <V> void innerValueFor(ManyFeatureBean key, @Nullable V value);
}
