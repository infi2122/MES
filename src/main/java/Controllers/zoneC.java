package Controllers;

import Models.piece;
import Models.productionOrder;
import Models.rawMaterial;

import java.util.ArrayList;
import java.util.Iterator;

public class zoneC<MCT_table> extends Thread {

    private ArrayList<piece> piecesOnFloor = new ArrayList<>();
    private final int MAXIMUM_CAPACITY = 6;

    private static int oneDay = 60;

    private MES mes;

    public zoneC(MES mes1) {
        this.mes = mes1;
    }

    public boolean is_W1in1_free() {
        return mes.getOpcClient().readBool("W1in1_free", "GVL");
    }

    public boolean is_zoneC_full() {
        return piecesOnFloor.size() == MAXIMUM_CAPACITY;
    }

    @Override
    public void run() {

        synchronized (mes) {
            defineMCT();
            unloadToSFS();

        }
    }

    private static int[][] MCT_table = {
            {1, 0, 3, 2, 2, 4},
            {1, 3, 3, 2, 3, 4},
            {1, 4, 3, 2, 4, 4},
            {1, 1, 3, 2, 0, 4},
            {1, 3, 3, 2, 4, 4},
            {1, 1, 3, 2, 3, 4},
            {1, 3, 3, 2, 4, 4}
    };


    public void defineMCT() {

        int currDay = mes.getCountdays();

        if (currDay == 0) {
            // Se o dia for 0 então faz o default das MCT
            // MCT [ 1, 1, 3, 2, 2, 4 ]
            int[] mct = {1, 1, 3, 2, 2, 4};
            setMCT(mct);

        } else if (currDay % 2 != 0)
            return;
        else {
            int type = 0, num = 0;
            for (productionOrder curr : mes.getProductionOrder()) {
                if (curr.getStartDate() == currDay + 1 || curr.getStartDate() == currDay + 2) {
                    if (curr.getQty() > num) {
                        num = curr.getQty();
                        type = curr.getFinalType();
                    }
                }
            }
            // Porque o vetor MCT_table tem as P3 na pos 0
            type = type - 3;
            setMCT(MCT_table[type]);
        }

    }

    private void setMCT(int[] mct) {

        for (int i = 0; i < mct.length; i++) {
            if (mct[i] == 0)
                mct[i] = mes.getOpcClient().readInt("MCT_" + i, "GVL");
            mes.getOpcClient().writeInt("MCT_" + i, "GVL", mct[i]);
        }

    }

    private int i = 0;

    public void unloadToSFS() {

        int currDay = mes.getCountdays();

        if (i < mes.getProductionOrder().size()) {

            productionOrder currProdOrder = mes.getProductionOrder().get(i);

            if (currProdOrder.getStartDate() <= currDay) {

                if (piecesOnFloor.size() < MAXIMUM_CAPACITY && mes.getOpcClient().readBool("W1in1_free", "GVL")) {
                    for (rawMaterial curr : currProdOrder.getRawMaterials()) {

                        if (curr.getQty_used() > 0) {
                            System.out.println("Raw Material id: "+ curr.getRawMaterialID() );
                            piece newPieceOnFloor = searchPieceInEntryWH(curr.getRawMaterialID());

                            newPieceOnFloor.setOrderID(currProdOrder.getManufacturingID());
                            newPieceOnFloor.setExpectedType(currProdOrder.getFinalType());
                            newPieceOnFloor.setProductionStart(mes.getCurrentTime());
                            piecesOnFloor.add(newPieceOnFloor);
                            //  int[] transformations = [actualType, T1, T2, T3]
                            int[] transformations = getTransformations(newPieceOnFloor.getExpectedType());
                            curr.setQty_used(curr.getQty_used() - 1);
                            System.out.println("manufacturingID: " + newPieceOnFloor.getOrderID() + " tipo atual: " + transformations[0]
                                    + " T1: " + transformations[1] + " T2: " + transformations[2] + " T3: " + transformations[3]
                                    + " piece ID: " + newPieceOnFloor.getPieceID() + " rawMaterialID: " + newPieceOnFloor.getRawMaterialID());


                            mes.getOpcClient().writeBool("W1in1_free", "GVL", 0);

                            i++;
                            break;
                        }

                    }
                }
            }
        }
        if (i == mes.getProductionOrder().size()) {
            i = 0;
        }

    }

    private piece searchPieceInEntryWH(int rawMaterialID) {

        piece returnPiece = null;

        ArrayList <piece> list = mes.getEntryWH().getPieces();
        Iterator<piece> iterador = list.iterator();

        while( iterador.hasNext() ){
            piece temp = iterador.next();
            if (temp.getRawMaterialID() == rawMaterialID) {
                returnPiece = new piece(temp);
                returnPiece.setRawMaterialID(rawMaterialID);
                iterador.remove();
                System.out.println("fds"+returnPiece.getPieceID());
                break;
            }
        }
//        for (piece currPiece : mes.getEntryWH().getPieces()) {
//            if (currPiece.getRawMaterialID() == rawMaterialID) {
//                returnPiece = new piece(currPiece);
//                returnPiece.setRawMaterialID(rawMaterialID);
//                mes.getEntryWH().getPieces().remove(currPiece);
//                break;
//            }
//        }


        return returnPiece;

    }

    private int[] getTransformations(int finalType) {

        int[] ret;
        switch (finalType) {
            case 3:
                ret = new int[]{2, 2, 0, 0};
                break;
            case 4:
                ret = new int[]{2, 3, 0, 0};
                break;
            case 5:
                ret = new int[]{2, 4, 0, 0};
                break;
            case 6:
                ret = new int[]{1, 1, 0, 0};
                break;
            case 7:
                ret = new int[]{2, 3, 4, 0};
                break;
            case 8:
                ret = new int[]{1, 1, 3, 0};
                break;
            case 9:
                ret = new int[]{2, 3, 4, 3};
                break;
            default:
                ret = new int[]{0, 0, 0, 0};
                break;
        }
        return ret;
    }


}
