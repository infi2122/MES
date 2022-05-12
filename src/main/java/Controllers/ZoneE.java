package Controllers;

import Models.piece;
import Models.shippingOrder;

import java.util.ArrayList;

public class ZoneE extends Thread {

    private MES mes;

    public ZoneE(MES mes1) {
        this.mes = mes1;
    }

    private ArrayList<piece> piecesInZoneE = new ArrayList<>();
    private ArrayList<piece> piecesToExport = new ArrayList<>();

    private boolean placingPiece = false;
    private boolean old_w2out2Free;

    @Override
    public void run() {
        synchronized (mes) {

            int pieceQtyZoneE = handlePlacementOfPieces();
            getShippingTimmings();

//            System.out.println("N pieces in Zone E: " + pieceQtyZoneE);

            updatePlacingPieceFlag();
        }
    }

    public int handlePlacementOfPieces() {

        ArrayList<shippingOrder> allShippingOrders = mes.getShippingOrder(); // Obter todas as shipping orders

        for (shippingOrder titanic : allShippingOrders) { // Verificar se alguma shipping order é para hoje
            if (titanic.getStartDate() <= mes.getCountdays() && allThePiecesArrived(titanic) ) { // Se order for para hoje

                ArrayList<piece> piecesInExitWH = mes.getExitWH().getPieces(); // Obter todas as peças no armazem de saída

                // Percorre armazem de saída em busca de peças com o OrderID igual ao da shippingOrder
                for (piece bolacha : piecesInExitWH) {
                    if (bolacha.getOrderID() == titanic.getManufacturingID() && searchForPieceInExportList(bolacha) == 0) {
                        piecesToExport.add(bolacha); // Armazena as peças a serem retiradas nesta arraylist
                        System.out.println("Added piece ID = " + bolacha.getPieceID() + " to piecesToExport");
                    }
                }
            }
        }

        // Vai correr em todos os ciclos do programa
        // Verifica as condições para alterar o registo que retira peça do armazem de saída
        if (piecesToExport.size() > 0 && piecesInZoneE.size() <= 12 && !placingPiece) {
            placingPiece = true;
            int placedPieceId = placePieceInConveyor(); // Coloca essas peças no tapete
            System.out.println("Piece ID = " + placedPieceId + " was placed in Zone E!");
        }

        return piecesInZoneE.size();

    }


    public int placePieceInConveyor() {
        // Função que literalmente altera o registo no plc para colocar peça
        // Apenas transmite o pieceID para o plc, o resto guarda numa arrayList de peças chamada piecesInZoneE
        piece p = piecesToExport.get(0); // Remove-se sempre a primeira peça na lista
        sendPieceCodeToPLC(p.getExpectedType()); // Código enviado para o plc
        //markW2out1AsFull();

        piecesInZoneE.add(p);
        mes.getExitWH().getPieces().remove(p); // Remove peça da arrayList do armazém
        piecesToExport.remove(0); // Remove peça da lista temporária de peças a exportar

        return p.getPieceID();
    }

    private boolean isW2out1Free() {
        return mes.getOpcClient().readBool("w2out2_free", "GVL");
    }

    private void markW2out1AsFull() {
        mes.getOpcClient().writeBool("w2out2_free", "GVL", 0);
    }

    private void sendPieceCodeToPLC(int PieceType) {
        mes.getOpcClient().writeInt("w2out2_r1", "GVL", PieceType);
    }

    private int searchForPieceInExportList(piece p) {
        for (piece box : piecesToExport) {
            if (box.getPieceID() == p.getPieceID()) return 1;
        }
        return 0;
    }

    private int searchForPieceInZoneE(piece p) {
        for (piece caixote : piecesInZoneE) {
            if (caixote.getPieceID() == p.getPieceID()) return 1;

        }
        return 0;
    }

    private boolean RE_W2out2Free() {
        boolean new_w2out2Free = mes.getOpcClient().readBool("w2out2_free", "GVL");

        boolean RE = !old_w2out2Free && new_w2out2Free;

        old_w2out2Free = new_w2out2Free;

        return RE;
    }

    private void updatePlacingPieceFlag() {
        if (RE_W2out2Free()) placingPiece = false;
    }


    public int getShippingTimmings() {
        return 0;
    }

    private boolean allThePiecesArrived(shippingOrder sOrder) {

        ArrayList<piece> piecesInExitWH = mes.getExitWH().getPieces(); // Obter todas as peças no armazem de saída
        int piecesIWant = 0;

        // Percorre armazem de saída em busca de peças com o OrderID igual ao da shippingOrder
        for (piece bolacha : piecesInExitWH) {
            if (bolacha.getOrderID() == sOrder.getManufacturingID() && searchForPieceInExportList(bolacha) == 0) {
                piecesIWant++;
            }
            if (sOrder.getQty() == piecesIWant) {
                return true;
            }

        }
        return false;
    }
}

