package Controllers;

import OPC_UA.opcConnection;
import UDP.ERPtunnel;
import Models.productionOrder;
import Models.receiveOrder;
import Models.shippingOrder;
import Viewers.MES_Viewer;

import java.util.ArrayList;

public class MES {

    // ****** VARIABLES *******

    private long startTime = 0;
    private int countdays = -1;
    private ERPtunnel erp2mesTunnel;
    private opcConnection opcClient;

    private static class exe {
        public static final int oneDay = 60;
    }

    // ****** ATTRIBUTTES ******
    private long currentTime;
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

    public void setErp2MesTunnel(ERPtunnel tunnel) {
        erp2mesTunnel = tunnel;
    }

    public ERPtunnel getErp2mesTunnel() {
        return erp2mesTunnel;
    }

    public opcConnection getOpcClient() {
        return opcClient;
    }

    public void setOpcClient(opcConnection opcClient) {
        this.opcClient = opcClient;
    }

    public void setStartTime() {
        long init = getErp2mesTunnel().initTimer();
        if (init != -1)
            startTime = init;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
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

    public void countTime() {

        if (countdays == -1) {
            countdays++;
            System.out.println("Current Day: " + countdays);
        }

        long time = System.currentTimeMillis();

        if (time >= getCurrentTime() * 1000 + startTime + 1000) {
            setCurrentTime((time - startTime) / 1000);

            if ((int) getCurrentTime() / exe.oneDay > countdays) {
                countdays = (int) getCurrentTime() / exe.oneDay;
                System.out.println("Current Day: " + countdays);
            }

        }


    }

    public void receiveInternalOrders() {

        String internalOrdersConcat = getErp2mesTunnel().receivingOrdersFromERP();
        if (internalOrdersConcat.equals(null))
            return;
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
                            String last[] = str_tok.split("@", -2);
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
                            String last[] = str_tok.split("@", -2);
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
                            String last[] = str_tok.split("@", -2);
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

    public void testeReadMCT () {

        opcClient.readMCT(4);
    }
    public void testeWriteMCT () {
        opcClient.writeMCT(0,2);
    }


    // ******** VIEW METHODS *********

    public void displayInternalOrders() {
        getMes_viewer().showInternalOrders(getReceiveOrder(), getProductionOrder(), getShippingOrder());
    }


}
