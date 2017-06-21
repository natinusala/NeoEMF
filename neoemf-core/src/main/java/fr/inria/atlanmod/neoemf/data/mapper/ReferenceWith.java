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

package fr.inria.atlanmod.neoemf.data.mapper;

import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.data.structure.SingleFeatureKey;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A {@link ReferenceMapper} that provides a default behavior to use {@link M} instead of {@link Id} for
 * references. This behavior is specified by the {@link #referenceMapping()} method.
 *
 * @param <M> the type of the reference after mapping
 */
@ParametersAreNonnullByDefault
public interface ReferenceWith<M> extends ValueMapper, ReferenceMapper {

    @Nonnull
    @Override
    default Optional<Id> referenceOf(SingleFeatureKey key) {
        MappingFunction<Id, M> func = referenceMapping();

        return this.<M>valueOf(key)
                .map(func::unmap);
    }

    @Nonnull
    @Override
    default Optional<Id> referenceFor(SingleFeatureKey key, Id reference) {
        MappingFunction<Id, M> func = referenceMapping();

        return this.<M>valueFor(key, func.map(reference))
                .map(func::unmap);
    }

    @Override
    default void unsetReference(SingleFeatureKey key) {
        unsetValue(key);
    }

    @Override
    default boolean hasReference(SingleFeatureKey key) {
        return referenceOf(key).isPresent();
    }

    @Nonnull
    MappingFunction<Id, M> referenceMapping();
}