/*
 * Copyright (c) 2013-2018 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.data.store.adapter;

import fr.inria.atlanmod.commons.function.Converter;
import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.core.PersistentEObject;

import org.eclipse.emf.ecore.EReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.isNull;

/**
 * A {@link Converter} that transforms the value of {@link EReference} instances.
 */
@ParametersAreNonnullByDefault
public class ReferenceConverter implements Converter<PersistentEObject, Id> {

    /**
     * The store associated to this converter.
     */
    @Nonnull
    private final StoreAdapter store;

    /**
     * Constructs a new {@code ReferenceConverter} for the given {@code store}.
     *
     * @param store the store associated to this converter
     */
    public ReferenceConverter(StoreAdapter store) {
        this.store = store;
    }

    @Override
    public Id convert(@Nullable PersistentEObject object) {
        if (isNull(object)) {
            return null;
        }

        return object.id();
    }

    @Override
    public PersistentEObject revert(@Nullable Id id) {
        if (isNull(id)) {
            return null;
        }

        return store.resolve(id);

    }
}