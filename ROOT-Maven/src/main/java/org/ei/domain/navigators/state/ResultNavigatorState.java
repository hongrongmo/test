package org.ei.domain.navigators.state;




public class ResultNavigatorState implements Comparable<ResultNavigatorState>{

    /**
     * Enumeration for navigator open states
     * @author harovetm
     *
     */
    public enum OPEN { 
        CLOSEDALL(-10),
        CLOSED5(-5),
        UNINITIALIZED(-1),
        SHOW5(5),
        SHOWALL(10);
        
        private int val = -1;
        
        OPEN(int val) {
            this.val = val;
        }
        
        public int getVal() { return val; }
        public static OPEN fromVal(int val) {
            switch (val) {
            case -10:return CLOSEDALL;
            case -5:return CLOSED5;
            case 5:return SHOW5;
            case 10:return SHOWALL;
            }
            return UNINITIALIZED;
        }
    }
    
    
    protected String field;
    protected OPEN open = OPEN.UNINITIALIZED;
    protected int order = 999;
    protected int navOrder = 999;

    public ResultNavigatorState(String field) {
        this.field = field;
    }
    public String getField() {
        return field;
    }
    public OPEN getOpen() {
        return open;
    }
    public void setOpen(OPEN open) {
        this.open = open;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
	public int compareTo(ResultNavigatorState obj) {
		if (this.order <  obj.order) return -1;
		else if (this.order > obj.order) return 1;
		else return 0;
	}
	public int getNavOrder() {
		return navOrder;
	}
	public void setNavOrder(int navOrder) {
		this.navOrder = navOrder;
	}
}

