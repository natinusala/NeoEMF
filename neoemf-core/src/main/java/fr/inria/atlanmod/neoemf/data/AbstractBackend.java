package fr.inria.atlanmod.neoemf.data;

import java.io.IOException;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An abstract {@link Backend} that provides a global behavior about the closure.
 */
@ParametersAreNonnullByDefault
public abstract class AbstractBackend implements Backend {

    /**
     * Whether this backend is closed.
     */
    private boolean isClosed;

    @Override
    public final void close() {
        if (isClosed) {
            return;
        }

        try {
            safeClose();
        }
        catch (Exception ignored) {
        }

        isClosed = true;
    }

    /**
     * Cleanly closes the database associated to this backend.
     */
    protected abstract void safeClose() throws IOException;
}