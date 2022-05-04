package Controllers;
import Models.productionOrder;
import Models.piece;
import Models.receiveOrder;

import java.util.ArrayList;

public class zoneC extends Thread{
    private MES mes;
    private final int MAXIMUM_CAPACITY = 6;
    private ArrayList<piece> pieces = new ArrayList<>();

    private int pieces_inside;
    private productionOrder currentPO_entry_wh;

    public zoneC(MES mes1) {
        this.mes = mes1;
    }

    public ArrayList<piece> getPieces() {
        return pieces;
    }

    public void addNewPiece(piece newPiece) {
        getPieces().add(newPiece);
    }

    public boolean is_W1in1_free(){
        return mes.getOpcClient().readBool("W1in1_free", "GVL");
    }
    public boolean is_zoneC_full(){
        return pieces.size() == MAXIMUM_CAPACITY;
    }

    @Override
    public void run() {

        synchronized (mes) {



            //filtrar encomenda com maior quantidade
            if( currentPO_entry_wh == null || currentPO_entry_wh.getQty() == currentPO_entry_wh.getPieces_in_sfs() ) {
                for (productionOrder curr : mes.getProductionOrder() ) {
                    if( curr.getStart_time() <= mes.getCurrentTime()/60  && curr.getQty()>curr.getPieces_in_sfs() ){

                        //primeira iteração
                        if(currentPO_entry_wh==null) currentPO_entry_wh = curr;
                        else if( curr.getQty() > currentPO_entry_wh.getQty() ){
                            currentPO_entry_wh = curr;
                        }
                    }
                }
            }

            //production order encontrada,
            //enquanto as peças nao estiverem todas dentro do armazem, retira mais peças
            if( )
            if( currentPO_entry_wh.getQty() > currentPO_entry_wh.getPieces_in_sfs() ){


            }







        }
    }
}
