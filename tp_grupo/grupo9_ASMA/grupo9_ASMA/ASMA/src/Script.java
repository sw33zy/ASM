import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Script {
    Position canSupEsq;
    Position canSupDir;
    Position canInfEsq;
    Position canInfDir;



    public Script(){
        canSupEsq = new Position(1,1);
        canSupDir = new Position(1,35);
        canInfEsq = new Position(35,1);
        canInfDir = new Position(35,35);

    }


    public double calculaDistancia(Position i , Position j){
        return Math.sqrt((j.getY() - i.getY()) * (j.getY() - i.getY()) + (j.getX() - i.getX()) * (j.getX() - i.getX()));
    }

    public boolean morteCanto(InfoPosition i, Map<Position, Double> mapPosInimigos){
        boolean morto = false;
        if ((i.getPosition().equals(canSupEsq)) && mapPosInimigos.containsKey(new Position(1,2)) && mapPosInimigos.containsKey(new Position(2,1))){
            morto=true;
            System.out.println("ENCURRALADO NO CANTO SUP ESQ");
            System.out.println("Mandei matar o agente:" + i.getAgent().getLocalName());
        }

        if ((i.getPosition().equals(canSupDir)) && mapPosInimigos.containsKey(new Position(1,34)) && mapPosInimigos.containsKey(new Position(2,35))){
            morto=true;

            System.out.println("ENCURRALADO NO CANTO SUP DIR");
            System.out.println("Mandei matar o agente:" + i.getAgent().getLocalName());
        }
        if ((i.getPosition().equals(canInfEsq)) && mapPosInimigos.containsKey(new Position(34, 1)) && mapPosInimigos.containsKey(new Position(35, 2))) {
            morto = true;
            System.out.println("ENCURRALADO NO CANTO INF ESQ");
            System.out.println("Mandei matar o agente:" + i.getAgent().getLocalName());
        }
        if ((i.getPosition().equals(canInfDir)) && mapPosInimigos.containsKey(new Position(34, 35)) && mapPosInimigos.containsKey(new Position(35, 34))) {
            morto = true;
            System.out.println("ENCURRLADO NO CANTO INF DIR");
            System.out.println("Mandei matar o agente:" + i.getAgent().getLocalName());
        }
        return morto;
    }

    public  boolean morteParedes(InfoPosition i, Map<Position, Double> mapPosInimigos){
        boolean morto = false;
        if (
                //PARECE DE CIMA
                ((i.getPosition().getX()==1) && mapPosInimigos.containsKey(new Position(i.getPosition().getX() , i.getPosition().getY()-1))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX() , i.getPosition().getY()+1))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX()+1 , i.getPosition().getY())))

                //PAREDE DE BAIXO
                || ((i.getPosition().getX()==35) && mapPosInimigos.containsKey(new Position(i.getPosition().getX() , i.getPosition().getY()-1))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX() , i.getPosition().getY()+1))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX()-1 , i.getPosition().getY())))

                //PAREDE DA ESQUERDA
                || ((i.getPosition().getY()==1) && mapPosInimigos.containsKey(new Position(i.getPosition().getX()-1 , i.getPosition().getY()))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX()+1 , i.getPosition().getY()))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX() , i.getPosition().getY()+1)))

                //PAREDE DA DIREITA
                || ((i.getPosition().getY()==35) && mapPosInimigos.containsKey(new Position(i.getPosition().getX()-1 , i.getPosition().getY()))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX()+1 , i.getPosition().getY()))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX() , i.getPosition().getY()-1)))

        ){
            morto=true;
            System.out.println("ENCURRALADO NA PAREDE");
            System.out.println("Mandei matar o agente:" + i.getAgent().getLocalName());
        }
        return morto;
    }

    public  boolean morterodeado(InfoPosition i, Map<Position, Double> mapPosInimigos){
        boolean morto = false;
        if ((mapPosInimigos.containsKey(new Position(i.getPosition().getX()+1 , i.getPosition().getY()))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX()-1 , i.getPosition().getY()))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX() , i.getPosition().getY()+1))
                && mapPosInimigos.containsKey(new Position(i.getPosition().getX() , i.getPosition().getY()-1)))){
            morto=true;
            System.out.println("Mandei matar o agente:" + i.getAgent().getLocalName());
        }
        return morto;
    }



    //CALCULA O CAMPO DE VISAO PARA CADA JOGADOR
    public Map<Position,Double> campoVisao( InfoPosition i, ArrayList<InfoPosition> pos_jogadoresInimigos){
        int xa = i.getPosition().getX();
        int ya = i.getPosition().getY();
        int xMin = i.getPosition().getX() - 4;
        int xMax = i.getPosition().getX() + 4;

        int yMin = i.getPosition().getY() - 4;
        int yMax = i.getPosition().getY() + 4;


        Map<Position, Double> mapPosInimigos = new HashMap<>();

        for (InfoPosition j: pos_jogadoresInimigos){

            if ((xMin < j.getPosition().getX()) && ( j.getPosition().getX() < xMax)
                    && (yMin < j.getPosition().getY()) && ( j.getPosition().getY() < yMax)){

                int xb = j.getPosition().getX();
                int yb = j.getPosition().getY();

                Double dist  = Math.sqrt((yb - ya) * (yb - ya) + (xb - xa) * (xb - xa));
                mapPosInimigos.put(j.getPosition(),dist);
            }
        }


        return mapPosInimigos;
    }



    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry: map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public int calculaScore(int nrJogVenc, int nrJogadas){
        int nrJogMortos = 5;
        return ((1000 * nrJogMortos + 500 * nrJogVenc ) / (nrJogadas));
    }























}
