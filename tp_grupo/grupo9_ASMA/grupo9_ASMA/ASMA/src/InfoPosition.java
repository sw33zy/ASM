import jade.core.AID;

import java.util.Objects;

public class InfoPosition implements java.io.Serializable {

    private AID agent;
    private Position position;
    private String tipo;


    public InfoPosition(AID agent, Position position, String t) {
        super();
        this.agent = agent;
        this.position = position;
        this.tipo = t;

    }

    public InfoPosition(AID agent, int x, int y, String t) {
        super();
        this.agent = agent;
        this.position = new Position(x,y);
        this.tipo = t;
    }

    public AID getAgent() {
        return agent;
    }

    public void setAgent(AID agent) {
        this.agent = agent;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setPositionX(int x) {
        this.position.setX(x);
    }

    public void setPositionY(int y) {
        this.position.setY(y);
    }

    @Override
    public String toString() {
        return "InfoPosition [agent=" + agent + ", position=" + position + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoPosition that = (InfoPosition) o;
        return Objects.equals(agent, that.agent) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agent, position, tipo);
    }
}
