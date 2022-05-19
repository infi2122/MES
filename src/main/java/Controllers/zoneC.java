package Controllers;

import Models.piece;
import Models.productionOrder;
import Models.rawMaterial;

import javax.xml.bind.SchemaOutputResolver;
import java.util.ArrayList;
import java.util.Iterator;

public class zoneC<MCT_table> extends Thread {

    private ArrayList<piece> piecesOnFloor = new ArrayList<>();

    private final int MAXIMUM_CAPACITY = 6;
    private static int oneDay = 60;
    private int curr_PO_day = 0;
    private boolean old_W2in1Sensor;

    private MES mes;

    // Variáveis de estado para os rising/falling edges //
    private boolean old_M11Working;
    private boolean old_M12Working;
    private boolean old_M13Working;
    private boolean old_M21Working;
    private boolean old_M22Working;
    private boolean old_M23Working;

    private boolean old_M11NotWorking;
    private boolean old_M12NotWorking;
    private boolean old_M13NotWorking;
    private boolean old_M21NotWorking;
    private boolean old_M22NotWorking;
    private boolean old_M23NotWorking;


    // Variáveis para contagem de tempo de trabalho das máquinas
    private int M11InitTime = 0;
    private int M12InitTime = 0;
    private int M13InitTime = 0;
    private int M21InitTime = 0;
    private int M22InitTime = 0;
    private int M23InitTime = 0;

    public zoneC(MES mes1) {
        this.mes = mes1;
    }

    public boolean is_W1in1_free() {
        return mes.getOpcClient().readBool("W1in1_free", "GVL");
    }

    public boolean is_zoneC_full() {
        return piecesOnFloor.size() == MAXIMUM_CAPACITY;
    }

    int cnt = 0;

    @Override
    public void run() {

        synchronized (mes) {

            defineMCT();
            set_curr_PO_day();
            unloadToSFS();
            storeInExitWH();
            updateExitWH_state();

            // Funções relativas a estatísticas
            updateMachineCountersAndWorkingTime();

            if (cnt / 100 == 1) {
                System.out.println("** Pieces on the zone C Floor **");
                for (piece curr : piecesOnFloor) {

                    System.out.println(
                            "order id: " + curr.getOrderID()
                                    + " piece type " + curr.getExpectedType()
                                    + " piece id " + curr.getPieceID()
                    );
                }
                System.out.println("********************************");
                cnt = 0;
            }
            cnt++;
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
            return;
        }
        if (currDay % 2 == 0)
            return;

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
        if (type >= 0) {
            setMCT(MCT_table[type]);
        }
        return;


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

        // Percorre todas as production orders
        if (i < mes.getProductionOrder().size()) {

            productionOrder currProdOrder = mes.getProductionOrder().get(i);
            // Seleciona as production orders do dia atual e anteriores, se houverem
            if (!is_zoneC_full() && is_W1in1_free()) {

                // Cabem peças na zona C e o tapete W1in1 está livre
                if (currProdOrder.getStartDate() <= curr_PO_day && currProdOrder.getStartDate() <= currDay) {

                    for (rawMaterial curr : currProdOrder.getRawMaterials()) {

                        // Se a production order necessitar do raw material
                        if (curr.getQty_used() > 0) {

                            //System.out.println("Raw Material id: " + curr.getRawMaterialID());
                            piece newPieceOnFloor = searchPieceInEntryWH(curr.getRawMaterialID());

                            newPieceOnFloor.setOrderID(currProdOrder.getManufacturingID());
                            newPieceOnFloor.setExpectedType(currProdOrder.getFinalType());
                            newPieceOnFloor.setProductionStart(mes.getCurrentTime());
                            piecesOnFloor.add(newPieceOnFloor);
                            //  int[] transformations = [actualType, T1, T2, T3]
                            int[] transformations = getTransformations(newPieceOnFloor.getExpectedType());
                            curr.setQty_used(curr.getQty_used() - 1);
//                            System.out.println("manufacturingID: " + newPieceOnFloor.getOrderID() + " tipo atual: " + transformations[0]
//                                    + " T1: " + transformations[1] + " T2: " + transformations[2] + " T3: " + transformations[3]
//                                    + " piece ID: " + newPieceOnFloor.getPieceID() + " rawMaterialID: " + newPieceOnFloor.getRawMaterialID());
                            send_piece_code_to_opcua(
                                    newPieceOnFloor.getOrderID(),
                                    transformations[0],
                                    transformations[1],
                                    transformations[2],
                                    transformations[3],
                                    newPieceOnFloor.getPieceID());
                            break;
                        }
                    }
                }
                i++;
            }

        }
        if (i == mes.getProductionOrder().size()) {
            i = 0;
        }

    }

