/*
 * Copyright (c) 2013-2017 Atlanmod INRIA LINA Mines Nantes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlanmod INRIA LINA Mines Nantes - initial API and implementation
 */

package fr.inria.atlanmod.neoemf.data.berkeleydb;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.EnvironmentConfig;

import fr.inria.atlanmod.neoemf.data.PersistenceBackendFactory;
import fr.inria.atlanmod.neoemf.data.structure.FeatureKey;
import fr.inria.atlanmod.neoemf.data.structure.MultiFeatureKey;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalInt;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.isNull;

/**
 * ???
 */
@ParametersAreNonnullByDefault
class BerkeleyDbBackendArrays extends AbstractBerkeleyDbBackend {

    /**
     * Constructs a new {@code BerkeleyDbBackendIndices} on the given {@code file} with the given
     * {@code envConfig}.
     *
     * @param file      ???
     * @param envConfig ???
     * @param dbConfig  ???
     *
     * @note This constructor is protected. To create a new {@code BerkeleyDbBackendArrays} use {@link
     * PersistenceBackendFactory#createPersistentBackend(org.eclipse.emf.common.util.URI, Map)}.
     */
    protected BerkeleyDbBackendArrays(File file, EnvironmentConfig envConfig, DatabaseConfig dbConfig) {
        super(file, envConfig, dbConfig);
    }

    @Nonnull
    @Override
    public <V> Optional<V> valueOf(MultiFeatureKey key) {
        return this.<V[]>valueOf(key.withoutPosition())
                .map(ts -> ts[key.position()]);
    }

    @Nonnull
    @Override
    public <V> Iterable<V> allValuesOf(FeatureKey key) {
        V[] values = this.<V[]>valueOf(key)
                .<NoSuchElementException>orElseThrow(NoSuchElementException::new);

        return Arrays.asList(values);
    }

    @Nonnull
    @Override
    public <V> Optional<V> valueFor(MultiFeatureKey key, V value) {
        V[] values = this.<V[]>valueOf(key.withoutPosition())
                .<NoSuchElementException>orElseThrow(NoSuchElementException::new);

        Optional<V> previousValue = Optional.of(values[key.position()]);

        values[key.position()] = value;

        valueFor(key.withoutPosition(), values);

        return previousValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void addValue(MultiFeatureKey key, V value) {
        V[] values = this.<V[]>valueOf(key.withoutPosition())
                .orElse((V[]) new Object[0]);

        while (key.position() > values.length) {
            values = ArrayUtils.add(values, values.length, null);
        }

        if (key.position() < values.length && isNull(values[key.position()])) {
            values[key.position()] = value;
        }
        else {
            values = ArrayUtils.add(values, key.position(), value);
        }

        valueFor(key.withoutPosition(), values);
    }

    @Nonnull
    @Override
    public <V> Optional<V> removeValue(MultiFeatureKey key) {
        V[] values = this.<V[]>valueOf(key.withoutPosition())
                .<NoSuchElementException>orElseThrow(NoSuchElementException::new);

        Optional<V> previousValue = Optional.of(values[key.position()]);

        valueFor(key.withoutPosition(), ArrayUtils.remove(values, key.position()));

        return previousValue;
    }

    @Override
    public <V> boolean containsValue(FeatureKey key, V value) {
        return this.<V[]>valueOf(key)
                .map(ts -> ArrayUtils.contains(ts, value))
                .orElse(false);
    }

    @Nonnull
    @Override
    public <V> OptionalInt indexOfValue(FeatureKey key, V value) {
        return this.<V[]>valueOf(key)
                .map(ts -> {
                    int index = ArrayUtils.indexOf(ts, value);
                    return index == -1 ? OptionalInt.empty() : OptionalInt.of(index);
                })
                .orElse(OptionalInt.empty());
    }

    @Nonnull
    @Override
    public <V> OptionalInt lastIndexOfValue(FeatureKey key, V value) {
        return this.<V[]>valueOf(key)
                .map(ts -> {
                    int index = ArrayUtils.lastIndexOf(ts, value);
                    return index == -1 ? OptionalInt.empty() : OptionalInt.of(index);
                })
                .orElse(OptionalInt.empty());
    }

    @Nonnull
    @Override
    public <V> OptionalInt sizeOfValue(FeatureKey key) {
        return this.<V[]>valueOf(key)
                .map(ts -> OptionalInt.of(ts.length))
                .orElse(OptionalInt.empty());
    }
}