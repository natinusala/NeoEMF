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

package fr.inria.atlanmod.neoemf;

import fr.inria.atlanmod.common.annotations.VisibleForReflection;
import fr.inria.atlanmod.common.log.Log;
import fr.inria.atlanmod.neoemf.data.BackendFactory;
import fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry;

import org.eclipse.emf.common.util.URI;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * A {@link BundleActivator} that automatically registers a {@link BackendFactory} with its associated
 * {@link URI URI} in the global {@link BackendFactoryRegistry} when loading an OSGi bundle.
 * <p>
 * <b>Note:</b> This class should not be used in standard use.
 */
@VisibleForReflection
public abstract class AbstractActivator implements BundleActivator {

    /**
     * {@inheritDoc}
     * <p>
     * Registers the {@link BackendFactory} from {@link #factory()}, with its {@link URI URI} scheme from
     * {@link #scheme()}, in the {@link BackendFactoryRegistry registry} if it's not already.
     *
     * @param bundleContext the execution context of the bundle being started
     */
    @Override
    public final void start(BundleContext bundleContext) {
        Log.info("NeoEMF-{0} plugin started", name());
        if (!BackendFactoryRegistry.isRegistered(scheme())) {
            BackendFactoryRegistry.register(scheme(), factory());
            Log.info("{0} backend registered", name());
        }
    }

    @Override
    public final void stop(BundleContext bundleContext) {
        Log.info("NeoEMF-{0} plugin stopped", name());
    }

    /**
     * Returns the name of this bundle.
     *
     * @return the name
     */
    protected abstract String name();

    /**
     * Returns the {@link URI URI} scheme associated with this bundle.
     *
     * @return the {@link URI} scheme
     */
    protected abstract String scheme();

    /**
     * Returns the {@link BackendFactory} associated with this bundle.
     *
     * @return the factory
     */
    protected abstract BackendFactory factory();
}
