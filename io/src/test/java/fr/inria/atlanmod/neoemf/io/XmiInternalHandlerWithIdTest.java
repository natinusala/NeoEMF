/*
 * Copyright (c) 2013 Atlanmod INRIA LINA Mines Nantes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlanmod INRIA LINA Mines Nantes - initial API and implementation
 */

package fr.inria.atlanmod.neoemf.io;

import fr.inria.atlanmod.neoemf.io.beans.Attribute;
import fr.inria.atlanmod.neoemf.io.beans.Namespace;
import fr.inria.atlanmod.neoemf.io.beans.Reference;
import fr.inria.atlanmod.neoemf.io.mock.beans.ClassifierMock;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsNot.not;

/**
 *
 */
public class XmiInternalHandlerWithIdTest extends AllXmiInternalHandlerTest {

    @Override
    @Before
    public void setUp() throws Exception {
        registerEPackageFromEcore("uml");
        this.sample = "/xmi/sampleWithId.xmi";

        super.setUp();
    }

    /**
     * Check that the elements (and the 'xmi:id' attribute) and their children are properly read.
     *
     */
    @Test
    public void testElementsAndChildrenWithId() throws Exception {
        assertThat(persistanceHandler.getElements(), not(empty()));

        ClassifierMock mock;
        ClassifierMock mockChild;

        ClassifierMock root = persistanceHandler.getElements().get(0);
        assertValidElement(root, "Model", 5, "themodel", "Model", true); // Assert that 'xmi' elements don't exist
        {
            //@Model/@packagedElement.0
            mock = ClassifierMock.getChildFrom(root, 0);
            assertValidElement(mock, "packagedElement", 4, "0x81_22", "Package", false);
            {
                //@Model/@packagedElement.0/@packagedElement.0/@ownedAttribute
                mockChild = ClassifierMock.getChildFrom(mock, 0, 4);
                assertValidElement(mockChild, "ownedAttribute", 0, "0x1f402_1", "Property", false);

                //@Model/@packagedElement.0/@packagedElement.3
                mock = ClassifierMock.getChildFrom(mock, 3);
                assertValidElement(mock, "packagedElement", 3, "COLLABORATION_0x1f402_12", "Collaboration", false);
                {
                    //@Model/@packagedElement.0/@packagedElement.3/ownedBehavior.0
                    mock = ClassifierMock.getChildFrom(mock, 0);
                    assertValidElement(mock, "ownedBehavior", 6, "INTERACTION_0x1f402_12", "Interaction", false);
                    {
                        //@Model/@packagedElement.0/@packagedElement.3/@ownedBehavior.0/@fragment/@operand
                        mockChild = ClassifierMock.getChildFrom(mock, 3, 0);
                        assertValidElement(mockChild, "operand", 5, "OPERAND1_0x1f402_12", "InteractionOperand", false);

                        //@Model/@packagedElement.0/@packagedElement.3/@ownedBehavior.0/@message.0
                        mockChild = ClassifierMock.getChildFrom(mock, 4);
                        assertValidElement(mockChild, "message", 0, "MSG2_0x1f402_12", "Message", false);
                    }
                }
            }
            //@Model/@packagedElement.2
            mock = ClassifierMock.getChildFrom(root, 2);
            assertValidElement(mock, "packagedElement", 0, "RECOPEREVT1_0x81_22", "ReceiveOperationEvent", false);
        }
    }