    private piece searchPieceInEntryWH(int rawMaterialID) {

        piece returnPiece = null;

        ArrayList<piece> list = mes.getEntryWH().getPieces();
        Iterator<piece> iterador = list.iterator();

        while (iterador.hasNext()) {
            piece temp = iterador.next();
            if (temp.getRawMaterialID() == rawMaterialID) {

                returnPiece = new piece(temp);
                returnPiece.setRawMaterialID(rawMaterialID);
                iterador.remove();
                break;
            }
        }

        return returnPiece;

    }

    private void send_piece_code_to_opcua(int orderID, int curr_type, int T1, int T2, int T3, int pieceID) {
        mes.getOpcClient().writeInt("id_encomenda", "GVL", orderID);
        mes.getOpcClient().writeInt("tipo_atual", "GVL", curr_type);
        mes.getOpcClient().writeInt("T1", "GVL", T1);
        mes.getOpcClient().writeInt("T2", "GVL", T2);
        mes.getOpcClient().writeInt("T3", "GVL", T3);
        mes.getOpcClient().writeInt("id_peca", "GVL", pieceID);

        mes.getOpcClient().writeInt("wh_entry_r1", "GVL", curr_type);
        mes.getOpcClient().writeBool("W1in1_free", "GVL", 0);


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

    private void updateExitWH_state() {
        if (mes.getExitWH().is_WH_full()) {
            mes.getOpcClient().writeBool("wh_out_full", "GVL", 1);
        } else {
            mes.getOpcClient().writeBool("wh_out_full", "GVL", 0);
        }
    }

    private void storeInExitWH() {
        if (RE_PieceInW2in1()) {
            int id = mes.getOpcClient().readInt("id_peca_out", "GVL");

            // Retirar peça da ZonaC
            // Criar peça nova para inserir no armazem
            piece pieceDone = searchPieceInZoneC(id);

            // Colocar nova peça na array do armazém
            if (pieceDone != null) mes.getExitWH().addNewPiece(pieceDone);
            else System.out.println("A peça que tentei inserir na exitWH é nula!");
        }
    }

    private boolean RE_PieceInW2in1() {
        boolean new_W2in1Sensor = mes.getOpcClient().readBool("PieceInW2in1", "GVL");
        boolean RE = !old_W2in1Sensor && new_W2in1Sensor;
        old_W2in1Sensor = new_W2in1Sensor;

        return RE;
    }

    private piece searchPieceInZoneC(int pieceID) {

        piece returnPiece = null;

        ArrayList<piece> list = piecesOnFloor;
        Iterator<piece> iterador = list.iterator();

        while (iterador.hasNext()) {
            piece temp = iterador.next();
            if (temp.getPieceID() == pieceID) {
                returnPiece = new piece(
                        temp.getPieceID(),
                        temp.getRawMaterialID(),
                        temp.getOrderID(),
                        temp.getExpectedType(),
                        temp.getWHarrival(),
                        temp.getProductionStart(),
                        mes.getCurrentTime()
                );

                iterador.remove();
                break;
            }
        }

        return returnPiece;

    }

    private void set_curr_PO_day() {
        curr_PO_day = Integer.MAX_VALUE;
        for (productionOrder currPO : mes.getProductionOrder()) {
//            Se a PO ainda nao esta completa e ja passou o dia de começar a fazer
            if (!currPO.is_Done() && currPO.getStartDate() <= mes.getCountdays()) {

//                O curr_PO_day vai ser a data mais baixa dos POs incompletos
                if( curr_PO_day > currPO.getStartDate() ){
                    curr_PO_day = currPO.getStartDate();
                }
            }

        }
    }


    // ********** Funções relativas à contagem de peças e de tempos nas máquinas ********* //

    // Detetores de Rising Edge
    private boolean RE_M11Working() {
        boolean new_M11Working = mes.getOpcClient().readBool("M11Working", "GVL");
        boolean RE = new_M11Working && !old_M11Working;
        old_M11Working = new_M11Working;

        return RE;
    }
    private boolean RE_M12Working() {
        boolean new_M12Working = mes.getOpcClient().readBool("M12Working", "GVL");
        boolean RE = new_M12Working && !old_M12Working;
        old_M12Working = new_M12Working;

        return RE;
    }
    private boolean RE_M13Working() {
        boolean new_M13Working = mes.getOpcClient().readBool("M13Working", "GVL");
        boolean RE = new_M13Working && !old_M13Working;
        old_M13Working = new_M13Working;

        return RE;
    }
    private boolean RE_M21Working() {
        boolean new_M21Working = mes.getOpcClient().readBool("M21Working", "GVL");
        boolean RE = new_M21Working && !old_M21Working;
        old_M21Working = new_M21Working;

        return RE;
    }
    private boolean RE_M22Working() {
        boolean new_M22Working = mes.getOpcClient().readBool("M22Working", "GVL");
        boolean RE = new_M22Working && !old_M22Working;
        old_M22Working = new_M22Working;

        return RE;
    }
    private boolean RE_M23Working() {
        boolean new_M23Working = mes.getOpcClient().readBool("M23Working", "GVL");
        boolean RE = new_M23Working && !old_M23Working;
        old_M23Working = new_M23Working;

        return RE;
    }

    // Detetores de Falling Edge
    private boolean FE_M11Working() {
        boolean new_M11NotWorking = mes.getOpcClient().readBool("M11NotWorking", "GVL");
        boolean FE = new_M11NotWorking && !old_M11NotWorking;
        old_M11NotWorking = new_M11NotWorking;

        return FE;
    }
    private boolean FE_M12Working() {
        boolean new_M12NotWorking = mes.getOpcClient().readBool("M12NotWorking", "GVL");
        boolean FE = new_M12NotWorking && !old_M12NotWorking;
        old_M12NotWorking = new_M12NotWorking;

        return FE;
    }
    private boolean FE_M13Working() {
        boolean new_M13NotWorking = mes.getOpcClient().readBool("M13NotWorking", "GVL");
        boolean FE = new_M13NotWorking && !old_M13NotWorking;
        old_M13NotWorking = new_M13NotWorking;

        return FE;
    }
    private boolean FE_M21Working() {
        boolean new_M21NotWorking = mes.getOpcClient().readBool("M21NotWorking", "GVL");
        boolean FE = new_M21NotWorking && !old_M21NotWorking;
        old_M21NotWorking = new_M21NotWorking;

        return FE;
    }
    private boolean FE_M22Working() {
        boolean new_M22NotWorking = mes.getOpcClient().readBool("M22NotWorking", "GVL");
        boolean FE = new_M22NotWorking && !old_M22NotWorking;
        old_M22NotWorking = new_M22NotWorking;

        return FE;
    }
    private boolean FE_M23Working() {
        boolean new_M23NotWorking = mes.getOpcClient().readBool("M23NotWorking", "GVL");
        boolean FE = new_M23NotWorking && !old_M23NotWorking;
        old_M23NotWorking = new_M23NotWorking;

        return FE;
    }


    private void updateMachineCountersAndWorkingTime() {
        // Máquina M11
        if (RE_M11Working()) {
            M11InitTime = (int) mes.getCurrentTime();
            System.out.println("Foi marcado o tempo de inicio!");
        }
        if (FE_M11Working() && M11InitTime > 0) {
            int finalTime = (int) mes.getCurrentTime();
            mes.incrementM11WorkTime(finalTime - M11InitTime);
            M11InitTime = 0;

            System.out.println("Foi marcado o tempo final e incrementado o contador de tempo!");

            // Lê o tipo de peça que foi feito
            int pieceType = mes.getOpcClient().readInt("M11PieceMadeType", "GVL");


            // Incrementa o contador desse tipo de peça para a máquina M11
            mes.incrementM11Counter(pieceType);
        }

        // Máquina M12
        if (RE_M12Working()) {
            M12InitTime = (int) mes.getCurrentTime();

        }
        if (FE_M12Working() && M12InitTime > 0) {
            int finalTime = (int) mes.getCurrentTime();
            mes.incrementM12WorkTime(finalTime - M12InitTime);
            M12InitTime = 0;


            // Lê o tipo de peça que foi feito
            int pieceType = mes.getOpcClient().readInt("M12PieceMadeType", "GVL");

            // Incrementa o contador desse tipo de peça para a máquina M12
            mes.incrementM12Counter(pieceType);
        }

        // Máquina M13
        if (RE_M13Working()) {
            M13InitTime = (int) mes.getCurrentTime();
        }
        if (FE_M13Working() && M13InitTime > 0) {
            int finalTime = (int) mes.getCurrentTime();
            mes.incrementM13WorkTime(finalTime - M13InitTime);
            M13InitTime = 0;

            // Lê o tipo de peça que foi feito
            int pieceType = mes.getOpcClient().readInt("M13PieceMadeType", "GVL");

            // Incrementa o contador desse tipo de peça para a máquina M13
            mes.incrementM13Counter(pieceType);
        }

        // Máquina M21
        if (RE_M21Working()) {
            M21InitTime = (int) mes.getCurrentTime();
        }
        if (FE_M21Working() && M21InitTime > 0) {
            int finalTime = (int) mes.getCurrentTime();
            mes.incrementM21WorkTime(finalTime - M21InitTime);
            M21InitTime = 0;

            // Lê o tipo de peça que foi feito
            int pieceType = mes.getOpcClient().readInt("M21PieceMadeType", "GVL");

            // Incrementa o contador desse tipo de peça para a máquina M21
            mes.incrementM21Counter(pieceType);
        }

        // Máquina M22
        if (RE_M22Working()) {
            M22InitTime = (int) mes.getCurrentTime();
        }
        if (FE_M22Working() && M22InitTime > 0) {
            int finalTime = (int) mes.getCurrentTime();
            mes.incrementM22WorkTime(finalTime - M22InitTime);
            M22InitTime = 0;

            // Lê o tipo de peça que foi feito
            int pieceType = mes.getOpcClient().readInt("M22PieceMadeType", "GVL");

            // Incrementa o contador desse tipo de peça para a máquina M22
            mes.incrementM22Counter(pieceType);
        }

        // Máquina M23
        if (RE_M23Working()) {
            M23InitTime = (int) mes.getCurrentTime();
        }
        if (FE_M23Working() && M23InitTime > 0) {
            int finalTime = (int) mes.getCurrentTime();
            mes.incrementM23WorkTime(finalTime - M23InitTime);
            M23InitTime = 0;

            // Lê o tipo de peça que foi feito
            int pieceType = mes.getOpcClient().readInt("M23PieceMadeType", "GVL");

            // Incrementa o contador desse tipo de peça para a máquina M23
            mes.incrementM23Counter(pieceType);
        }
    }

}
