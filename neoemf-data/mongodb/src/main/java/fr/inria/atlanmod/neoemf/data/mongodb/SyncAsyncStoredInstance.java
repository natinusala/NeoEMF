package fr.inria.atlanmod.neoemf.data.mongodb;

import com.mongodb.async.SingleResultCallback;
import fr.inria.atlanmod.neoemf.data.mongodb.model.StoredInstance;

public class SyncAsyncStoredInstance implements SingleResultCallback<StoredInstance> {
    private StoredInstance instance;
    private Boolean isEnded;
    private Throwable throwable;

    @Override
    public void onResult(StoredInstance storedInstance, Throwable throwable) {
        this.throwable = throwable;
        instance = storedInstance;
        isEnded = true;
    }

    public StoredInstance waitForCompletion() throws Throwable {
        while (!isEnded){
            Thread.sleep(100);
        }
        if(throwable!=null)
            throw throwable;

        return instance;
    }

}
