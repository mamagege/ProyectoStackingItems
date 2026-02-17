import java.util.ArrayList;

public class Cup {

    private int id;                
    private int size;                 
    private String color;

    private ArrayList<Rectangle> parts;
    private boolean isVisible;


    private static final int BLOCK_SIZE = 25;
    
    private int xPosition; 
    private int yPosition; 

    public Cup(int id, int size, String color) {
        this.id = id;
        this.size = size;
        this.color = color;
        this.parts = new ArrayList<>();
        this.isVisible = false;
        

        this.xPosition = 0;
        this.yPosition = 0;

        buildCup(); 
    }
    
    /**
     * Construye la taza en la posición actual (xPosition, yPosition)
     */
    private void buildCup() {
 
        if (parts.size() > 0) {
            makeInvisible();
            parts.clear();
        }
        
        int heightInBlocks = getHeight(); // (2*size - 1)
        int widthInBlocks = size;         // El ancho es 'size' bloques
        
        // 1. Crear la BASE (Fondo)
        // La base va en la parte inferior.
        // Coordenada Y de la base = yPosition + (altura - 1)*25
        int baseIndexY = heightInBlocks - 1;
        
        for (int c = 0; c < widthInBlocks; c++) {
            Rectangle r = new Rectangle();
            r.changeColor(color);
            // Mover a la posición absoluta
            r.moveHorizontal(xPosition + (c * BLOCK_SIZE));
            r.moveVertical(yPosition + (baseIndexY * BLOCK_SIZE));
            parts.add(r);
        }

        // 2. Crear las PAREDES (Izquierda y Derecha)
        // Van desde arriba (0) hasta justo antes de la base
        for (int r = 0; r < heightInBlocks - 1; r++) {
            // Pared Izquierda (Columna 0)
            Rectangle left = new Rectangle();
            left.changeColor(color);
            left.moveHorizontal(xPosition); // Borde izquierdo
            left.moveVertical(yPosition + (r * BLOCK_SIZE));
            parts.add(left);

            // Pared Derecha (Última columna)
            // Solo si el tamaño > 1 (si es 1, es solo base)
            if (size > 1) {
                Rectangle right = new Rectangle();
                right.changeColor(color);
                right.moveHorizontal(xPosition + ((widthInBlocks - 1) * BLOCK_SIZE));
                right.moveVertical(yPosition + (r * BLOCK_SIZE));
                parts.add(right);
            }
        }
    }
    
    /**
     * Mueve la taza a una posición absoluta (X, Y).
     * Reconstruye la taza en la nueva posición para evitar errores de delta.
     */
    public void moveTo(int targetX, int targetY) {
        // Opción segura: Actualizar coordenadas y reconstruir
        // Esto garantiza que visualmente esté donde los números dicen que está.
        boolean wasVisible = isVisible;
        if (wasVisible) makeInvisible();
        
        this.xPosition = targetX;
        this.yPosition = targetY;
        
        buildCup();
        
        if (wasVisible) makeVisible();
    }

    public int getPixelWidth() {
        return size * BLOCK_SIZE;
    }

    public int getRealPixelHeight() {
        return getHeight() * BLOCK_SIZE;
    }

    // Altura en bloques lógicos
    public int getHeight() {
        return (2 * size - 1);
    }
    
    public int getId() { return id; }
    public int getSize() { return size; }
    
    // Retorna la coordenada Y superior real
    public int getY() { return yPosition; }
    public int getX() { return xPosition; }

    public void makeVisible() {
        isVisible = true;
        for (Rectangle r : parts) {
            r.makeVisible();
        }
    }

    public void makeInvisible() {
        isVisible = false;
        for (Rectangle r : parts) {
            r.makeInvisible();
        }
    }
}