    /**
     * Check that the attributes are properly read.
     * Most references are recognized as attributes, until the next step...
     */
    @Test
    public void testAttributesWithId() throws Exception {
        ClassifierMock mock;
        ClassifierMock mockChild;

        List<Attribute> attributeList;

        ClassifierMock root = persistanceHandler.getElements().get(0);
        attributeList = root.getAttributes();
        assertThat(attributeList, hasSize(1));
        assertValidAttribute(attributeList.get(0), "name", "jbk");
        {
            //@Model/@packagedElement.0
            mock = ClassifierMock.getChildFrom(root, 0);
            attributeList = mock.getAttributes();
            assertThat(attributeList, hasSize(1));
            {
                //@Model/@packagedElement.0/@packagedElement.0/@ownedAttribute
                mockChild = ClassifierMock.getChildFrom(mock, 0, 4);
                attributeList = mockChild.getAttributes();
                assertThat(attributeList, hasSize(2));
                assertValidAttribute(attributeList.get(1), "visibility", "private");

                //@Model/@packagedElement.0/@packagedElement.3
                mock = ClassifierMock.getChildFrom(mock, 3);
                attributeList = mock.getAttributes();
                assertThat(attributeList, hasSize(1));
                assertValidAttribute(attributeList.get(0), "name", "machine");
                {
                    //@Model/@packagedElement.0/@packagedElement.3/ownedBehavior.0
                    mock = ClassifierMock.getChildFrom(mock, 0);
                    attributeList = mock.getAttributes();
                    assertThat(attributeList, hasSize(1));
                    {
                        //@Model/@packagedElement.0/@packagedElement.3/@ownedBehavior.0/@fragment/@operand
                        mockChild = ClassifierMock.getChildFrom(mock, 3, 0);
                        assertThat(mockChild.getAttributes(), empty());

                        //@Model/@packagedElement.0/@packagedElement.3/@ownedBehavior.0/@message.0
                        mockChild = ClassifierMock.getChildFrom(mock, 4);
                        attributeList = mockChild.getAttributes();
                        assertThat(attributeList, hasSize(2));
                        assertValidAttribute(attributeList.get(0), "name", "answer");
                        assertValidAttribute(attributeList.get(1), "messageSort", "synchCall");
                    }
                }
            }
            //@Model/@packagedElement.2
            mock = ClassifierMock.getChildFrom(root, 2);
            attributeList = mock.getAttributes();
            assertThat(attributeList, hasSize(1));
            assertValidAttribute(attributeList.get(0), "name", "answer");
        }
    }

    /**
     * Check that the 'xmi:idref' references are properly read.
     * Most are not recognized as references yet
     */
    @Test
    public void testReferencesWithId() throws Exception {
        ClassifierMock mock;
        ClassifierMock mockChild;

        List<Reference> referenceList;

        ClassifierMock root = persistanceHandler.getElements().get(0);
        referenceList = root.getReferences();
        assertThat(referenceList, hasSize(5)); // Now contains 'containment'
        assertValidReference(referenceList.get(0), "packagedElement", UNKNOWN_INDEX, "0x81_22", true, null);
        assertValidReference(referenceList.get(2), "packagedElement", UNKNOWN_INDEX, "RECOPEREVT1_0x81_22", true, null);
        {
            //@Model/@packagedElement.0
            mock = ClassifierMock.getChildFrom(root, 0);
            referenceList = mock.getReferences();
            assertThat(referenceList, hasSize(4)); // Now contains 'containment'
            assertValidReference(referenceList.get(1), "packagedElement", UNKNOWN_INDEX, "0x1f582_4", true, null);
            {
                //@Model/@packagedElement.0/@packagedElement.0/@ownedAttribute
                mockChild = ClassifierMock.getChildFrom(mock, 0, 4);
                referenceList = mockChild.getReferences();
                assertThat(referenceList, hasSize(1));
                assertValidReference(referenceList.get(0), "type", UNKNOWN_INDEX, "0x1f582_4", false, null);

                //@Model/@packagedElement.0/@packagedElement.3
                mock = ClassifierMock.getChildFrom(mock, 3);
                referenceList = mock.getReferences();
                assertThat(referenceList, hasSize(3)); // Now contains 'containment'
                assertValidReference(referenceList.get(0), "ownedBehavior", UNKNOWN_INDEX, "INTERACTION_0x1f402_12", true, null);
                {
                    //@Model/@packagedElement.0/@packagedElement.3/ownedBehavior.0
                    mock = ClassifierMock.getChildFrom(mock, 0);
                    referenceList = mock.getReferences();
                    assertThat(referenceList, hasSize(6)); // Now contains 'containment'UNKNOWN_INDEX, "LIFELINE1_0x1f402_12", true, "interaction");
                    assertValidReference(referenceList.get(4), "message", UNKNOWN_INDEX, "MSG2_0x1f402_12", true, "interaction");
                    {
                        //@Model/@packagedElement.0/@packagedElement.3/@ownedBehavior.0/@fragment/@operand
                        mockChild = ClassifierMock.getChildFrom(mock, 3, 0);
                        referenceList = mockChild.getReferences();
                        assertThat(referenceList, hasSize(5)); // Now contains 'containment'
                        assertValidReference(referenceList.get(2), "fragment", UNKNOWN_INDEX, "BEHEXECSPEC2_0x1f402_12", true, "enclosingOperand");

                        //@Model/@packagedElement.0/@packagedElement.3/@ownedBehavior.0/@message.0
                        mockChild = ClassifierMock.getChildFrom(mock, 4);
                        referenceList = mockChild.getReferences();
                        assertThat(referenceList, hasSize(3));
                        assertValidReference(referenceList.get(0), "sendEvent", 0, "MSGOCCSPECSEND2_0x1f402_12", false, null); // New reference
                        assertValidReference(referenceList.get(1), "receiveEvent", 0, "MSGOCCSPECREC2_0x1f402_12", false, null); // New reference
                        assertValidReference(referenceList.get(2), "connector", 0, "CONNECTOR1_2_0x1f402_12", false, null); // New reference
                    }
                }
            }

            //@Model/@packagedElement.2
            mock = ClassifierMock.getChildFrom(root, 2);
            referenceList = mock.getReferences();
            assertThat(referenceList, hasSize(1));
            assertValidReference(referenceList.get(0), "operation", 0, "0x1f582_2", false, null); // New reference
        }
    }

