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

    private int[] pusher1Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] pusher2Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] pusher3Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int[] machine11Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] machine12Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] machine13Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] machine21Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] machine22Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] machine23Counter = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int machine11WorkingTime = 0;
    private int machine12WorkingTime = 0;
    private int machine13WorkingTime = 0;
    private int machine21WorkingTime = 0;
    private int machine22WorkingTime = 0;
    private int machine23WorkingTime = 0;

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
     * If true, will try to synchronize with ERP
     * else will get system millis
     * @param connectToERP If TRUE synchronize
     */
    public void setStartTime(boolean connectToERP) {

        if (connectToERP) {
            syncronized_with_ERP = true;

        } else {
            startTime = System.currentTimeMillis();
            syncronized_with_ERP = false;
        }
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

    /**
     * Counts every second since the program started
     */
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

    /**
     * Check if the internalOrders from sharedResources have new orders that came from ERP, via TCP
     */
    public void receiveInternalOrders() {

        String internalOrdersConcat = sharedBuffer.getInternalOrdersConcat();
        if (internalOrdersConcat.equals("null") || internalOrdersConcat.equals("___") || internalOrdersConcat.equals("empty")) {
            return;
        }
        //System.out.println(internalOrdersConcat);
        addNewInternalOrders(internalOrdersConcat);

    }

    /**
     * Add new orders to MES
     * @param internalOrders internalOrders encoded that was received from ERP
     */
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
                                        Integer.parseInt(last[5 + 2*k]),
                                        Integer.parseInt(last[6 + 2*k])));
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

    /**
     * Get production times for every productions
     */
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
        //System.out.println("FinishedOrders: " + sharedBuffer.getFinishedOrdersTimes());

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

    public void displayCounters() {
        int[][] mCounter = new int[6][9];
        for (int i = 0; i < 9; i++) mCounter[0][i] = getM11Counter(i+1);
        for (int i = 0; i < 9; i++) mCounter[1][i] = getM12Counter(i+1);
        for (int i = 0; i < 9; i++) mCounter[2][i] = getM13Counter(i+1);
        for (int i = 0; i < 9; i++) mCounter[3][i] = getM21Counter(i+1);
        for (int i = 0; i < 9; i++) mCounter[4][i] = getM22Counter(i+1);
        for (int i = 0; i < 9; i++) mCounter[5][i] = getM23Counter(i+1);

        int[] totals = new int[6];
        totals[0] = getMachineXXTotalCounter(11);
        totals[1] = getMachineXXTotalCounter(12);
        totals[2] = getMachineXXTotalCounter(13);
        totals[3] = getMachineXXTotalCounter(21);
        totals[4] = getMachineXXTotalCounter(22);
        totals[5] = getMachineXXTotalCounter(23);

        mes_viewer.showCounters(mCounter, totals);
    }

    public void displayMachinesTimmings() {
        int[] timmings = new int[6];
        timmings[0] = getM11WorkingTime();
        timmings[1] = getM12WorkingTime();
        timmings[2] = getM13WorkingTime();
        timmings[3] = getM21WorkingTime();
        timmings[4] = getM22WorkingTime();
        timmings[5] = getM23WorkingTime();

        mes_viewer.showMachineTimmings(timmings);
    }

    public void displayPusherCounters() {
        int[][] pCounter = new int[3][9];
        for (int i = 0; i < 9; i++) pCounter[0][i] = getPusher1Counter(i+1);
        for (int i = 0; i < 9; i++) pCounter[1][i] = getPusher2Counter(i+1);
        for (int i = 0; i < 9; i++) pCounter[2][i] = getPusher3Counter(i+1);

        int[] pTotals = new int[3];
        pTotals[0] = getPusherXTotalCounter(1);
        pTotals[1] = getPusherXTotalCounter(2);
        pTotals[2] = getPusherXTotalCounter(3);

        mes_viewer.showPusherCounters(pCounter, pTotals);
    }

    // ****************** Funções relacionadas com os contadores de peças na zona E **************** //

    // ---- Retornam o novo valor da contagem --- //
    public int incrementPusher1Counter(int pieceType) {
        return ++pusher1Counter[pieceType-1];
    }
    public int incrementPusher2Counter(int pieceType) {
        return ++pusher2Counter[pieceType-1];
    }
    public int incrementPusher3Counter(int pieceType) {
        return ++pusher3Counter[pieceType-1];
    }

    // ---- Retornam o valor da contagem --- //
    public int getPusher1Counter(int pieceType) {
        return pusher1Counter[pieceType-1];
    }
    public int getPusher2Counter(int pieceType) {
        return pusher2Counter[pieceType-1];
    }
    public int getPusher3Counter(int pieceType) {
        return pusher3Counter[pieceType-1];
    }

    // ---- Retorna a quantidade total de peças entregues pelo pusher X ---- //
    public int getPusherXTotalCounter(int X) {
        int counter = 0;

        switch (X) {
            case 1 -> {
                for (int i : pusher1Counter) counter += i;
                return counter;
            }
            case 2 -> {
                for (int i : pusher2Counter) counter += i;
                return counter;
            }
            case 3 -> {
                for (int i : pusher3Counter) counter += i;
                return counter;
            }
            default -> System.out.println("That pusher does not exist! Insert value between 1 and 3!");
        }
        return counter;
    }

    // ******************************************************************************************* //



    // ******************** Funções relacionadas com os contadores de peças maquinadas ******************* //

    // ---- Retornam o novo valor da contagem ---- //
    public int incrementM11Counter(int pieceType) {
        return ++machine11Counter[pieceType-1];
    }
    public int incrementM12Counter(int pieceType) {
        return ++machine12Counter[pieceType-1];
    }
    public int incrementM13Counter(int pieceType) {
        return ++machine13Counter[pieceType-1];
    }
    public int incrementM21Counter(int pieceType) {
        return ++machine21Counter[pieceType-1];
    }
    public int incrementM22Counter(int pieceType) {
        return ++machine22Counter[pieceType-1];
    }
    public int incrementM23Counter(int pieceType) {
        return ++machine23Counter[pieceType-1];
    }

    // ---- Retornam o valor da contagem ---- //
    public int getM11Counter(int pieceType) {
        return machine11Counter[pieceType-1];
    }
    public int getM12Counter(int pieceType) {
        return machine12Counter[pieceType-1];
    }
    public int getM13Counter(int pieceType) {
        return machine13Counter[pieceType-1];
    }
    public int getM21Counter(int pieceType) {
        return machine21Counter[pieceType-1];
    }
    public int getM22Counter(int pieceType) {
        return machine22Counter[pieceType-1];
    }
    public int getM23Counter(int pieceType) {
        return machine23Counter[pieceType-1];
    }

    // ---- Retorna a quantidade total de peças operadas por uma máquina ---- //
    // X corresponde à máquina X no MCT, ou seja:
    // X = {0,1,2,3,4,5} <=> M = {M11, M12, M13, M23, M21, M22}
    public int getMachineXXTotalCounter(int XX) {
        int counter = 0;

        switch(XX) {
            case 11 -> {
                for (int i : machine11Counter) counter += i;
                return counter;
            }
            case 12 -> {
                for (int i : machine12Counter) counter += i;
                return counter;
            }
            case 13 -> {
                for (int i : machine13Counter) counter += i;
                return counter;
            }
            case 23 -> {
                for (int i : machine23Counter) counter += i;
                return counter;
            }
            case 21 -> {
                for (int i : machine21Counter) counter += i;
                return counter;
            }
            case 22 -> {
                for (int i : machine22Counter) counter += i;
                return counter;
            }
            default -> {
                System.out.println("A máquina referida não existe!");
                return -1;
            }

        }
    }

    // ************************************************************************************************* //


    // ********** Funções relativas à contagem do tempo de trabalho de cada máquina ********** //

    // Incrementam o tempo que as máquinas estiveram a operar
    public void incrementM11WorkTime(int delta){
        machine11WorkingTime += delta;
    }
    public void incrementM12WorkTime(int delta){
        machine12WorkingTime += delta;
    }
    public void incrementM13WorkTime(int delta){
        machine13WorkingTime += delta;
    }
    public void incrementM21WorkTime(int delta){
        machine21WorkingTime += delta;
    }
    public void incrementM22WorkTime(int delta){
        machine22WorkingTime += delta;
    }
    public void incrementM23WorkTime(int delta){
        machine23WorkingTime += delta;
    }

    // Retornam os tempos que as máquinas estiveram a operar
    public int getM11WorkingTime() { return machine11WorkingTime;}
    public int getM12WorkingTime() { return machine12WorkingTime;}
    public int getM13WorkingTime() { return machine13WorkingTime;}
    public int getM21WorkingTime() { return machine21WorkingTime;}
    public int getM22WorkingTime() { return machine22WorkingTime;}
    public int getM23WorkingTime() { return machine23WorkingTime;}

    // ************************************************************************************** //

}
