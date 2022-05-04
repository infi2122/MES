package Controllers;

import Models.piece;
import Models.receiveOrder;
import Models.warehouse;

import java.util.ArrayList;

public class zoneA extends Thread {

    // NAO ESQUECER DE DAR RESET ALGURES
    private boolean recvType1 = false;
    private boolean recvType2 = false;

    private int piece_counter = 0;
    private boolean WH_full = false;
    private boolean old_sensor = false;
    private final int one_day = 60;
    /**
     * PRECISO SABER A QUANTIDADE DE PEÇAS A RECEBER, E A QUANTIDADE DE PEÇAS RESERVADAS A CADA ENCOMENDA
     */
    private receiveOrder curr_receiveOrder;

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
        this.WH_full = WH_full;
    }


    private MES mes;

    public zoneA(MES mes1) {
        this.mes = mes1;
    }

    @Override
    public void run() {

        synchronized (mes) {
            if (!entryWH_isFull()) {
                System.out.println("Prioridade 1: " + isRecvType1() + " Prioridade 2: " + isRecvType2());
                if ( !isRecvType1() && !isRecvType2() ) {
                    curr_receiveOrder = setRecvType();
                }
                if (curr_receiveOrder != null) {
                    System.out.println("Diferente de NULL");
                    entryCarpet();
                }
            }
        }
    }

    /**
     * Based on the current day and the reception orders, will define which type of pieces will receive
     */
    public receiveOrder setRecvType() {

        long currDay = mes.getCurrentTime();

        for (receiveOrder curr : mes.getReceiveOrder()) {
            if (curr.getStartDate() == currDay / 60) {
                int currPieceType = curr.getPieceType();
                if (currPieceType == 6 || currPieceType == 8) {
                    setRecvType1(true);
                    System.out.println("RECV Type 1");
                } else {
                    setRecvType2(true);
                    System.out.println("RECV Type 2");
                }
                return curr;
            }
        }
        return null;
    }

    public boolean entryWH_isFull() {

        if (mes.getEntryWH().getPieces().size() == mes.getEntryWH().getMAXIMUM_CAPACITY()) {
            setWH_full(true);
            mes.getOpcClient().writeInt("wh_entry_full", "GVL", 1);
            return true;
        }
        mes.getOpcClient().writeInt("wh_entry_full", "GVL", 0);
        return false;
    }

    public void entryCarpet() {

        boolean sensor = mes.getOpcClient().readBool("W1in0_sensor", "IO");
        System.out.println("Sensor TP3: " + sensor);

        if (piece_counter < curr_receiveOrder.getReservedQty()) {

            if (sensor && !old_sensor) {

                piece newPiece = new piece(piece_counter, curr_receiveOrder.getOrderID(), curr_receiveOrder.getStartDate() * one_day);
                if (curr_receiveOrder.getOrderID() != -1) {
                    newPiece.setExpectedType(curr_receiveOrder.getPieceType());
                }
                if (!mes.getEntryWH().getPieces().contains(newPiece)) {
                    mes.addPiece2entryWH(newPiece);
                    piece_counter++;
                }
            }
        } else {
            piece_counter = 0;
            setRecvType1(false);
            setRecvType2(false);
            mes.getReceiveOrder().remove(curr_receiveOrder);
            curr_receiveOrder = null;
        }
        System.out.println("Piece counter: " + piece_counter + " Curr QTY: " +curr_receiveOrder.getReservedQty() );
        old_sensor = sensor;
    }


}


