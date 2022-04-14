package Controllers;

import UDP.ERP2MES;
import Models.productionOrder;
import Models.receiveOrder;
import Models.shippingOrder;
import Viewers.MES_Viewer;

import java.util.ArrayList;

public class MES {

    // ****** ATTRIBUTTES ******
    private MES_Viewer mes_viewer;
    private ArrayList<receiveOrder> receiveOrder;
    private ArrayList<Models.productionOrder> productionOrder;
    private ArrayList<Models.shippingOrder> shippingOrder;

    public MES(MES_Viewer mes_viewer) {
        this.mes_viewer = mes_viewer;
        this.receiveOrder = new ArrayList<>();
        this.productionOrder = new ArrayList<>();
        this.shippingOrder = new ArrayList<>();
    }

    public MES_Viewer getMes_viewer() {
        return mes_viewer;
    }

    public void setMes_viewer(MES_Viewer mes_viewer) {
        this.mes_viewer = mes_viewer;
    }

    public ArrayList<receiveOrder> getReceiveOrder() {
        return receiveOrder;
    }

    public boolean addReceiveOrder(receiveOrder newReceiveOrder) {
        if (!getReceiveOrder().add(newReceiveOrder)) return false;
        return true;
    }

    public ArrayList<productionOrder> getProductionOrder() {
        return productionOrder;
    }

    public boolean addProductionOrder(productionOrder newProductionOrder) {
        if (!getProductionOrder().add(newProductionOrder)) return false;
        return true;
    }

    public ArrayList<shippingOrder> getShippingOrder() {
        return shippingOrder;
    }

    public boolean addShippingOrder(shippingOrder newShippingOrder) {
        if (!getShippingOrder().add(newShippingOrder)) return false;
        return true;
    }

    // ***** METHODS ******

    public void receiveInternalOrders() {

        ERP2MES erp2MES = new ERP2MES();
        String internalOrdersConcat = erp2MES.receivingFromERP();
        System.out.println(internalOrdersConcat);
        addNewInternalOrders(internalOrdersConcat);

    }

    private void addNewInternalOrders(String internalOrders) {

        // tokens will have 3 string, for receive, production and shipping respectively
        String[] tokens = internalOrders.split("-", -2);
        int i = 1;
        for (String tok : tokens) {
            //System.out.println("tok: " + tok);
            String str[] = tok.split("/", -2);

            switch (i) {
                case 1: {
                    for (String str_tok : str) {
                        if (!str_tok.isEmpty()) {
                            //System.out.println("str_tok: " + str_tok);
                            String last[] = str_tok.split("@",-2);
                            //System.out.println(last[0] + " || " + last[1]);
                            receiveOrder rOrder = new receiveOrder(Integer.parseInt(last[0]), Integer.parseInt(last[1]));
                            if (!getReceiveOrder().contains(rOrder)) {
                                addReceiveOrder(rOrder);
                                //System.out.println(getProductionOrder().get(getProductionOrder().size())-1);
                            }
                        }
                    }
                    i++;
                    break;
                }
                case 2:
                    for (String str_tok : str) {
                        if (!str_tok.isEmpty()) {
                            String last[] = str_tok.split("@",-2);
                            //System.out.println(last[0] + " || " + last[1]);
                            productionOrder pOrder = new productionOrder(Integer.parseInt(last[0]), Integer.parseInt(last[1]));
                            if (!getProductionOrder().contains(pOrder)) {
                                addProductionOrder(pOrder);
                            }
                        }
                    }
                    i++;
                    break;
                case 3:
                    for (String str_tok : str) {
                        if (!str_tok.isEmpty()) {
                            String last[] = str_tok.split("@",-2);
                            //System.out.println(last[0] + " || " + last[1]);
                            shippingOrder sOrder = new shippingOrder(Integer.parseInt(last[0]), Integer.parseInt(last[1]));
                            if (!getShippingOrder().contains(sOrder)) {
                                addShippingOrder(sOrder);

                            }
                        }
                    }
                    i++;
                    break;
                default:
                    break;

            }

        }


    }
  /*

        for (int i = 0; i < internalOrders.size(); i++) {
            //System.out.println(internalOrders.get(i));
            String recv[] = internalOrders.get(i).split("\\*", -1);
            //System.out.println("recv length: " + recv.length);
            if (i == 0) {
                int j = 0;
                while (j < recv.length - 1) {
                    //System.out.println(recv[j]);
                    String newOrder[] = recv[j].split("\\+", -1);
                    //System.out.println(newOrder[0] + newOrder[1]);
                    if (!getReceiveOrder().contains(new receiveOrder(Integer.parseInt(newOrder[0]), Integer.parseInt(newOrder[1]))))
                        addReceiveOrder(new receiveOrder(Integer.parseInt(newOrder[0]), Integer.parseInt(newOrder[1])));
                    j++;
                }
            } else if (i == 1) {
                int j = 0;
                while (j < recv.length - 1) {

                    String newOrder[] = recv[j].split("\\+", -1);
                    //System.out.println(newOrder[0] + newOrder[1]);
                    if (!getProductionOrder().contains(new productionOrder(Integer.parseInt(newOrder[0]), Integer.parseInt(newOrder[1]))))
                        addProductionOrder(new productionOrder(Integer.parseInt(newOrder[0]), Integer.parseInt(newOrder[1])));
                    j++;
                }
            } else {
                int j = 0;
                while (j < recv.length - 1) {
                    String newOrder[] = recv[j].split("\\+", -1);
                    //System.out.println(newOrder[0] + newOrder[1]);
                    if(!getShippingOrder().contains(new shippingOrder(Integer.parseInt(newOrder[0]), Integer.parseInt(newOrder[1]))))
                        addShippingOrder(new shippingOrder(Integer.parseInt(newOrder[0]), Integer.parseInt(newOrder[1])));
                    j++;
                }
            }
        }

    }*/

    // ******** VIEW METHODS *********

    public void displayInternalOrders() {
        getMes_viewer().showInternalOrders(getReceiveOrder(), getProductionOrder(), getShippingOrder());
    }


}
