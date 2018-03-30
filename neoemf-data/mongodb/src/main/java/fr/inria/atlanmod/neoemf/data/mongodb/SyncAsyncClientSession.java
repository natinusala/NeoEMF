package fr.inria.atlanmod.neoemf.data.mongodb;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.session.ClientSession;

public class SyncAsyncClientSession implements SingleResultCallback<ClientSession> {
    private ClientSession clientSession;
    private Boolean isEnded;
    private Throwable throwable;

    @Override
    public void onResult(ClientSession cs, Throwable throwable) {
        this.throwable = throwable;
        this.clientSession = cs;
        isEnded = true;
    }

    public ClientSession waitForCompletion() throws Throwable {
        while (!isEnded){
            Thread.sleep(100);
        }
        if(throwable!=null)
            throw throwable;

        return clientSession;
    }
}
