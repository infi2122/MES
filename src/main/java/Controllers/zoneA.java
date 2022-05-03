package Controllers;

import Models.piece;
import Models.receiveOrder;
import Models.warehouse;

import java.util.ArrayList;

public class zoneA extends Thread {

    // NAO ESQUECER DE DAR RESET ALGURES
    private boolean recvType1 = false;
    private boolean recvType2 = false;

    private boolean WH_full = false;

    /**
     * PRECISO SABER A QUANTIDADE DE PEÇAS A RECEBER, E A QUANTIDADE DE PEÇAS RESERVADAS A CADA ENCOMENDA
     */
    private int currOrderID;
    private int currOrderQty;

    /* ********************************************************************************** */
    // NOS SET posso enviar para o OPC os valores, porque em cada set é para alterar os booleans
    public boolean isRecvType1() {
        return recvType1;
    }

    public void setRecvType1(boolean recvType1) {
        this.recvType1 = recvType1;
    }

    public boolean isRecvType2() {
        return recvType2;
    }

    public void setRecvType2(boolean recvType2) {
        this.recvType2 = recvType2;
    }

    public boolean isWH_full() {
        return WH_full;
    }

    public void setWH_full(boolean WH_full) {
        this.WH_full = WH_full;
    }

    public int getCurrOrderID() {
        return currOrderID;
    }

    public void setCurrOrderID(int currOrderID) {
        this.currOrderID = currOrderID;
    }

    public int getCurrOrderQty() {
        return currOrderQty;
    }

    public void setCurrOrderQty(int currOrderQty) {
        this.currOrderQty = currOrderQty;
    }


    private MES mes;

    public zoneA(MES mes1){
        this.mes = mes1;
    }

    @Override
    public void run() {

        synchronized (mes) {
            setRecvType();
            entryWH_isFull();

            //entryCarpet();
        }
    }

    /**
     * Based on the current day and the reception orders, will define which type of pieces will receive
     * AO COMEÇAR A RECEBER É PRECISO VER SE A ENCOMENDA JÁ FOI DESCARREGADA, PORQUE SENAO ENCRAVA SEMPRE
     * PARA A 1a RECV ORDER A DESCARREGAR -> UMA HIPOTESE E VER SE JA HA PEÇAS DESSA ORDEM NO ARMAZEM
     * POSSO REMOVER A PEÇA DO VETOR RECV ORDERS PQ O ERP SO ENVIA DO DIA N+1 E N+2, LOGO QD FOR DE N -> N+1
     * JÁ NAO ENVIA A ORDERM DE NOVO !!! *********** CONFIRMAR ************** !!!
     */
    public void setRecvType() {

        long currDay = mes.getCurrentTime();

        for (receiveOrder curr : mes.getReceiveOrder()) {
            if (curr.getStartDate() == currDay) {
                int div = curr.getOrderID() % 100;
                if (div == 6 || div == 8) {

                    setRecvType2(true);
                    System.out.println("RECV Type 2");
                }
                else{
                    setRecvType1(true);
                    System.out.println("RECV Type 1");
                }

            }
        }
    }

    public void entryWH_isFull() {

        if (mes.getEntryWH().getPieces().size() == mes.getEntryWH().getMAXIMUM_CAPACITY()) {
            setWH_full(true);
        }
    }

    public void entryCarpet() {

        int n_stored = 0;

        while (n_stored < getCurrOrderQty()) {
            if (mes.ReadBools("TP3")) {

                piece newPiece = new piece(getCurrOrderID(), (int) mes.getCurrentTime());
                mes.addPiece2entryWH(newPiece);
                n_stored++;

            }

        }
        if(isRecvType1())
            setRecvType1(false);
        else
            setRecvType2(false);


    }


}


