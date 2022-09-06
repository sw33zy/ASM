import java.util.Objects;

public class Position implements java.io.Serializable {

    private int x,y;

    public Position(Position p){
        this.x=p.getX();
        this.y= p.getY();
    }

    public Position(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return "Position [x=" + x + ", y=" + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y); }
}