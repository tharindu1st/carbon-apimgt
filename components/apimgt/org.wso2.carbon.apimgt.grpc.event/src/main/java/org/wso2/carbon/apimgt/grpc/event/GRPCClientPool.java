package org.wso2.carbon.apimgt.grpc.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;

public class GRPCClientPool {

    private static final Log log = LogFactory.getLog(GRPCClientPool.class);

    private ObjectPool clientPool;

    public GRPCClientPool(String url) {

        int port = Integer.parseInt(url.substring(url.lastIndexOf(":")+1));
        String host = url.substring(0, url.lastIndexOf(":"));
        log.debug("Initializing API key validator client pool");
        clientPool = new StackObjectPool(new BasePoolableObjectFactory() {
            @Override
            public Object makeObject() throws Exception {

                log.debug("Initializing new APIKeyValidatorClient instance");
                return new GRPCClient(host, port);
            }
        }, 10, 5);
    }

    public GRPCClient get() throws Exception {

        if (log.isTraceEnabled()) {
            int active = clientPool.getNumActive();
            if (active >= 10) {
                log.trace("Key validation pool size is :" + active);
            }
        }
        return (GRPCClient) clientPool.borrowObject();
    }

    public void release(GRPCClient client) throws Exception {

        clientPool.returnObject(client);
    }

    public void cleanup() {

        try {
            clientPool.close();
        } catch (Exception e) {
            log.warn("Error while cleaning up the object pool", e);
        }
    }

}
