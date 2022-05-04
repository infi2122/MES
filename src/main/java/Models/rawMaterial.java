package Models;

public class rawMaterial {

    private int rawMaterialID;
    private int qty_used;

    public rawMaterial(int rawMaterialID, int qty_used) {
        this.rawMaterialID = rawMaterialID;
        this.qty_used = qty_used;
    }

    public int getRawMaterialID() {
        return rawMaterialID;
    }

    public int getQty_used() {
        return qty_used;
    }
}
