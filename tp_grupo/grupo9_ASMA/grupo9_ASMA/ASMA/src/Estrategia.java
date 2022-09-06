import jade.core.AID;

import java.io.Serializable;
import java.util.ArrayList;

public class Estrategia implements  java.io.Serializable {
    private int tipo; //0 -> stutter; 1-> estrategia
    private AID agente;
    private Position pos;
    private ArrayList<Position> pos_Inimigos;


    public Estrategia(int t, AID agent, Position position, ArrayList<Position> pos) {
        super();
        this.tipo = t;
        this.agente = agent;
        this.pos = position;
        this.pos_Inimigos=pos;

    }

    public AID getAgent() {
        return agente;
    }

    public void setAgent(AID agent) {
        this.agente = agent;
    }

    public Position getPosition() {
        return pos;
    }

    public void setPosition(Position position) {
        this.pos = position;
    }

    public int getTipo() { return tipo; }

    public void setTipo(int tipo) { this.tipo = tipo; }

    public ArrayList<Position> getPos_Inimigos() {
        ArrayList<Position> pos = new ArrayList<>(pos_Inimigos);
        return pos;
    }

    @Override
    public String toString() {
        return "Estrategia{" +
                "agent=" + agente.getLocalName() +
                ", tipo=" + tipo +
                ", pos=" + pos +
                ",pos_Inimigos=" + pos_Inimigos +
                '}';
    }
}