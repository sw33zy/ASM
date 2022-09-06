import jade.core.AID;

import java.util.ArrayList;

public class InfoEquipa implements java.io.Serializable {

    private AID agent;
    private ArrayList<InfoPosition> pos_jogadores;

    public InfoEquipa(AID agent, ArrayList<InfoPosition> positions) {
        super();
        this.agent = agent;
        this.pos_jogadores = positions;

    }


    public AID getAgent() {
        return agent;
    }

    public void setAgent(AID agent) {
        this.agent = agent;
    }


    public ArrayList<InfoPosition> getPos_jogadores() {
        return new ArrayList<>(pos_jogadores);
    }

    public void setPos_jogadores(ArrayList<InfoPosition> pos) {
        this.pos_jogadores.addAll(pos);
    }
}
