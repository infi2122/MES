package Viewers;

import Models.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MES_Viewer {

    public void showInternalOrders(ArrayList<receiveOrder> recv, ArrayList<productionOrder> prod, ArrayList<shippingOrder> ship) {
        System.out.println("***** Internal Orders aka MES vectors *****");
        System.out.println("**** Raw Material Orders ****");

        for (receiveOrder curr : recv) {
            System.out.println("*** rawMaterial ID: " + curr.getRawMaterialOrderID() + " arrives on day: " + curr.getArrivalDate() +
                    " Type: " + curr.getPieceType() + " Quantity: " + curr.getQty() + " ***");

        }

        System.out.println("\n**** Production Orders ****");
        for (productionOrder curr : prod) {
            System.out.println("manufacuringID: " + curr.getManufacturingID()
                    + " of type: " + curr.getFinalType()
                    + " starts on day: " + curr.getStartDate());
            for(rawMaterial cur2: curr.getRawMaterials() ){
                System.out.println("RawID: "+cur2.getRawMaterialID() +" Qty used: "+ cur2.getQty_used() );
            }
        }

        System.out.println("\n**** Shipping Orders ****");
        for (shippingOrder curr : ship) {
            System.out.println("manufacuringID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartDate());
        }
    }

    public void showEntryWH(entryWarehouse entryWH) {

        System.out.println("***** Entry Warehouse *****");

        for (piece curr : entryWH.getPieces()) {
            System.out.println("rawMaterial ID: " + curr.getRawMaterialID() + " piece ID:" + curr.getPieceID() + " arrived on day: " + curr.getWHarrival());
        }

    }

    public void showExitWH(exitWarehouse exitWH) {

        System.out.println("***** Exit Warehouse *****");

        for (piece curr : exitWH.getPieces()) {
            System.out.println("rawMaterial ID: " + curr.getRawMaterialID() + " piece ID:" + curr.getPieceID()
                                + " arrived on day: " + curr.getWHarrival()
                                + " and started production: "+ curr.getProductionStart()
                                + " and ended production: "+ curr.getProductionEnd() );
        }

    }

}
