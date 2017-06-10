/**
 */

package graph.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

import graph.Edge;
import graph.Graph;
import graph.GraphPackage;
import graph.Vertice;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 *
 * @generated
 * @see graph.GraphPackage
 */
public class GraphSwitch<T> extends Switch<T> {

    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    protected static GraphPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated
     */
    public GraphSwitch() {
        if (modelPackage == null) {
            modelPackage = GraphPackage.eINSTANCE;
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Graph</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     *
     * @return the result of interpreting the object as an instance of '<em>Graph</em>'.
     *
     * @generated
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     */
    public T caseGraph(Graph object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Vertice</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     *
     * @return the result of interpreting the object as an instance of '<em>Vertice</em>'.
     *
     * @generated
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     */
    public T caseVertice(Vertice object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Edge</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     *
     * @return the result of interpreting the object as an instance of '<em>Edge</em>'.
     *
     * @generated
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     */
    public T caseEdge(Edge object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     *
     * @param object the target of the switch.
     *
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     *
     * @generated
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that
     * result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     *
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case GraphPackage.GRAPH: {
                Graph graph = (Graph) theEObject;
                T result = caseGraph(graph);
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case GraphPackage.VERTICE: {
                Vertice vertice = (Vertice) theEObject;
                T result = caseVertice(vertice);
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            case GraphPackage.EDGE: {
                Edge edge = (Edge) theEObject;
                T result = caseEdge(edge);
                if (result == null) {
                    result = defaultCase(theEObject);
                }
                return result;
            }
            default:
                return defaultCase(theEObject);
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param ePackage the package in question.
     *
     * @return whether this is a switch for the given package.
     *
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

} //GraphSwitch
