/*
 * Copyright (c) 2013-2017 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

package fr.inria.atlanmod.neoemf.data.blueprints.context;

import fr.inria.atlanmod.neoemf.config.Config;
import fr.inria.atlanmod.neoemf.context.AbstractContext;
import fr.inria.atlanmod.neoemf.context.Context;
import fr.inria.atlanmod.neoemf.data.BackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.BlueprintsBackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.config.BlueprintsTinkerConfig;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A specific {@link Context} for the Blueprints implementation.
 */
@ParametersAreNonnullByDefault
public abstract class BlueprintsContext extends AbstractContext {

    /**
     * Creates a new {@code BlueprintsContext} with a mapping with indices.
     *
     * @return a new context.
     */
    @Nonnull
    public static Context getDefault() {
        return new Default();
    }

    @Nonnull
    @Override
    public String name() {
        return "Blueprints";
    }

    @Nonnull
    @Override
    public BackendFactory factory() {
        return BlueprintsBackendFactory.getInstance();
    }

    @ParametersAreNonnullByDefault
    private static class Default extends BlueprintsContext {

        @Nonnull
        @Override
        public String name() {
            return super.name() + "#Tinker";
        }

        @Nonnull
        @Override
        public Config config() {
            return BlueprintsTinkerConfig.newConfig();
        }
    }
}
