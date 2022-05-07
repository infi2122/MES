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
            storeInExitWH();
            updateExitWH_state();

            for (piece curr : piecesOnFloor) {
                System.out.println(
                        "order id: " + curr.getOrderID()
                                + " piece type " + curr.getExpectedType()
                                + " piece id " + curr.getPieceID()
                );
            }
            System.out.println("-------------------------");
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
            if (type > 0) {
                //NAO ACREDITO QUE ERA ISTO AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa
                setMCT(MCT_table[type]);
            }

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

        // Percorre todas as production orders
        if (i < mes.getProductionOrder().size()) {

            productionOrder currProdOrder = mes.getProductionOrder().get(i);

            // Seleciona as production orders do dia atual e anteriores, se houverem
            if (currProdOrder.getStartDate() <= currDay) {

                // Cabem peças na zona C e o tapete W1in1 está livre
                if (!is_zoneC_full() && is_W1in1_free()) {
                    System.out.println("------------HELLO-----------");
                    for (rawMaterial curr : currProdOrder.getRawMaterials()) {

                        // Se a production order necessitar do raw material
                        if (curr.getQty_used() > 0) {
                            System.out.println("Raw Material id: " + curr.getRawMaterialID());
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
                    i++;
                }
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
        //checkar se tem peça pronta no W2in1
        /*if (mes.getOpcClient().readBool("W2in1_sensor", "IO")) {

            //checkar se o exit_wh nao esta cheio
            if (!mes.getExitWH().is_WH_full()) {

                //pesquisar peça na lista de peças da zona C pelo ID
                int id = mes.getOpcClient().readInt("id_peca_out", "GVL");

                //tirou da arraylist de peças
                piece pieceDone = searchPieceInZoneC(id);
                //System.out.println("AQUI ID_PECA_OUT"+id);
                if (pieceDone != null) {
                    //System.out.println("ENCONTROU CRL");
                    mes.getExitWH().addNewPiece(pieceDone);
                    mes.getOpcClient().writeBool("clear_to_store_in_exitWH", "GVL", 1);
                }
                return;


            }
        }*/

        int id = mes.getOpcClient().readInt("id_peca_out", "GVL");
        boolean signal = mes.getOpcClient().readBool("request_to_store_in_exitWH", "GVL");
        // Peça detetada em W2in1, para guardar no armazém
        if (id >= 0 && signal) {

            // Retirar peça da ZonaC
            // Criar peça nova para inserir no armazem
            piece pieceDone = searchPieceInZoneC(id);

            // Colocar nova peça na array do armazém
            if (pieceDone != null) mes.getExitWH().addNewPiece(pieceDone);
            else System.out.println("A peça em W2in1 é nula!");;

            mes.getOpcClient().writeBool("clear_to_store_in_exitWH", "GVL", 1);
            mes.getOpcClient().writeBool("request_to_store_in_exitWH", "GVL", 0);

        }
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

}
