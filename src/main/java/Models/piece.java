package Models;

public class piece {

    private int orderID;
    private int expectedType;
    private int WHarrival;
    private int productionStart;
    private int productionEnd;
    private int unloadStart;
    private int unloadEnd;

    public piece(int orderID, int WHarrival) {
        this.orderID = orderID;
        this.WHarrival = WHarrival;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(int expectedType) {
        this.expectedType = expectedType;
    }

    public int getWHarrival() {
        return WHarrival;
    }

    public void setWHarrival(int WHarrival) {
        this.WHarrival = WHarrival;
    }

    public int getProductionStart() {
        return productionStart;
    }

    public void setProductionStart(int productionStart) {
        this.productionStart = productionStart;
    }

    public int getProductionEnd() {
        return productionEnd;
    }

    public void setProductionEnd(int productionEnd) {
        this.productionEnd = productionEnd;
    }

    public int getUnloadStart() {
        return unloadStart;
    }

    public void setUnloadStart(int unloadStart) {
        this.unloadStart = unloadStart;
    }

    public int getUnloadEnd() {
        return unloadEnd;
    }

    public void setUnloadEnd(int unloadEnd) {
        this.unloadEnd = unloadEnd;
    }


}