    /**
     * Check that the metaclasses ('xsi:type' or 'xmi:type') are properly read.
     */
    @Test
    public void testMetaClassesWithId() throws Exception {
        ClassifierMock mock;
        ClassifierMock mockChild;

        ClassifierMock root = persistanceHandler.getElements().get(0);
        Namespace ns = root.getNamespace();
        assertValidMetaClass(root.getMetaClassifier(), "Model", ns);
        {
            //@Model/@packagedElement.0
            mock = ClassifierMock.getChildFrom(root, 0);
            assertValidMetaClass(mock.getMetaClassifier(), "Package", ns);
            {
                //@Model/@packagedElement.0/@packagedElement.0/@ownedAttribute
                mockChild = ClassifierMock.getChildFrom(mock, 0, 4);
                assertValidMetaClass(mockChild.getMetaClassifier(), "Property", ns);

                //@Model/@packagedElement.0/@packagedElement.3
                mock = ClassifierMock.getChildFrom(mock, 3);
                assertValidMetaClass(mock.getMetaClassifier(), "Collaboration", ns);
                {
                    //@Model/@packagedElement.0/@packagedElement.3/ownedBehavior.0
                    mock = ClassifierMock.getChildFrom(mock, 0);
                    assertValidMetaClass(mock.getMetaClassifier(), "Interaction", ns);
                    {
                        //@Model/@packagedElement.0/@packagedElement.3/@ownedBehavior.0/@fragment/@operand
                        mockChild = ClassifierMock.getChildFrom(mock, 3, 0);
                        assertValidMetaClass(mockChild.getMetaClassifier(), "InteractionOperand", ns);

                        //@Model/@packagedElement.0/@packagedElement.3/@ownedBehavior.0/@message.0
                        mockChild = ClassifierMock.getChildFrom(mock, 4);
                        assertValidMetaClass(mockChild.getMetaClassifier(), "Message", ns);
                    }
                }
            }
            //@Model/@packagedElement.2
            mock = ClassifierMock.getChildFrom(root, 2);
            assertValidMetaClass(mock.getMetaClassifier(), "ReceiveOperationEvent", ns);
        }
    }
}
