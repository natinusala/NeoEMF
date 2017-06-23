package fr.inria.atlanmod.neoemf.util;

import org.eclipse.emf.common.util.URI;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A builder of {@link URI} used to register {@link fr.inria.atlanmod.neoemf.data.BackendFactory} in the {@link
 * fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry} and configure the {@code protocol to factory} map of an
 * existing {@link org.eclipse.emf.ecore.resource.ResourceSet} with a {@link fr.inria.atlanmod.neoemf.resource.PersistentResourceFactory}.
 *
 * @see fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry
 * @see org.eclipse.emf.ecore.resource.ResourceSet#getResourceFactoryRegistry()
 * @see org.eclipse.emf.ecore.resource.Resource.Factory.Registry#getProtocolToFactoryMap()
 */
@ParametersAreNonnullByDefault
public interface UriBuilder {

    /**
     * Creates a new {@code URI} from the given {@code uri}.
     * <p>
     * This method checks that the scheme of the provided {@code uri} can be used to create a new {@link
     * UriBuilder}. Its scheme must be registered in the {@link fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry}.
     *
     * @param uri the base file-based {@link URI}
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if the {@code uri} is {@code null}
     * @throws IllegalArgumentException      if the scheme of the provided {@code uri} is not registered in the {@link
     *                                       fr.inria.atlanmod.neoemf.data.BackendFactoryRegistry}
     */
    @Nonnull
    URI fromUri(URI uri);

    /**
     * Creates a new {@code URI} from the given {@code file} descriptor.
     *
     * @param filePath the path of the {@link File} to build a {@link URI} from
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if the {@code filePath} is {@code null}
     */
    @Nonnull
    URI fromFile(String filePath);

    /**
     * Creates a new {@code URI} from the given {@code file} descriptor.
     *
     * @param file the {@link File} to build a {@link URI} from
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if the {@code file} is {@code null}
     */
    @Nonnull
    URI fromFile(File file);

    /**
     * Creates a new {@code URI} from the {@code host}, {@code port}, and {@code model} by creating a hierarchical
     * {@link URI} that references the distant model resource.
     *
     * @param host  the address of the server (use {@code "localhost"} if the server is running locally)
     * @param port  the port of the server
     * @param model a {@link URI} identifying the model in the database
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if any of the parameters is {@code null}
     * @throws IllegalArgumentException      if {@code port < 0}
     */
    @Nonnull
    URI fromServer(String host, int port, URI model);

    /**
     * Creates a new {@code URI} from the {@code host}, {@code port}, and {@code model} by creating a hierarchical
     * {@link URI} that references the distant model resource.
     *
     * @param host     the address of the server (use {@code "localhost"} if the server is running locally)
     * @param port     the port of the server
     * @param segments a string identifying the model in the database
     *
     * @return a new URI
     *
     * @throws UnsupportedOperationException if this URI builder does not support this method
     * @throws NullPointerException          if any of the parameters is {@code null}
     * @throws IllegalArgumentException      if {@code port < 0}
     */
    @Nonnull
    URI fromServer(String host, int port, String... segments);
}
