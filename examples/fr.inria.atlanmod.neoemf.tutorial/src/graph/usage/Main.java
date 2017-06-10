package graph.usage;

import fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry;
import fr.inria.atlanmod.neoemf.data.blueprints.BlueprintsBackendFactory;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.option.BlueprintsNeo4jOptions;
import fr.inria.atlanmod.neoemf.data.blueprints.util.BlueprintsURI;
import fr.inria.atlanmod.neoemf.data.hbase.HBaseBackendFactory;
import fr.inria.atlanmod.neoemf.data.hbase.util.HBaseURI;
import fr.inria.atlanmod.neoemf.data.mapdb.MapDbBackendFactory;
import fr.inria.atlanmod.neoemf.data.mapdb.util.MapDbURI;
import fr.inria.atlanmod.neoemf.option.CommonOptions;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;
import fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.io.File;
import java.io.IOException;

import graph.Edge;
import graph.Graph;
import graph.GraphFactory;
import graph.Vertice;

/**
 * Code from the online NeoEMF tutorial.
 * <p>
 * This class contains the tutorial code and additional examples showing how to instantiate {@link PersistentResource}s
 * relying on Neo4j, MapDB, and HBase.
 * <p>
 * <b>Note:</b> HBase resource creation is presented in this file but not used to perform read/write operations,
 * because HBase needs to be installed separately and started to store a model. To enable HBase storage see the HBase
 * Configuration page on the wiki.
 */
public class Main {

    private static final ResourceSet resourceSet = new ResourceSetImpl();

    /**
     * Creates a new {@link PersistentResource} relying on a Blueprints
     * datastore on top of a Neo4j database to perform modeling operations.
     *
     * @return the created resource
     */
    public static Resource createBlueprintsResource() throws IOException {
        BackendFactoryRegistry.register(BlueprintsURI.SCHEME, BlueprintsBackendFactory.getInstance());
        resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(BlueprintsURI.SCHEME, PersistentResourceFactory.getInstance());

        Resource resource = resourceSet.createResource(BlueprintsURI.newBuilder().fromFile(new File("databases/myGraph.graphdb")));

        /*
         * Specify that Neo4j is used as the underlying blueprints backend.
         * Note using the BlueprintsNeo4jOptions class to create the option map automatically sets Neo4j as the graph
         * backend.
         */
        resource.save(BlueprintsNeo4jOptions.noOption());
        return resource;
    }

    /**
     * Creates a new {@link PersistentResource} using the MapDB datastore to
     * perform modeling operations.
     *
     * @return the created resource
     */
    public static Resource createMapDBResource() {
        BackendFactoryRegistry.register(MapDbURI.SCHEME, MapDbBackendFactory.getInstance());
        resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(MapDbURI.SCHEME, PersistentResourceFactory.getInstance());

        return resourceSet.createResource(MapDbURI.newBuilder().fromFile(new File("databases/myGraph.mapdb")));
    }

    /**
     * Creates a new {@link PersistentResource} using the HBase datastore
     * connected to a HBase server running on localhost:2181 to perfrom modeling
     * operations.
     *
     * @return the created resource
     */
    public static Resource createHBaseResource() {
        BackendFactoryRegistry.register(HBaseURI.SCHEME, HBaseBackendFactory.getInstance());
        resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(HBaseURI.SCHEME, PersistentResourceFactory.getInstance());

        return resourceSet.createResource(HBaseURI.newBuilder().fromServer("localhost", 2181, URI.createURI("myModel.hbase")));
    }

    /**
     * Reads the content of the provided {@code resource} and print it in the
     * console.
     *
     * @param resource the resource to read
     *
     * @throws IOException if an error occurs when loading the resource
     */
    public static void read(Resource resource) throws IOException {
        resource.load(CommonOptions.noOption());
        Graph graph = (Graph) resource.getContents().get(0);
        for (Edge each : graph.getEdges()) {
            System.out.println(each.getFrom().getLabel() + "--->" + each.getTo().getLabel());
        }
    }

    /**
     * Adds elements to the provided {@code resource} with new elements
     * representing a basic graph.
     *
     * @param resource the resource to add the element to
     *
     * @throws IOException if an error occurs when saving the resource
     */
    public static void write(Resource resource) throws IOException {
        GraphFactory factory = GraphFactory.eINSTANCE;
        Graph graph = factory.createGraph();

        for (int i = 0; i < 100; i++) {
            Vertice v1 = factory.createVertice();
            v1.setLabel("Vertice " + i + "a");
            Vertice v2 = factory.createVertice();
            v2.setLabel("Vertice " + i + "b");
            Edge e = factory.createEdge();
            e.setFrom(v1);
            e.setTo(v2);
            graph.getEdges().add(e);
            graph.getVertices().add(v1);
            graph.getVertices().add(v2);
        }
        resource.getContents().add(graph);
        resource.save(CommonOptions.noOption());
    }

    /**
     * Run the tutorial using a Blueprints and a MapDB resource.
     * <p>
     * <b>Note:</b> HBase resource creation is presented in this file but not
     * used to perform read/write operations, because HBase needs to be
     * installed separately and started to store a model. To enable HBase
     * storage see the HBase Configuration page on the wiki.
     *
     * @param args
     *
     * @throws IOException if a resource cannot be saved or loaded
     */
    public static void main(String[] args) throws IOException {
        Resource[] resources = {createBlueprintsResource(), createMapDBResource()};

        for (Resource resource : resources) {
            write(resource);
        }

        for (Resource resource : resources) {
            read(resource);
        }
    }
}
