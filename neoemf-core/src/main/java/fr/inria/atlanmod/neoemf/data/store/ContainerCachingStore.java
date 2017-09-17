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

import fr.inria.atlanmod.commons.annotation.VisibleForReflection;
import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.data.bean.SingleFeatureBean;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A {@link Store} wrapper that caches containers.
 */
@ParametersAreNonnullByDefault
@SuppressWarnings("unused") // Called dynamically
public class ContainerCachingStore extends AbstractCachingStore<Id, Optional<SingleFeatureBean>> {

    /**
     * Constructs a new {@code ContainerCachingStore} on the given {@code store}.
     *
     * @param store the inner store
     */
    @VisibleForReflection
    protected ContainerCachingStore(Store store) {
        super(store);
    }

    @Nonnull
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Optional<SingleFeatureBean> containerOf(Id id) {
        return cache.get(id, super::containerOf);
    }

    @Override
    public void containerFor(Id id, SingleFeatureBean container) {
        cache.put(id, Optional.of(container));
        super.containerFor(id, container);
    }

    @Override
    public void removeContainer(Id id) {
        cache.put(id, Optional.empty());
        super.removeContainer(id);
    }
}
