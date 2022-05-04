package Models;

import java.util.ArrayList;

public class productionOrder{

    private int manufactID;
    private int qty;
    private int final_type;
    private ArrayList<rawMaterial> rawMaterials = new ArrayList<>();
    private int start_time;
    private int pieces_in_sfs;
    private int pieces_done;


    public productionOrder(int manufactID, int qty, int final_type, ArrayList<rawMaterial> rawMaterials, int start_time) {
        this.manufactID = manufactID;
        this.qty = qty;
        this.final_type = final_type;
        this.rawMaterials = rawMaterials;
        this.start_time = start_time;
    }

    public int getManufactID() {
        return manufactID;
    }
    public int getQty() {
        return qty;
    }
    public int getFinal_type() {
        return final_type;
    }
    public ArrayList<rawMaterial> getRawMaterials() {
        return rawMaterials;
    }
    public int getStart_time() {
        return start_time;
    }
    public int getPieces_in_sfs() {
        return pieces_in_sfs;
    }

    public void setPieces_in_sfs(int pieces_in_sfs) {
        this.pieces_in_sfs = pieces_in_sfs;
    }

    public int getPieces_done() {
        return pieces_done;
    }

    public void setPieces_done(int pieces_done) {
        this.pieces_done = pieces_done;
    }
}
