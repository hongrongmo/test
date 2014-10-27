package org.ei.util;

public class TimedObject {
    private long expireIn;
    private long timeSet;
    private Object ob;

    public void setExpireIn(long millis) {
        this.expireIn = millis;
        this.timeSet = System.currentTimeMillis();
    }

    public boolean expired() {
        long now = System.currentTimeMillis();
        return ((now - timeSet) > expireIn);
    }

    public void setObject(Object o) {
        this.ob = o;
    }

    public Object getObject() {
        return ob;
    }

}