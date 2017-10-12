/*
 * Copyright (c) 2013-2017 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.data.mapdb;

import fr.inria.atlanmod.neoemf.config.ImmutableConfig;
import fr.inria.atlanmod.neoemf.context.Context;
import fr.inria.atlanmod.neoemf.data.AbstractBackendFactoryTest;
import fr.inria.atlanmod.neoemf.data.Backend;
import fr.inria.atlanmod.neoemf.data.mapdb.config.MapDbConfig;
import fr.inria.atlanmod.neoemf.data.mapdb.context.MapDbContext;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test-case about {@link MapDbBackendFactory}.
 */
@ParametersAreNonnullByDefault
class MapDbBackendFactoryTest extends AbstractBackendFactoryTest {

    @Nonnull
    @Override
    protected Context context() {
        return MapDbContext.getWithIndices();
    }

    @Override
    public void testCreateTransientBackend() {
        Backend backend = context().factory().createTransientBackend();
        assertThat(backend).isInstanceOf(MapDbBackend.class);
    }

    @Override
    public void testCreateDefaultPersistentBackend() throws IOException {
        ImmutableConfig config = MapDbConfig.newConfig().withIndices();

        Backend backend = context().factory().createPersistentBackend(context().createUri(currentTempFile()), config);
        assertThat(backend).isInstanceOf(MapDbBackendIndices.class);
    }

    @Override
    public void testCopyBackend() throws IOException {
        ImmutableConfig config = MapDbConfig.newConfig().withIndices();

        Backend transientBackend = context().factory().createTransientBackend();
        assertThat(transientBackend).isInstanceOf(MapDbBackend.class);

        Backend persistentBackend = context().factory().createPersistentBackend(context().createUri(currentTempFile()), config);
        assertThat(persistentBackend).isInstanceOf(MapDbBackend.class);

        transientBackend.copyTo(persistentBackend);
    }

    /**
     * Checks the creation of a {@link fr.inria.atlanmod.neoemf.data.PersistentBackend}, specific for MapDB.
     * <p>
     * The mapping {@code indices} is declared explicitly.
     */
    @Test
    void testCreateIndicesPersistentBackend() throws IOException {
        ImmutableConfig config = MapDbConfig.newConfig().withIndices();

        Backend backend = context().factory().createPersistentBackend(context().createUri(currentTempFile()), config);
        assertThat(backend).isInstanceOf(MapDbBackendIndices.class);
    }

    /**
     * Checks the creation of a {@link fr.inria.atlanmod.neoemf.data.PersistentBackend}, specific for MapDB.
     * <p>
     * The mapping {@code arrays} is declared explicitly.
     */
    @Test
    void testCreateArraysPersistentBackend() throws IOException {
        ImmutableConfig config = MapDbConfig.newConfig().withArrays();

        Backend backend = context().factory().createPersistentBackend(context().createUri(currentTempFile()), config);
        assertThat(backend).isInstanceOf(MapDbBackendArrays.class);
    }

    /**
     * Checks the creation of a {@link fr.inria.atlanmod.neoemf.data.PersistentBackend}, specific for MapDB.
     * <p>
     * The mapping {@code lists} is declared explicitly.
     */
    @Test
    void testCreateListsPersistentBackend() throws IOException {
        ImmutableConfig config = MapDbConfig.newConfig().withLists();

        Backend backend = context().factory().createPersistentBackend(context().createUri(currentTempFile()), config);
        assertThat(backend).isInstanceOf(MapDbBackendLists.class);
    }
}
