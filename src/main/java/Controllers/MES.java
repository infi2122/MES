package Controllers;

import Models.*;
import OPC_UA.opcConnection;
import Viewers.MES_Viewer;
import comsProtocols.sharedResources;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.ArrayList;

public class MES {
    // ****** VARIABLES *******

    private long startTime = Integer.MAX_VALUE;
    private int countdays = -1;
    private sharedResources sharedBuffer;
    private opcConnection opcClient;
    private boolean syncronized_with_ERP;

    public static final int oneDay = 60;

    // ****** ATTRIBUTTES ******
    private long currentTime;
    private MES_Viewer mes_viewer;
    private ArrayList<receiveOrder> receiveOrder;
    private ArrayList<productionOrder> productionOrder;
    private ArrayList<shippingOrder> shippingOrder;
    private ArrayList<piecesHistory> piecesHistories;
    private entryWarehouse entryWH;
    private exitWarehouse exitWH;


    public MES(MES_Viewer mes_viewer, sharedResources sharedBuffer) {
        this.mes_viewer = mes_viewer;
        this.sharedBuffer = sharedBuffer;
        this.receiveOrder = new ArrayList<>();
        this.productionOrder = new ArrayList<>();
        this.shippingOrder = new ArrayList<>();
        this.entryWH = new entryWarehouse();
        this.exitWH = new exitWarehouse();
        this.piecesHistories = new ArrayList<>();
    }

    public int getCountdays() {
        return countdays;
    }

    public opcConnection getOpcClient() {
        return opcClient;
    }

    public void setOpcClient(opcConnection opcClient) {
        this.opcClient = opcClient;
    }

    /**
     * If true, will try to syncronize with ERP
     * else will get system millis
     *
     * @param conectToERP
     */
    public void setStartTime(boolean conectToERP) {

        if (conectToERP) {
            syncronized_with_ERP = true;

        } else {
            startTime = System.currentTimeMillis();
            syncronized_with_ERP = false;
        }
    }

    /**
     * Time in seconds
     *
     * @return
     */
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

    public exitWarehouse getExitWH() {
        return exitWH;
    }

    public void addPiece2entryWH(piece newPiece) {
        getEntryWH().addNewPiece(newPiece);
    }

    public void addPiece2exitWH(piece newPiece) {
        getExitWH().addNewPiece(newPiece);
    }

    public ArrayList<piecesHistory> getPiecesHistories() {
        return piecesHistories;
    }

    public void addPiecesHistories(piecesHistory newPieceHistory) {
        getPiecesHistories().add(newPieceHistory);
    }

    // ***** METHODS ******

    public void countTime() {

        if (syncronized_with_ERP)
            startTime = sharedBuffer.getStartTime();

        if (countdays == -1) {
            countdays++;
            System.out.println("Current Day: " + countdays);
        }

        long time = System.currentTimeMillis();

        if (startTime != 0) {
            if (time >= getCurrentTime() * 1000 + startTime + 1000) {
                setCurrentTime((time - startTime) / 1000);

                if ((int) getCurrentTime() / oneDay > countdays) {
                    countdays = (int) getCurrentTime() / oneDay;
                    System.out.println("Current Day: " + countdays);
                }
            }
        }
    }

    public void receiveInternalOrders() {

        String internalOrdersConcat = sharedBuffer.getInternalOrdersConcat();
        if (internalOrdersConcat.equals("null") || internalOrdersConcat.equals("___") || internalOrdersConcat.equals("empty")) {
            return;
        }
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


    public void orderTimes() {
        if (getPiecesHistories().size() == 0)
            return;
        for (piecesHistory curr : getPiecesHistories()) {
            if (curr.getMeanProductionTime() == 0 && curr.getMeanTimeInSFS() == 0) {
                long sumProductionTime = 0;
                int sumSFStime = 0;

                for (piece p : curr.getPieces()) {
                    sumProductionTime += (p.getProductionEnd() - p.getProductionStart()) ;
                    sumSFStime += p.getShippingEnd() - p.getWHarrival();
                }
                curr.setMeanProductionTime((int) sumProductionTime / curr.getQty());
                curr.setMeanTimeInSFS(sumSFStime / curr.getQty());
            }
        }

        // Set na string do MES para depois ser enviado para o ERP
        sharedBuffer.setFinishedOrdersTimes(encodeOrderTimes());
        System.out.println("FinishedOrders: " + sharedBuffer.getFinishedOrdersTimes());

    }

    private String encodeOrderTimes() {

        String finishedOrders = "";

        for (piecesHistory curr : getPiecesHistories()) {
            if (curr.getMeanProductionTime() != 0 && curr.getMeanTimeInSFS() != 0) {
                finishedOrders = finishedOrders.concat(Integer.toString(curr.getManufacturingID()));
                finishedOrders = finishedOrders.concat("@");
                finishedOrders = finishedOrders.concat(Integer.toString(curr.getMeanProductionTime()));
                finishedOrders = finishedOrders.concat("@");
                finishedOrders = finishedOrders.concat(Integer.toString(curr.getMeanTimeInSFS()));
                finishedOrders = finishedOrders.concat("/");
            }
        }

        return finishedOrders;
    }

    public void testMES_zonaA() {

        setStartTime(false);

        addReceiveOrder(new receiveOrder(
                0,
                2,
                1,
                5
        ));
        addReceiveOrder(new receiveOrder(
                1,
                1,
                1,
                4
        ));
        addReceiveOrder(new receiveOrder(
                3,
                2,
                2,
                16
        ));


    }

    public void testMES_zonaC() {

        setStartTime(false);
        int rawMaterialID = 0;
        for (int i = 0; i < 10; i++) {

            if (i == 5)
                rawMaterialID++;

            addPiece2entryWH(new piece(
                    i,
                    rawMaterialID,
                    1
            ));

        }

        productionOrder pOrder = new productionOrder(0, 4, 3, 3);
        ArrayList<rawMaterial> raw = new ArrayList<>();
        raw.add(new rawMaterial(0, 3));
        pOrder.setRawMaterials(raw);

        addProductionOrder(pOrder);

        productionOrder pOrder2 = new productionOrder(1, 5, 3, 5);
        ArrayList<rawMaterial> raw2 = new ArrayList<>();
        raw2.add(new rawMaterial(1, 5));
        pOrder2.setRawMaterials(raw2);

        addProductionOrder(pOrder2);


    }

    public void testMES_zonaE() {
        setStartTime(false);
        for(int i=0;i<5;i++){
            piece p = new piece(
                    i,
                    0,
                    1,
                    5,
                    2,
                    122+i,
                    140+i);

            addPiece2exitWH(p);
        }
        for(int i=0;i<5;i++){
            piece p = new piece(
                    i+5,
                    2,
                    2,
                    4,
                    3,
                    12+i,
                    14+i);

            addPiece2exitWH(p);
        }

//        addProductionOrder(new productionOrder(1,5,1,5));

        shippingOrder shipship = new shippingOrder(1, 2, 5);

        addShippingOrder(shipship);
    }

    // ******** VIEW METHODS *********

    public void displayInternalOrders() {

        getMes_viewer().showInternalOrders(getReceiveOrder(), getProductionOrder(), getShippingOrder());
    }

    public void displayEntryWH() {

        getMes_viewer().showEntryWH(getEntryWH());
    }

    public void displayExitWH() {

        getMes_viewer().showExitWH(getExitWH());
    }
    public void displayPieceHistory() {
        getMes_viewer().showPiecesHistory(getPiecesHistories());
    }


}
