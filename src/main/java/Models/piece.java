package Models;

public class piece {

    private int pieceID;
    private int rawMaterialID;
    private int orderID;
    private int expectedType;
    private int WHarrival;
    private long productionStart;
    private long productionEnd;
    private int shippingStart;
    private int shippingEnd;

    public piece(int pieceID, int WHarrival){
        this.pieceID = pieceID;
        this.WHarrival = WHarrival;
    }

    public piece(int pieceID, int rawMaterialID, int WHarrival) {
        this.pieceID = pieceID;
        this.rawMaterialID = rawMaterialID;
        this.WHarrival = WHarrival;
    }

    public piece(piece convP2) {
        this.pieceID = convP2.pieceID;
        this.WHarrival = convP2.WHarrival;
    }

    public piece(int pieceID, int rawMaterialID, int orderID, int expectedType, int WHarrival, long productionStart, long productionEnd) {
        this.pieceID = pieceID;
        this.rawMaterialID = rawMaterialID;
        this.orderID = orderID;
        this.expectedType = expectedType;
        this.WHarrival = WHarrival;
        this.productionStart = productionStart;
        this.productionEnd = productionEnd;
    }

    public int getPieceID() {
        return pieceID;
    }

    public void setPieceID(int pieceID) {
        this.pieceID = pieceID;
    }

    public int getRawMaterialID() {
        return rawMaterialID;
    }

    public void setRawMaterialID(int rawMaterialID) {
        this.rawMaterialID = rawMaterialID;
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

    public long getProductionStart() {
        return productionStart;
    }

    public void setProductionStart(long productionStart) {
        this.productionStart = productionStart;
    }

    public long getProductionEnd() {
        return productionEnd;
    }

    public void setProductionEnd(long productionEnd) {
        this.productionEnd = productionEnd;
    }

    public int getShippingStart() {
        return shippingStart;
    }

    public void setShippingStart(int shippingStart) {
        this.shippingStart = shippingStart;
    }

    public int getShippingEnd() {
        return shippingEnd;
    }

    public void setShippingEnd(int shippingEnd) {
        this.shippingEnd = shippingEnd;
    }
}
