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

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import fr.inria.atlanmod.neoemf.core.Id;
import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.data.AbstractPersistenceBackend;
import fr.inria.atlanmod.neoemf.data.PersistenceBackend;
import fr.inria.atlanmod.neoemf.data.berkeleydb.util.serializer.ObjectSerializer;
import fr.inria.atlanmod.neoemf.data.structure.ContainerDescriptor;
import fr.inria.atlanmod.neoemf.data.structure.FeatureKey;
import fr.inria.atlanmod.neoemf.data.structure.MetaclassDescriptor;

import org.eclipse.emf.ecore.EClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ???
 */
@ParametersAreNonnullByDefault
abstract class AbstractBerkeleyDbBackend extends AbstractPersistenceBackend implements BerkeleyDbBackend {

    /**
     * ???
     */
    @Nonnull
    protected final Environment environment;

    /**
     * A persistent map that stores the container of {@link PersistentEObject}, identified by the object {@link Id}.
     */
    @Nonnull
    private final Database containers;

    /**
     * A persistent map that stores the {@link EClass} for {@link PersistentEObject}, identified by the object {@link
     * Id}.
     */
    @Nonnull
    private final Database instances;

    /**
     * A persistent map that stores structural features values for {@link PersistentEObject}, identified by the
     * associated {@link FeatureKey}.
     */
    @Nonnull
    private final Database features;

    /**
     * ???
     */
    private boolean isClosed;

    /**
     * {@link Id} representing the {@link Id} concerned by the last call of {{@link #create(Id)}}.
     * BerkeleyDB doesn't support {@link Id} creation.
     */
    private Id lastId;

    /**
     * ???
     *
     * @param file      ???
     * @param envConfig ???
     * @param dbConfig  ???
     */
    protected AbstractBerkeleyDbBackend(File file, EnvironmentConfig envConfig, DatabaseConfig dbConfig) {
        checkNotNull(file);
        checkNotNull(envConfig);
        checkNotNull(dbConfig);

        environment = new Environment(file, envConfig);

        containers = environment.openDatabase(null, "eContainer", dbConfig);
        instances = environment.openDatabase(null, "neoInstanceOf", dbConfig);
        features = environment.openDatabase(null, "features", dbConfig);

        isClosed = false;
    }

    @Override
    public void save() {
        allDatabases().forEach(Database::sync);
    }

    @Override
    public void close() {
        this.save();

        allDatabases().forEach(Database::close);

        environment.close();
        isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public boolean isDistributed() {
        return false;
    }

    /**
     * Copies all the contents of this {@code PersistenceBackend} to the {@code target} one.
     *
     * @param target the {@code PersistenceBackend} to copy the database contents to
     */
    @Override
    public void copyTo(PersistenceBackend target) {
        checkArgument(target instanceof AbstractBerkeleyDbBackend);
        AbstractBerkeleyDbBackend to = (AbstractBerkeleyDbBackend) target;

        copyDatabaseTo(instances, to.instances);
        copyDatabaseTo(features, to.features);
        copyDatabaseTo(containers, to.containers);
    }

    @Override
    public void create(Id id) {
        lastId = id;
    }

    @Override
    public boolean has(Id id) {
        return (Objects.equals(id, lastId)) || metaclassOf(id).isPresent();
    }

    @Nonnull
    @Override
    public Optional<ContainerDescriptor> containerOf(Id id) {
        return fromDatabase(containers, id);
    }

    @Override
    public void containerFor(Id id, ContainerDescriptor container) {
        toDatabase(containers, id, container);
    }

    @Nonnull
    @Override
    public Optional<MetaclassDescriptor> metaclassOf(Id id) {
        return fromDatabase(instances, id);
    }

    @Override
    public void metaclassFor(Id id, MetaclassDescriptor metaclass) {
        toDatabase(instances, id, metaclass);
    }

    @Nonnull
    @Override
    public <V> Optional<V> valueOf(FeatureKey key) {
        return fromDatabase(features, key);
    }

    @Nonnull
    @Override
    public <V> Optional<V> valueFor(FeatureKey key, V value) {
        Optional<V> previousValue = valueOf(key);

        toDatabase(features, key, value);

        return previousValue;
    }

    @Override
    public void unsetValue(FeatureKey key) {
        outDatabase(features, key);
    }

    @Override
    public boolean hasValue(FeatureKey key) {
        return fromDatabase(features, key).isPresent();
    }

    /**
     * Returns all loaded databases.
     *
     * @return a list of {@link Database}
     */
    @Nonnull
    protected List<Database> allDatabases() {
        List<Database> databases = new ArrayList<>();
        databases.add(containers);
        databases.add(instances);
        databases.add(features);
        return databases;
    }

    /**
     * Retrieves a value from the {@code database} according to the given {@code key}.
     *
     * @param database the database where to looking for
     * @param key      the key of the element to retrieve
     * @param <K>      the type of the key
     * @param <V>      the type of the value
     *
     * @return on {@link Optional} containing the element, or an empty {@link Optional} if the element has not been
     * found
     */
    @Nonnull
    protected <K, V> Optional<V> fromDatabase(Database database, K key) {
        DatabaseEntry dbKey = new DatabaseEntry(ObjectSerializer.serialize(key));
        DatabaseEntry dbValue = new DatabaseEntry();

        Optional<V> value;
        if (database.get(null, dbKey, dbValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
            value = Optional.of(ObjectSerializer.deserialize(dbValue.getData()));
        }
        else {
            value = Optional.empty();
        }
        return value;
    }

    /**
     * Saves a {@code value} identified by the {@code key} in the {@code database}.
     *
     * @param database the database where to save the value
     * @param key      the key of the element to save
     * @param value    the value to save
     * @param <K>      the type of the key
     * @param <V>      the type of the value
     */
    protected <K, V> void toDatabase(Database database, K key, V value) {
        DatabaseEntry dbKey = new DatabaseEntry(ObjectSerializer.serialize(key));
        DatabaseEntry dbValue = new DatabaseEntry(ObjectSerializer.serialize(value));

        database.put(null, dbKey, dbValue);
    }

    /**
     * Removes a value from the {@code database} according to its {@code key}.
     *
     * @param database the database where to remove the value
     * @param key      the key of the element to remove
     * @param <K>      the type of the key
     */
    protected <K> void outDatabase(Database database, K key) {
        DatabaseEntry dbKey = new DatabaseEntry(ObjectSerializer.serialize(key));

        database.delete(null, dbKey);
    }

    /**
     * Utility method to copy the contents from one database to another.
     *
     * @param from the database to copy
     * @param to   the database to copy the database contents to
     */
    protected void copyDatabaseTo(Database from, Database to) {
        try (Cursor cursor = from.openCursor(null, null)) {
            DatabaseEntry dbKey = new DatabaseEntry();
            DatabaseEntry dbValue = new DatabaseEntry();
            while (cursor.getNext(dbKey, dbValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                to.put(null, dbKey, dbValue);
            }
        }
        to.sync();
    }
}