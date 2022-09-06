import jade.core.AID;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class InfoInimigo implements java.io.Serializable {

    private AID agent;
    private Map<Position, Double> mapPosInimigos;


    public InfoInimigo(AID agent,Map<Position, Double> mapPosInimigos) {
        super();
        this.agent = agent;
        this.mapPosInimigos = mapPosInimigos;
    }

    public InfoInimigo() {
        super();
        this.agent= null;
        this.mapPosInimigos = new HashMap<>();
    }

    public AID getAgent() {
        return agent;
    }

    public void setAgent(AID agent) {
        this.agent = agent;
    }

    public void setMapPosInimigos(Map<Position, Double> mapPosInimigos) {
        this.mapPosInimigos = mapPosInimigos;
    }

    public Map<Position, Double> getMapPosInimigos() {
        Map<Position, Double> mapInimigos = new HashMap<>();
        for (Position p : mapPosInimigos.keySet()){
            Double d = mapPosInimigos.get(p);
            mapInimigos.put(p,d);
        }
        return mapInimigos;
    }

    public Map<Position,Double> removerInimigos(Position position ){
        Map<Position,Double> mapInimigos = getMapPosInimigos();
        Map<Position,Double> mapInimigos2 = new HashMap<>();

        for(Map.Entry<Position,Double> m : mapInimigos.entrySet() ){
            if(!m.getKey().equals(position)){
                mapInimigos2.put(m.getKey(),m.getValue() );
            }
        }
        return mapInimigos2;
    }


    @Override
    public String toString() {
        return "InfoInimigo{" +
                "agent=" + agent +
                ", mapPosInimigos=" + mapPosInimigos +
                '}';
    }



}
