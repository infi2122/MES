package Controllers;

import Models.piece;
import Models.piecesHistory;
import Models.productionOrder;
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

    private boolean old_pusher1_pieceDelivered;
    private boolean old_pusher2_pieceDelivered;
    private boolean old_pusher3_pieceDelivered;

    @Override
    public void run() {
        synchronized (mes) {

            handlePlacementOfPieces();
            updatePlacingPieceFlag();

            setShippingTimmingsAndCounters();

        }
    }

    public void handlePlacementOfPieces() {

        ArrayList<shippingOrder> allShippingOrders = mes.getShippingOrder(); // Obter todas as shipping orders

        for (shippingOrder titanic : allShippingOrders) { // Verificar se alguma shipping order é para hoje
            if (titanic.getStartDate() <= mes.getCountdays() && allThePiecesArrived(titanic)) { // Se order for para hoje

                ArrayList<piece> piecesInExitWH = mes.getExitWH().getPieces(); // Obter todas as peças no armazem de saída

                // Percorre armazem de saída em busca de peças com o OrderID igual ao da shippingOrder
                for (piece bolacha : piecesInExitWH) {
                    if (bolacha.getOrderID() == titanic.getManufacturingID() && searchForPieceInExportList(bolacha) == 0) {
                        piecesToExport.add(bolacha); // Armazena as peças a serem retiradas nesta arraylist
                    }
                }
            }
        }

        // Vai correr em todos os ciclos do programa
        // Verifica as condições para alterar o registo que retira peça do armazem de saída
        if (piecesToExport.size() > 0 && piecesInZoneE.size() <= 12 && !placingPiece) {
            placingPiece = true;
            int placedPieceId = placePieceInConveyor(); // Coloca essas peças no tapete
        }

    }


    public int placePieceInConveyor() {
        // Função que literalmente altera o registo no plc para colocar peça
        // Apenas transmite o pieceID para o plc, o resto guarda numa arrayList de peças chamada piecesInZoneE
        piece p = piecesToExport.get(0); // Remove-se sempre a primeira peça na lista
        sendPieceCodeToPLC(p.getExpectedType(), p.getPieceID()); // Código enviado para o plc

        p.setShippingStart((int) mes.getCurrentTime()); // Define o tempo de início da exportação

        piecesInZoneE.add(p);
        mes.getExitWH().getPieces().remove(p); // Remove peça da arrayList do armazém
        piecesToExport.remove(0); // Remove peça da lista temporária de peças a exportar

        return p.getPieceID();
    }

    private void sendPieceCodeToPLC(int PieceType, int PieceID) {
        mes.getOpcClient().writeInt("w2out2_r1", "GVL", PieceType);
        mes.getOpcClient().writeInt("PE_0", "GVL", PieceID);
    }

    private int searchForPieceInExportList(piece p) {
        for (piece box : piecesToExport) {
            if (box.getPieceID() == p.getPieceID()) return 1;
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

    public void setShippingTimmingsAndCounters() {
        // Verificar se pusher1 entregou peça
        if (RE_pusher1_pieceDelivered()) {
            // Resetar a flag
            mes.getOpcClient().writeBool("pusher1_pieceDelivered", "GVL", 0);

            // Ler id da peça entregue
            int pieceDelivered_id = mes.getOpcClient().readInt("pusher1_pieceDeliveredID", "GVL");

            // Guardar tempo de entrega dessa peça no piecesInZoneE
            setPieceDeliveryTime(pieceDelivered_id, (int) mes.getCurrentTime());

            // Incrementar contador do pusher1
            incrementPusherCounters(1, pieceDelivered_id);

            // Enviar Peça do piecesInZoneE para o piecesHistory (Ideia do Daniel)
            sendPieceToPieceHistory(pieceDelivered_id);

        }


        // Verificar se pusher2 entregou peça
        if (RE_pusher2_pieceDelivered()) {
            // Resetar a flag
            mes.getOpcClient().writeBool("pusher2_pieceDelivered", "GVL", 0);

            // Ler id da peça entregue
            int pieceDelivered_id = mes.getOpcClient().readInt("pusher2_pieceDeliveredID", "GVL");

            // Guardar tempo de entrega dessa peça no piecesInZoneE
            setPieceDeliveryTime(pieceDelivered_id, (int) mes.getCurrentTime());

            // Incrementar contador do pusher2
            incrementPusherCounters(2, pieceDelivered_id);

            // Enviar Peça do piecesInZoneE para o piecesHistory (Ideia do Daniel)
            sendPieceToPieceHistory(pieceDelivered_id);
        }


        // Verificar se pusher3 entregou peça
        if (RE_pusher3_pieceDelivered()) {
            // Resetar a flag
            mes.getOpcClient().writeBool("pusher3_pieceDelivered", "GVL", 0);

            // Ler id da peça entregue
            int pieceDelivered_id = mes.getOpcClient().readInt("pusher3_pieceDeliveredID", "GVL");

            // Guardar tempo de entrega dessa peça no piecesInZoneE
            setPieceDeliveryTime(pieceDelivered_id, (int) mes.getCurrentTime());

            // Incrementar contador do pusher3
            incrementPusherCounters(3, pieceDelivered_id);

            // Enviar Peça do piecesInZoneE para o piecesHistory (Ideia do Daniel)
            sendPieceToPieceHistory(pieceDelivered_id);
        }
    }

    private boolean RE_pusher1_pieceDelivered() {
        boolean new_pusher1_pieceDelivered = mes.getOpcClient().readBool("pusher1_pieceDelivered", "GVL");
        boolean RE = !old_pusher1_pieceDelivered && new_pusher1_pieceDelivered;
        old_pusher1_pieceDelivered = new_pusher1_pieceDelivered;

        return RE;
    }
    private boolean RE_pusher2_pieceDelivered() {
        boolean new_pusher2_pieceDelivered = mes.getOpcClient().readBool("pusher2_pieceDelivered", "GVL");
        boolean RE = !old_pusher2_pieceDelivered && new_pusher2_pieceDelivered;
        old_pusher2_pieceDelivered = new_pusher2_pieceDelivered;

        return RE;
    }
    private boolean RE_pusher3_pieceDelivered() {
        boolean new_pusher3_pieceDelivered = mes.getOpcClient().readBool("pusher3_pieceDelivered", "GVL");
        boolean RE = !old_pusher3_pieceDelivered && new_pusher3_pieceDelivered;
        old_pusher3_pieceDelivered = new_pusher3_pieceDelivered;

        return RE;
    }

    private void setPieceDeliveryTime(int pieceId, int time) {
        for (piece biscoito : piecesInZoneE) {
            if (biscoito.getPieceID() == pieceId) {
                biscoito.setShippingEnd(time);
            }
        }
    }

    private void sendPieceToPieceHistory(int pieceId) {
        piece p = null;
        for (piece bolacha : piecesInZoneE) {
            if (bolacha.getPieceID() == pieceId) {
                p = bolacha;
                break;
            }
        }

        // Procura a pieceHistory com o mesmo orderID que a peça
        for (piecesHistory pH : mes.getPiecesHistories()) {
            if (p == null) throw new AssertionError();
            if (pH.getManufacturingID() == p.getOrderID()) {
                pH.addNewPieces(p);
                piecesInZoneE.remove(p);
                return;
            }
        }

        // Se não encontrar pieceHistory, cria um novo
        // Procurar production order correspondente ao orderID
        // Retirar qty dessa production order
        int prodOrderQty = 0;

        for (productionOrder pO : mes.getProductionOrder()) {
            assert p != null;
            if (pO.getManufacturingID() == p.getOrderID()) {
                prodOrderQty = pO.getQty();
                break;
            }
        }

        // Criar nova pieceHistory, adicionar a peça a essa nova pH, remover peça da zonaE, adicionar pH ao MES
        assert p != null;
        piecesHistory newPiecesHistory = new piecesHistory(p.getOrderID(), prodOrderQty);
        newPiecesHistory.addNewPieces(p);
        mes.addPiecesHistories(newPiecesHistory);
        piecesInZoneE.remove(p);

    }

    private void incrementPusherCounters(int pusher, int pieceID) {
        int pieceType = 0;
        // Vai buscar o pieceType
        for (piece pudim : piecesInZoneE) {
            if (pudim.getPieceID() == pieceID) {
                pieceType = pudim.getExpectedType();
            }
        }

        // incrementa o contador do pusher indicado
        switch(pusher) {
            case 1 -> mes.incrementPusher1Counter(pieceType);
            case 2 -> mes.incrementPusher2Counter(pieceType);
            case 3 -> mes.incrementPusher3Counter(pieceType);
            default -> System.out.println("A variável pusher tem um valor diferente de 1, 2 e 3, wtf?!");
        }
    }
}

