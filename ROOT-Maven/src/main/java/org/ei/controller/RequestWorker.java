package org.ei.controller;

public class RequestWorker extends Thread {
    private RequestQueue que;
    private ResponsePool pool;
    private String cacheDir;

    public RequestWorker(RequestQueue que, ResponsePool pool, String cacheDir) {
        this.que = que;
        this.pool = pool;
        this.cacheDir = cacheDir;
    }

    public void run() {
        try {
            while (true) {
                DataRequest req = (DataRequest) this.que.dequeue();
                // System.out.println("DataSourceWorker Getting To Work");
                DataResponseBroker broker = DataResponseBroker.getInstance(cacheDir);
                OutputPrinter ou = null;
                DataResponse res = broker.getDataResponse(ou, req);
                this.pool.addResponse(req.getRequestID(), res);
            }
        } catch (Exception e) {
            // TODO, put error logger here
        }
    }

}
