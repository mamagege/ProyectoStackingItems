import java.util.ArrayList;

/**
 * Representa una taza construida por bloques.
 * Cada taza tiene tamaño NxN.
 * 
 * @author  Oscar Lasso and Juan Diego Gaitán
 */
public class Cup {

    private int id;               // Identificador único
    private int size;                 // Tamaño NxN
    private String color;

    private ArrayList<Rectangle> parts;
    private boolean isVisible;

    private static final int BLOCK_SIZE = 50;
    
    private Lid lid;

    /**
     * Constructor
     */
    public Cup(int id, int size, String color) {
        this.id = id;
        this.size = size;
        this.color = color;
        this.parts = new ArrayList<>();
        this.isVisible = false;

        buildCup(); // Solo construye, NO dibuja
    }
    
    /**
     * Construye la forma de la taza
     */
    private void buildCup() {

        // Columna izquierda
        for (int i = 1; i < size; i++) {
            Rectangle r = new Rectangle();
            r.moveHorizontal(0);
            r.moveVertical(i * BLOCK_SIZE);
            r.changeColor(color);
            parts.add(r);
        }

        // Base
        for (int i = 0; i < size; i++) {
            Rectangle r = new Rectangle();
            r.moveHorizontal(i * BLOCK_SIZE);
            r.moveVertical((size) * BLOCK_SIZE);
            r.changeColor(color);
            parts.add(r);
        }

        // Columna derecha
        for (int i = 1; i < size; i++) {
            Rectangle r = new Rectangle();
            r.moveHorizontal((size-1) * BLOCK_SIZE);
            r.moveVertical(i * BLOCK_SIZE);
            r.changeColor(color);
            parts.add(r);
        }
    }

    public void placeLid(Lid lid) {
        this.lid = lid;
    }

    public void removeLid() {
        this.lid = null;
    }

    public boolean hasLid() {
        return lid != null;
    }

    public Lid getLid() {
        return lid;
    }
    
    /**
     * Hace visible la taza
     */
    public void makeVisible() {
        for (Rectangle r : parts) {
            r.makeVisible();
        }
        isVisible = true;
    }

    /**
     * Hace invisible la taza
     */
    public void makeInvisible() {
        for (Rectangle r : parts) {
            r.makeInvisible();
        }
        isVisible = false;
    }

    /**
     * Mueve verticalmente toda la taza
     */
    public void moveVertical(int distance) {
        distance = distance * BLOCK_SIZE;
        for (Rectangle r : parts) {
            r.moveVertical(distance);
        }
        if (lid != null) {
            lid.moveVertical(distance);
        }
    }

    /**
     * Mueve horizontalmente toda la taza
     */
    public void moveHorizontal(int distance) {
        distance = distance * BLOCK_SIZE;
        for (Rectangle r : parts) {
            r.moveHorizontal(distance);
        }
        if (lid != null) {
            lid.moveHorizontal(distance);
        }
}
    

    /**
     * Retorna la altura real de la taza
     */
    public int getHeight() {
        return (2 * size - 1);
    }

    /**
     * Retorna número identificador
     */
    public int getId() {
        return id;
    }
    
    /**
     * Retorna color identificador
     */
    public String getColor() {
        return color;
    }
}
