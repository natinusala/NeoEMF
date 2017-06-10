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

package fr.inria.atlanmod.neoemf.data.store;

import fr.inria.atlanmod.common.log.Log;
import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.data.structure.ClassDescriptor;
import fr.inria.atlanmod.neoemf.data.structure.ContainerDescriptor;
import fr.inria.atlanmod.neoemf.data.structure.FeatureKey;
import fr.inria.atlanmod.neoemf.data.structure.ManyFeatureKey;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A {@link Store} wrapper that count the number elements used.
 */
@ParametersAreNonnullByDefault
public class LoadedObjectCounterStoreDecorator extends AbstractStoreDecorator {

    /**
     * Set that holds loaded objects.
     */
    private final Set<Id> loadedObjects;

    /**
     * Constructs a new {@code LoadedObjectCounterStoreDecorator}.
     *
     * @param store the inner store
     */
    @SuppressWarnings("unused") // Called dynamically
    public LoadedObjectCounterStoreDecorator(Store store) {
        super(store);
        loadedObjects = new TreeSet<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> Log.info("{0} objects loaded during the execution", loadedObjects.size())));
    }

    @Nonnull
    @Override
    public Optional<ContainerDescriptor> containerOf(Id id) {
        register(id);
        return super.containerOf(id);
    }

    @Override
    public void containerFor(Id id, ContainerDescriptor container) {
        register(id);
        super.containerFor(id, container);
    }

    @Override
    public void unsetContainer(Id id) {
        register(id);
        super.unsetContainer(id);
    }

    @Nonnull
    @Override
    public Optional<ClassDescriptor> metaclassOf(Id id) {
        register(id);
        return super.metaclassOf(id);
    }

    @Override
    public void metaclassFor(Id id, ClassDescriptor metaclass) {
        register(id);
        super.metaclassFor(id, metaclass);
    }

    @Nonnull
    @Override
    public <V> Optional<V> valueOf(FeatureKey key) {
        register(key);
        return super.valueOf(key);
    }

    @Nonnull
    @Override
    public <V> Optional<V> valueFor(FeatureKey key, V value) {
        register(key);
        return super.valueFor(key, value);
    }

    @Override
    public <V> void unsetValue(FeatureKey key) {
        register(key);
        super.unsetValue(key);
    }

    @Override
    public <V> boolean hasValue(FeatureKey key) {
        register(key);
        return super.hasValue(key);
    }

    @Nonnull
    @Override
    public Optional<Id> referenceOf(FeatureKey key) {
        register(key);
        return super.referenceOf(key);
    }

    @Nonnull
    @Override
    public Optional<Id> referenceFor(FeatureKey key, Id reference) {
        register(key);
        return super.referenceFor(key, reference);
    }

    @Override
    public void unsetReference(FeatureKey key) {
        register(key);
        super.unsetReference(key);
    }

    @Override
    public boolean hasReference(FeatureKey key) {
        register(key);
        return super.hasReference(key);
    }

    @Nonnull
    @Override
    public <V> Optional<V> valueOf(ManyFeatureKey key) {
        register(key);
        return super.valueOf(key);
    }

    @Nonnull
    @Override
    public <V> List<V> allValuesOf(FeatureKey key) {
        register(key);
        return super.allValuesOf(key);
    }

    @Nonnull
    @Override
    public <V> Optional<V> valueFor(ManyFeatureKey key, V value) {
        register(key);
        return super.valueFor(key, value);
    }

    @Override
    public <V> boolean hasAnyValue(FeatureKey key) {
        register(key);
        return super.hasAnyValue(key);
    }

    @Override
    public <V> void addValue(ManyFeatureKey key, V value) {
        register(key);
        super.addValue(key, value);
    }

    @Nonnegative
    @Override
    public <V> int appendValue(FeatureKey key, V value) {
        register(key);
        return super.appendValue(key, value);
    }

    @Nonnegative
    @Override
    public <V> int appendAllValues(FeatureKey key, List<V> values) {
        register(key);
        return super.appendAllValues(key, values);
    }

    @Nonnull
    @Override
    public <V> Optional<V> removeValue(ManyFeatureKey key) {
        register(key);
        return super.removeValue(key);
    }

    @Override
    public <V> void removeAllValues(FeatureKey key) {
        register(key);
        super.removeAllValues(key);
    }

    @Nonnull
    @Override
    public <V> Optional<V> moveValue(ManyFeatureKey source, ManyFeatureKey target) {
        register(source);
        register(target);
        return super.moveValue(source, target);
    }

    @Override
    public <V> boolean containsValue(FeatureKey key, @Nullable V value) {
        register(key);
        return super.containsValue(key, value);
    }

    @Nonnull
    @Nonnegative
    @Override
    public <V> Optional<Integer> indexOfValue(FeatureKey key, @Nullable V value) {
        register(key);
        return super.indexOfValue(key, value);
    }

    @Nonnull
    @Nonnegative
    @Override
    public <V> Optional<Integer> lastIndexOfValue(FeatureKey key, @Nullable V value) {
        register(key);
        return super.lastIndexOfValue(key, value);
    }

    @Nonnull
    @Nonnegative
    @Override
    public <V> Optional<Integer> sizeOfValue(FeatureKey key) {
        register(key);
        return super.sizeOfValue(key);
    }

    @Nonnull
    @Override
    public Optional<Id> referenceOf(ManyFeatureKey key) {
        register(key);
        return super.referenceOf(key);
    }

    @Nonnull
    @Override
    public List<Id> allReferencesOf(FeatureKey key) {
        register(key);
        return super.allReferencesOf(key);
    }

    @Nonnull
    @Override
    public Optional<Id> referenceFor(ManyFeatureKey key, Id reference) {
        register(key);
        return super.referenceFor(key, reference);
    }

    @Override
    public boolean hasAnyReference(FeatureKey key) {
        register(key);
        return super.hasAnyReference(key);
    }

    @Override
    public void addReference(ManyFeatureKey key, Id reference) {
        register(key);
        super.addReference(key, reference);
    }

    @Nonnegative
    @Override
    public int appendReference(FeatureKey key, Id reference) {
        register(key);
        return super.appendReference(key, reference);
    }

    @Nonnegative
    @Override
    public int appendAllReferences(FeatureKey key, List<Id> references) {
        register(key);
        return super.appendAllReferences(key, references);
    }

    @Nonnull
    @Override
    public Optional<Id> removeReference(ManyFeatureKey key) {
        register(key);
        return super.removeReference(key);
    }

    @Override
    public void removeAllReferences(FeatureKey key) {
        register(key);
        super.removeAllReferences(key);
    }

    @Nonnull
    @Override
    public Optional<Id> moveReference(ManyFeatureKey source, ManyFeatureKey target) {
        register(source);
        register(target);
        return super.moveReference(source, target);
    }

    @Override
    public boolean containsReference(FeatureKey key, @Nullable Id reference) {
        register(key);
        return super.containsReference(key, reference);
    }

    @Nonnull
    @Nonnegative
    @Override
    public Optional<Integer> indexOfReference(FeatureKey key, @Nullable Id reference) {
        register(key);
        return super.indexOfReference(key, reference);
    }

    @Nonnull
    @Nonnegative
    @Override
    public Optional<Integer> lastIndexOfReference(FeatureKey key, @Nullable Id reference) {
        register(key);
        return super.lastIndexOfReference(key, reference);
    }

    @Nonnull
    @Nonnegative
    @Override
    public Optional<Integer> sizeOfReference(FeatureKey key) {
        register(key);
        return super.sizeOfReference(key);
    }

    /**
     * Registers the given {@code key} as loaded.
     *
     * @param key the key from which to extract the identifier
     *
     * @see #register(Id)
     */
    private void register(FeatureKey key) {
        register(key.id());
    }

    /**
     * Registers the given {@code id} as loaded.
     *
     * @param id the identifier to register
     */
    private void register(Id id) {
        loadedObjects.add(id);
    }
}
