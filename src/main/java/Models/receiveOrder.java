package Models;

public class receiveOrder extends internalOrders{

    private int pieceType;
    private int reservedQty;


    public receiveOrder(int orderID, int startDate, int type, int resQty) {
        super(orderID, startDate);
        this.pieceType = type;
        this.reservedQty = resQty;

    }

    public int getPieceType() {
        return pieceType;
    }

    public int getReservedQty() {
        return reservedQty;
    }



}
