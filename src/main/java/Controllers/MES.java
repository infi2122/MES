package Controllers;

import Models.*;
import OPC_UA.opcConnection;
import UDP.ERPtunnel;
import Viewers.MES_Viewer;

import java.util.ArrayList;

public class MES {

    // ****** VARIABLES *******

    private long startTime = 0;
    private int countdays = -1;
    private ERPtunnel erp2mesTunnel;
    private opcConnection opcClient;


    public static final int oneDay = 60;


    // ****** ATTRIBUTTES ******
    private long currentTime;
    private MES_Viewer mes_viewer;
    private ArrayList<receiveOrder> receiveOrder;
    private ArrayList<productionOrder> productionOrder;
    private ArrayList<shippingOrder> shippingOrder;
    private entryWarehouse entryWH;


    public MES(MES_Viewer mes_viewer) {
        this.mes_viewer = mes_viewer;
        this.receiveOrder = new ArrayList<>();
        this.productionOrder = new ArrayList<>();
        this.shippingOrder = new ArrayList<>();
        this.entryWH = new entryWarehouse();

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

    public entryWarehouse getEntryWH() {
        return entryWH;
    }

    public void addPiece2entryWH(piece newPiece) {
        getEntryWH().addNewPiece(newPiece);
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

            if ((int) getCurrentTime() / oneDay > countdays) {
                countdays = (int) getCurrentTime() / oneDay;
                System.out.println("Current Day: " + countdays);
            }

        }
    }

    public void receiveInternalOrders() {

        String internalOrdersConcat = getErp2mesTunnel().receivingOrdersFromERP();
        if (internalOrdersConcat.equals(null))
            return;
        //System.out.println(internalOrdersConcat);
        addNewInternalOrders(internalOrdersConcat);

    }

    private void addNewInternalOrders(String internalOrders) {

        // tokens will have 3 string, for receive, production and shipping respectively
        String[] tokens = internalOrders.split("_", -2);
        int i = 1;
        for (String tok : tokens) {
            //System.out.println("tok: " + tok);
            String str[] = tok.split("/", -2);

            switch (i) {
                case 1: {
                    //getReceiveOrder().removeAll(getReceiveOrder());

                    for (String str_tok : str) {
                        if (!str_tok.isEmpty()) {
                            //System.out.println("str_tok: " + str_tok);
                            String last[] = str_tok.split("@", -2);
                            //System.out.println(last[0] + " || " + last[1]);

                            receiveOrder rOrder = new receiveOrder(
                                    Integer.parseInt(last[0]),
                                    Integer.parseInt(last[1]),
                                    Integer.parseInt(last[2]),
                                    Integer.parseInt(last[3]));

                            if (!getReceiveOrder().contains(rOrder)) {
                                addReceiveOrder(rOrder);
                                //System.out.println(getProductionOrder().get(getProductionOrder().size() - 1));
                            }
                        }
                    }
                    i++;
                    break;
                }
                case 2:
                    //getProductionOrder().removeAll(getProductionOrder());

                    for (String str_tok : str) {
                        if (!str_tok.isEmpty()) {
                            String last[] = str_tok.split("@", -2);
                            //System.out.println(last[0] + " || " + last[1]);
                            productionOrder pOrder = new productionOrder(
                                    Integer.parseInt(last[0]),
                                    Integer.parseInt(last[1]),
                                    Integer.parseInt(last[2]),
                                    Integer.parseInt(last[3]));
                            int k = 0;
                            ArrayList<rawMaterial> rawMaterials = new ArrayList<>();
                            while (k < Integer.parseInt(last[4])) {
                                rawMaterials.add(new rawMaterial(
                                        Integer.parseInt(last[5 + k]),
                                        Integer.parseInt(last[6 + k])));
                                k++;
                            }
                            pOrder.setRawMaterials(rawMaterials);

                            if (!getProductionOrder().contains(pOrder)) {
                                addProductionOrder(pOrder);
                            }
                        }
                    }
                    i++;
                    break;
                case 3:
                    //getShippingOrder().removeAll(getShippingOrder());

                    for (String str_tok : str) {
                        if (!str_tok.isEmpty()) {
                            String last[] = str_tok.split("@", -2);
                            //System.out.println(last[0] + " || " + last[1]);
                            shippingOrder sOrder = new shippingOrder(
                                    Integer.parseInt(last[0]),
                                    Integer.parseInt(last[1]),
                                    Integer.parseInt(last[2]));
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

    public void testMES (){

        setCurrentTime(System.currentTimeMillis());

        addReceiveOrder(new receiveOrder(
                0,
                9,
                2,
                5
        ));
        addReceiveOrder(new receiveOrder(
                1,
                8,
                3,
                4
        ));
        addReceiveOrder(new receiveOrder(
                3,
                5,
                4,
                16
        ));


    }


    // ******** VIEW METHODS *********

    public void displayInternalOrders() {
        getMes_viewer().showInternalOrders(getReceiveOrder(), getProductionOrder(), getShippingOrder());
    }


}
