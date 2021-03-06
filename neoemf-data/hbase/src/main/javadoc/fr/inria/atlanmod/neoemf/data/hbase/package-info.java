/*
 * Copyright (c) 2013-2017 Atlanmod, Inria, LS2N, and IMT Nantes.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which accompanies
 * this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
 */

/**
 * Provides classes related to data management specific to a HBase implementation.
 * <p>
 * This package provides {@link fr.inria.atlanmod.neoemf.data.hbase.HBaseBackend} and {@link
 * fr.inria.atlanmod.neoemf.data.hbase.HBaseBackendFactory} classes, that correspond to HBase specific implementations
 * of {@link fr.inria.atlanmod.neoemf.data.Backend} and {@link fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry}.
 * <p>
 * The {@link fr.inria.atlanmod.neoemf.data.hbase.HBaseBackendFactory} has to be registered to enable {@link
 * fr.inria.atlanmod.neoemf.resource.PersistentResource} creation with a HBase storage:
 * <pre>{@code
 * BackendFactoryRegistry.register(new HBaseBackendFactory());
 * }</pre>
 */

package fr.inria.atlanmod.neoemf.data.hbase;