package Viewers;

import Models.productionOrder;
import Models.receiveOrder;
import Models.shippingOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MES_Viewer {

/*    public void showInternalOrders(ArrayList<receiveOrder> recv, ArrayList<productionOrder> prod, ArrayList<shippingOrder> ship) {
        System.out.println("***** Internal Orders aka MES vectors *****");
        System.out.println("**** Unload Orders ****");
        int old_id = 0;

        if (recv.size() != 0) {
            receiveOrder begin = recv.get(0);
            receiveOrder end = new receiveOrder(0, 0);
            old_id = begin.getStartDate();
            for (receiveOrder curr : recv) {
                if (old_id == curr.getOrderID()) {
                    end = curr;
                } else {
                    if (end.getOrderID() != curr.getOrderID()) {
                        System.out.println("productionID: " + begin.getOrderID() + " starts on day: " + begin.getStartDate() + " & ends on day: " + end.getStartDate());
                        begin = curr;
                    }
                    old_id = curr.getOrderID();
                }
            }

            System.out.println("productionID: " + begin.getOrderID() + " starts on day: " + begin.getStartDate() + " & ends on day: " +recv.get(recv.size()-1).getStartDate());

        }

        System.out.println("\n**** Production Orders ****");
        if (prod.size() != 0) {
            productionOrder begin2 = prod.get(0);
            old_id = begin2.getStartDate();
            productionOrder end2 = new productionOrder(0, 0);

            for (productionOrder curr : prod) {
                if (old_id == curr.getOrderID()) {
                    end2 = curr;
                } else {
                    if (end2.getOrderID() != curr.getOrderID()) {
                        System.out.println("productionID: " + begin2.getOrderID() + " starts on day: " + begin2.getStartDate() + " & ends on day: " + end2.getStartDate());
                        begin2 = curr;
                    }
                    old_id = curr.getOrderID();
                }
            }

            System.out.println("productionID: " + begin2.getOrderID() + " starts on day: " + begin2.getStartDate() + " & ends on day: " + prod.get(prod.size()-1).getStartDate());

        }

        System.out.println("\n**** Shipping Orders ****");
        if (ship.size() != 0) {

            shippingOrder begin3 = ship.get(0);
            shippingOrder end3 = new shippingOrder(0, 0);
            old_id = begin3.getStartDate();
            for (shippingOrder curr : ship) {

                if (old_id == curr.getOrderID()) {
                    end3 = curr;
                } else {
                    if (end3.getOrderID() != curr.getOrderID()) {
                        System.out.println("productionID: " + begin3.getOrderID() + " starts on day: " + begin3.getStartDate() + " & ends on day: " + end3.getStartDate());
                        begin3 = curr;
                    }
                    old_id = curr.getOrderID();
                }
            }

            System.out.println("productionID: " + begin3.getOrderID() + " starts on day: " + begin3.getStartDate() + " & ends on day: " + ship.get(ship.size()-1).getStartDate());

        }
        System.out.println(" ");
    }*/

    public void showInternalOrders(ArrayList<receiveOrder> recv, ArrayList<productionOrder> prod, ArrayList<shippingOrder> ship) {
        System.out.println("***** Internal Orders aka MES vectors *****");
        System.out.println("**** Raw Material Orders ****");

        for (receiveOrder curr : recv) {
            System.out.println("*** rawMaterial ID: " + curr.getRawMaterialOrderID()+ " arrives on day: " + curr.getArrivalDate() +
                    " Type: " + curr.getPieceType() + " Quantity: " + curr.getQty() +" ***");

        }

        System.out.println("\n**** Production Orders ****");
        for (productionOrder curr : prod) {
            System.out.println("manufacuringID: " + curr.getManufacturingID() + " of type: " + curr.getFinalType() + " starts on day: " + curr.getStartDate());
        }

        System.out.println("\n**** Shipping Orders ****");
        for (shippingOrder curr : ship) {
            System.out.println("manufacuringID: " + curr.getManufacturingID() + " starts on day: " + curr.getStartDate());
        }
    }

}
