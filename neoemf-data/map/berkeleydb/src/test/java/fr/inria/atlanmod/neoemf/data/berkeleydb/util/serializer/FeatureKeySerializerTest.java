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

package fr.inria.atlanmod.neoemf.data.berkeleydb.util.serializer;

import fr.inria.atlanmod.neoemf.core.StringId;
import fr.inria.atlanmod.neoemf.data.structure.FeatureKey;
import fr.inria.atlanmod.neoemf.data.structure.MultivaluedFeatureKey;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class FeatureKeySerializerTest {

    @Test
    public void testSerialize() {
        FeatureKey fkIn = FeatureKey.of(StringId.of("obj1"), "feature");
        FeatureKey fkOut = Serializer.deserialize(Serializer.serialize(fkIn));

        assertThat(fkIn).isEqualTo(fkOut);
    }

    @Test
    public void testSerializeMFK() {
        MultivaluedFeatureKey fkIn = FeatureKey.of(StringId.of("obj1"), "feature").withPosition(0);
        MultivaluedFeatureKey fkOut = Serializer.deserialize(Serializer.serialize(fkIn));

        assertThat(fkIn).isEqualTo(fkOut);
    }

    @Test
    public void testSerializeMFKBis() {
        FeatureKey fk = FeatureKey.of(StringId.of("obj1"), "feature");
        MultivaluedFeatureKey fk1 = fk.withPosition(0);
        MultivaluedFeatureKey fk2 = fk.withPosition(1);

        byte[] ser1 = Serializer.serialize(fk1);
        byte[] ser2 = Serializer.serialize(fk2);
        byte[] ser3 = Serializer.serialize(fk);


        assertThat(Arrays.equals(ser1, ser2)).isFalse();
        assertThat(Arrays.equals(ser1, ser3)).isFalse();
        assertThat(Arrays.equals(ser2, ser3)).isFalse();
    }
}