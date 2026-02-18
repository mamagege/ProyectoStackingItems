import java.util.ArrayList;

/**
 * Representa una taza formada por bloques rectangulares.
 * @author Juan Gaitán and Oscar Lasso
 */
public class Cup {

    private int id;
    private int size;
    private String color;

    private ArrayList<Rectangle> parts;
    private ArrayList<Lid> lids;

    private boolean isVisible;

    private static final int BLOCK_SIZE = 25;

    private int xPosition;
    private int yPosition;

    /**
     * Construye una taza con identificador, tamaño y color.
     */
    public Cup(int id, int size, String color) {
        this.id = id;
        this.size = size;
        this.color = color;
        this.parts = new ArrayList<>();
        this.lids = new ArrayList<>();
        this.isVisible = false;
        this.xPosition = 0;
        this.yPosition = 0;
        buildCup();
    }

    /**
     * Construye la geometría de la taza en la posición actual.
     */
    private void buildCup() {
        parts.clear();

        int heightBlocks = getHeight();
        int widthBlocks = size;

        int baseYIndex = heightBlocks - 1;

        for (int c = 0; c < widthBlocks; c++) {
            Rectangle r = new Rectangle();
            r.changeColor(color);
            r.moveHorizontal(xPosition + (c * BLOCK_SIZE));
            r.moveVertical(yPosition + (baseYIndex * BLOCK_SIZE));
            parts.add(r);
        }

        for (int r = 0; r < heightBlocks - 1; r++) {
            Rectangle left = new Rectangle();
            left.changeColor(color);
            left.moveHorizontal(xPosition);
            left.moveVertical(yPosition + (r * BLOCK_SIZE));
            parts.add(left);

            if (size > 1) {
                Rectangle right = new Rectangle();
                right.changeColor(color);
                right.moveHorizontal(xPosition + ((widthBlocks - 1) * BLOCK_SIZE));
                right.moveVertical(yPosition + (r * BLOCK_SIZE));
                parts.add(right);
            }
        }
    }

    /**
     * Mueve la taza a una posición absoluta y reposiciona sus tapas.
     */
    public void moveTo(int targetX, int targetY) {
        boolean wasVisible = isVisible;
        if (wasVisible) makeInvisible();

        this.xPosition = targetX;
        this.yPosition = targetY;

        buildCup();

        for (int i = 0; i < lids.size(); i++) {
            Lid lid = lids.get(i);
            int lidX = xPosition + BLOCK_SIZE;
            int lidY = yPosition - BLOCK_SIZE - (i * BLOCK_SIZE);
            lid.moveTo(lidX, lidY);
        }

        if (wasVisible) makeVisible();
    }

    /**
     * Hace visible la taza y sus tapas.
     */
    public void makeVisible() {
        isVisible = true;
        for (Rectangle r : parts) r.makeVisible();
        for (Lid lid : lids) lid.makeVisible();
    }

    /**
     * Hace invisible la taza y sus tapas.
     */
    public void makeInvisible() {
        isVisible = false;
        for (Rectangle r : parts) r.makeInvisible();
        for (Lid lid : lids) lid.makeInvisible();
    }

    /**
     * Agrega una tapa a la taza.
     */
    public void addLid(Lid lid) {
        lids.add(lid);
    }

    /**
     * Elimina la tapa superior.
     */
    public void removeTopLid() {
        if (!lids.isEmpty()) {
            Lid lid = lids.remove(lids.size() - 1);
            lid.makeInvisible();
        }
    }

    /**
     * Elimina todas las tapas asociadas.
     */
    public void removeAllLids() {
        for (Lid lid : lids) lid.makeInvisible();
        lids.clear();
    }

    public boolean hasLids() {
        return !lids.isEmpty();
    }

    public ArrayList<Lid> getLids() {
        return lids;
    }

    public int getHeight() {
        return (2 * size - 1);
    }

    public int getRealPixelHeight() {
        return getHeight() * BLOCK_SIZE;
    }

    public int getPixelWidth() {
        return size * BLOCK_SIZE;
    }

    public int getId() { return id; }

    public int getSize() { return size; }

    public int getX() { return xPosition; }

    public int getY() { return yPosition; }

    public String getColor() { return color; }
}
