import java.util.ArrayList;

/**
 * Representa una tapa construida por bloques.
 * Cada tapa tiene tamaño (N-2) x 1.
 * 
 * @author  Oscar Lasso and Juan Diego Gaitán
 */

public class Lid {

    private int id;
    private int size;
    private String color;

    private ArrayList<Rectangle> parts;
    private boolean isVisible;

    private static final int BLOCK_SIZE = 50;

    public Lid(int id, int size, String color) {
        this.id = id;
        this.size = size;
        this.color = color;
        this.parts = new ArrayList<>();
        this.isVisible = false;

        buildLid();
    }

    private void buildLid() {
        for (int i = 0; i < size; i++) {
            Rectangle r = new Rectangle();
            r.moveHorizontal(i * BLOCK_SIZE);
            r.moveVertical(0);
            r.changeColor(color);
            parts.add(r);
        }
    }

    public void makeVisible() {
        for (Rectangle r : parts) {
            r.makeVisible();
        }
        isVisible = true;
    }

    public void makeInvisible() {
        for (Rectangle r : parts) {
            r.makeInvisible();
        }
        isVisible = false;
    }

    public void moveVertical(int distance) {
        for (Rectangle r : parts) {
            r.moveVertical(distance * BLOCK_SIZE);
        }
    }

    public void moveHorizontal(int distance) {
        for (Rectangle r : parts) {
            r.moveHorizontal(distance * BLOCK_SIZE);
        }
    }

    public int getHeight() {
        return 1;
    }

    public int getId() {
        return id;
    }
}
