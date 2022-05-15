package Viewers;

import Models.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MES_Viewer {

    public void showInternalOrders(ArrayList<receiveOrder> recv, ArrayList<productionOrder> prod, ArrayList<shippingOrder> ship) {
        if (recv.size() == 0 && prod.size() == 0 && ship.size() == 0)
            return;

        System.out.println("************************ Internal Orders  *************************");
        System.out.println("---------------------> Raw Material Orders <-----------------------");

        for (receiveOrder curr : recv) {
            System.out.println(" rawMaterial ID: " + curr.getRawMaterialOrderID() + " arrives on day: " + curr.getArrivalDate() +
                    " Type: " + curr.getPieceType() + " Quantity: " + curr.getQty());

        }

        System.out.println("\n----------------------> Production Orders <-----------------------");
        for (productionOrder curr : prod) {
            System.out.println(" manufacuringID: " + curr.getManufacturingID()
                    + " of type: " + curr.getFinalType()
                    + " starts on day: " + curr.getStartDate());
            for (rawMaterial cur2 : curr.getRawMaterials()) {
                System.out.println("     rawMaterial ID: " + cur2.getRawMaterialID() + " quantity used: " + cur2.getQty_used());
            }
        }

        System.out.println("\n------------------------> Shipping Orders <-----------------------");
        for (shippingOrder curr : ship) {
            System.out.println(" manufacturingID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartDate());
        }

        System.out.println("*******************************************************************");
        System.out.println(" ");
    }

    public void showEntryWH(entryWarehouse entryWH) {

        if (entryWH.getPieces().size() == 0)
            return;

        int oldOrder = -1;
        System.out.println("************************* Entry Warehouse *************************");

        for (piece curr : entryWH.getPieces()) {

            if (oldOrder != curr.getRawMaterialID()) {
                System.out.println(" \nrawMaterial ID: " + curr.getRawMaterialID() + " arrived on day: " + curr.getWHarrival());
                System.out.print(" piece IDs: ");
            }
            System.out.print(curr.getPieceID() + "/");

            oldOrder = curr.getRawMaterialID();

        }

        System.out.println("\n*******************************************************************");
        System.out.println(" ");

    }

    public void showExitWH(exitWarehouse exitWH) {
        if (exitWH.getPieces().size() == 0)
            return;
        int oldOrder = -1;

        System.out.println("************************* Exit Warehouse **************************");
        for (piece curr : exitWH.getPieces()) {

            if (oldOrder != curr.getRawMaterialID()) {
                System.out.println(" \nrawMaterial ID: " + curr.getRawMaterialID() + " arrived on day: " + curr.getWHarrival());
                System.out.print(" piece IDs: ");
            }
            System.out.print(curr.getPieceID() + "/");

            oldOrder = curr.getRawMaterialID();

        }

        System.out.println("\n*******************************************************************");
        System.out.println(" ");

    }

    public void showPiecesHistory(ArrayList<piecesHistory> arrayPH) {
        if (arrayPH.size() == 0)
            return;

        System.out.println("************************** Piece History **************************");

        for (piecesHistory pH : arrayPH) {
            System.out.println(" Manufacturing ID: " + pH.getManufacturingID() + " Quantity: " + pH.getQty());
            for (piece curr : pH.getPieces()) {
                System.out.println("     rawID: " + curr.getRawMaterialID() +
                        " pieceID: " + curr.getPieceID() +
                        " WHarrival/prodStart/ShipStart: " + curr.getWHarrival() + "/" + curr.getProductionStart() + "/" + curr.getShippingStart() +
                        " prodEnd/ShipEnd: " + curr.getProductionEnd() + "/" + curr.getShippingEnd());
            }
        }

        System.out.println("*******************************************************************");
        System.out.println(" ");
    }


}
