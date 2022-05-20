package Controllers;

import Models.piece;
import Models.receiveOrder;

public class zoneA extends Thread {

    // NAO ESQUECER DE DAR RESET ALGURES
    private boolean recvType1 = false;
    private boolean recvType2 = false;
    private boolean WH_full = false;

    private int global_piece_cnt_for_id = 0;

    private final int one_day = 60;

    private receiveOrder curr_receiveOrder;
    private piece convP1 = null;
    private piece convP2 = null;
    private piece convLoad0 = null;
    private piece convWH = null;

    private boolean oldConvP1_sensor = false;
    private boolean oldConvP2_sensor = false;
    private boolean oldLoad0_sensor = false;
    private boolean oldConvWH_sensor = false;

    /* ********************************************************************************** */
    // NOS SET posso enviar para o OPC os valores, porque em cada set é para alterar os booleans
    public boolean isRecvType1() {
        return recvType1;
    }

    public void setRecvType1(boolean recvType1) {
        if (recvType1)
            mes.getOpcClient().writeBool("prio_p1", "GVL", 1);
        else
            mes.getOpcClient().writeBool("prio_p1", "GVL", 0);

        this.recvType1 = recvType1;
    }

    public boolean isRecvType2() {
        return recvType2;
    }

    public void setRecvType2(boolean recvType2) {
        if (recvType2)
            mes.getOpcClient().writeBool("prio_p2", "GVL", 1);
        else
            mes.getOpcClient().writeBool("prio_p2", "GVL", 0);

        this.recvType2 = recvType2;
    }

    public boolean isWH_full() {
        return WH_full;
    }

    public void setWH_full(boolean WH_full) {
        if (WH_full)
            mes.getOpcClient().writeBool("wh_entry_full", "GVL", 1);
        else
            mes.getOpcClient().writeBool("wh_entry_full", "GVL", 0);

        this.WH_full = WH_full;
    }

    private MES mes;

    public zoneA(MES mes1) {
        this.mes = mes1;
    }

    @Override
    public void run() {

        synchronized (mes) {

            // Verificar so RE dos tapetes de entrada
            //    Se sim, entao criar nova instancia de p1 e p2
            // No RE do Load 0 passa a peça de p1 ou p2 para o tapete rotativo para nao ser perdida a info
            detectEntryConveyor();

            if (!entryWH_isFull()) {
                if (!isRecvType1() && !isRecvType2()) {
                    curr_receiveOrder = setRecvType();
                }
                if (curr_receiveOrder != null) {
                    entry2WH();
                }
            }
        }
    }

    /**
     * Based on the current day and the reception orders, will define which type of pieces will receive
     */
    public receiveOrder setRecvType() {

        long currTime = mes.getCurrentTime();

        for (receiveOrder curr : mes.getReceiveOrder()) {
            if (curr.getArrivalDate() <= currTime / one_day && curr.getQty() > 0) {
                int currPieceType = curr.getPieceType();
                if (currPieceType == 1) {
                    setRecvType1(true);
                    //System.out.println("RECV Type 1");
                } else {
                    setRecvType2(true);
                    //System.out.println("RECV Type 2");
                }
                return curr;
            }
        }
        return null;
    }

    /**
     *
     * @return TRUE if warehouse full
     */

    public boolean entryWH_isFull() {

        if (mes.getEntryWH().getPieces().size() == mes.getEntryWH().getMAXIMUM_CAPACITY()) {
            setWH_full(true);
            return true;
        }
        setWH_full(false);
        return false;
    }

    /**
     * Put piece into entry warehouse
     */
    public void entry2WH() {

        boolean convWH_sensor = mes.getOpcClient().readBool("W1in0_sensor", "IO");
        if (curr_receiveOrder.getQty() > 0) {

            if (convWH_sensor && !oldConvWH_sensor) {
                convWH = convLoad0;
                mes.addPiece2entryWH(convWH);
                curr_receiveOrder.setQty(curr_receiveOrder.getQty() - 1);
//                mes.displayEntryWH();

            }
        }
        if (curr_receiveOrder.getQty() == 0) {
            setRecvType1(false);
            setRecvType2(false);
            curr_receiveOrder = null;
        }

        oldConvWH_sensor = convWH_sensor;
    }

    /**
     * Detect piece in zone A since input conveyors until the last before warehouse
     */
    public void detectEntryConveyor() {

        boolean convP1_sensor = mes.getOpcClient().readBool("Load1_sensor", "IO");
        boolean convP2_sensor = mes.getOpcClient().readBool("Load2_sensor", "IO");
        boolean load0_sensor = mes.getOpcClient().readBool("Load0_sensor", "IO");

        // RE de convLoad_0
        if (!oldLoad0_sensor && load0_sensor) {
            if (isRecvType1()) {
                convLoad0 = new piece(
                        convP1.getPieceID(),
                        curr_receiveOrder.getRawMaterialOrderID(),
                        convP1.getWHarrival() * one_day
                        );
                convP1 = null;
            }
            if (isRecvType2()) {
                convLoad0 = new piece(
                        convP2.getPieceID(),
                        curr_receiveOrder.getRawMaterialOrderID(),
                        convP2.getWHarrival() * one_day
                );
                convP2 = null;
            }
        }

        // RE de convP1
        if (!oldConvP1_sensor && convP1_sensor) {
            convP1 = new piece(global_piece_cnt_for_id, (int) mes.getCurrentTime() / 60);
            global_piece_cnt_for_id++;
        }

        // RE de convP2
        if (!oldConvP2_sensor && convP2_sensor) {
            convP2 = new piece(global_piece_cnt_for_id, (int) mes.getCurrentTime() / 60);
            global_piece_cnt_for_id++;
        }
        oldConvP1_sensor = convP1_sensor;
        oldConvP2_sensor = convP2_sensor;
        oldLoad0_sensor = load0_sensor;

    }


}