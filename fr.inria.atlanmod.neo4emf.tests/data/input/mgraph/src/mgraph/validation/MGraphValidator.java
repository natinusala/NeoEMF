/**
 *
 * $Id$
 */
package mgraph.validation;

import mgraph.MEdge;
import mgraph.MNode;

import org.eclipse.emf.common.util.EList;

/**
 * A sample validator interface for {@link mgraph.MGraph}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface MGraphValidator {
	boolean validate();

	boolean validateName(String value);
	boolean validateNodes(EList<MNode> value);
	boolean validateEdges(EList<MEdge> value);
}
