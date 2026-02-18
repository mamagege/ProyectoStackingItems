import java.util.ArrayList;

/**
 * Representa una tapa asociable a una taza.
 * @author Juan Gaitán and Oscar LasLidso
 */
public class Lid {

    private int id;
    private int size;
    private String color;

    private ArrayList<Rectangle> parts;
    private boolean isVisible;

    private static final int BLOCK_SIZE = 25;

    private int xPosition;
    private int yPosition;

    /**
     * Construye una tapa con identificador, tamaño y color.
     */
    public Lid(int id, int size, String color) {
        this.id = id;
        this.size = size;
        this.color = color;
        this.parts = new ArrayList<>();
        this.isVisible = false;
        buildLid();
    }

    /**
     * Construye la geometría de la tapa.
     */
    private void buildLid() {
        parts.clear();

        for (int i = 0; i < size; i++) {
            Rectangle r = new Rectangle();
            r.changeColor(color);
            parts.add(r);
        }
    }

    /**
     * Mueve la tapa a una posición absoluta.
     */
    public void moveTo(int targetX, int targetY) {
        boolean wasVisible = isVisible;
        if (wasVisible) makeInvisible();

        this.xPosition = targetX;
        this.yPosition = targetY;

        for (int i = 0; i < parts.size(); i++) {
            Rectangle r = parts.get(i);
            r.moveHorizontal(targetX + (i * BLOCK_SIZE));
            r.moveVertical(targetY);
        }

        if (wasVisible) makeVisible();
    }

    /**
     * Hace visible la tapa.
     */
    public void makeVisible() {
        isVisible = true;
        for (Rectangle r : parts) r.makeVisible();
    }

    /**
     * Hace invisible la tapa.
     */
    public void makeInvisible() {
        isVisible = false;
        for (Rectangle r : parts) r.makeInvisible();
    }

    public int getId() { return id; }

    public int getSize() { return size; }

    public int getY() { return yPosition; }
}
