import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Representa una torre de tazas y tapas.
 * Controla apilamiento, anidamiento, altura máxima,
 * ordenamiento y representación visual.
 * @author Juan Gaitán and Oscar Lasso
 */
public class Tower {

    //Constantes de posicionamiento

    private static final int TOWER_X = 500;
    private static final int BASE_Y = 600;
    private static final int BLOCK_SIZE = 25;

    //Estructuras principales

    private ArrayList<Cup> cups;
    private HashMap<Integer, Lid> lids;
    private ArrayList<Rectangle> heightMarks;

    //Estado de la torre
       

    private boolean isVisible;
    private int width;
    private int maxHeight;

    /**
     * Construye una torre con ancho visual y altura máxima.
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        cups = new ArrayList<>();
        heightMarks = new ArrayList<>();
        drawHeightMarks();
        isVisible = false;
    }

    //Inserción de elementos

    /**
     * Agrega una tapa a la taza indicada.
     * Valida altura máxima antes de insertarla.
     */
    public void pushLid(int id) {

        Cup cup = getCupById(id);
        if (cup == null) return;

        int lidSize = cup.getSize() - 2;
        if (lidSize <= 0) return;

        Lid lid = new Lid(id, lidSize, getColorForSize(id));

        int projectedHeight = getCurrentHeight() + BLOCK_SIZE;

        if (projectedHeight > maxHeight) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No cabe la tapa, supera la altura máxima.");
            }
            return;
        }

        cup.addLid(lid);

        int index = cup.getLids().size() - 1;
        int lidX = cup.getX() + BLOCK_SIZE;
        int lidY = cup.getY() - BLOCK_SIZE - (index * BLOCK_SIZE);

        lid.moveTo(lidX, lidY);

        if (isVisible) lid.makeVisible();
    }

    /**
     * Agrega una taza respetando reglas de anidamiento y altura máxima.
     */
    public void pushCup(int size) {

        int id = size;

        for (Cup c : cups) {
            if (c.getId() == id) {
                if (isVisible) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "La taza " + id + " ya existe.");
                }
                return;
            }
        }

        String color = getColorForSize(size);
        Cup newCup = new Cup(id, size, color);

        int widthPx = newCup.getPixelWidth();
        int targetX = TOWER_X - (widthPx / 2);
        int targetY;

        Cup topCup = cups.isEmpty() ? null : cups.get(cups.size() - 1);

        if (topCup == null) {
            targetY = BASE_Y - newCup.getRealPixelHeight();
        } else {

            boolean topHasLid = topCup.hasLids();
            boolean fitsInside = newCup.getSize() < topCup.getSize();

            if (!topHasLid && fitsInside) {
                int floorY = topCup.getY() + topCup.getRealPixelHeight();
                int insideFloor = floorY - BLOCK_SIZE;
                targetY = insideFloor - newCup.getRealPixelHeight();
            } else {
                int topY = getTopY();
                targetY = topY - newCup.getRealPixelHeight();
            }
        }

        int projectedHeight = BASE_Y - targetY;

        if (projectedHeight > maxHeight) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No se puede agregar la taza. Supera la altura máxima.");
            }
            return;
        }

        newCup.moveTo(targetX, targetY);

        if (isVisible) newCup.makeVisible();

        cups.add(newCup);
    }

    //Eliminación de elementos
       

    /**
     * Elimina una taza por identificador y reconstruye la torre.
     */
    public void removeCup(int id) {

        Cup toRemove = null;

        for (Cup c : cups) {
            if (c.getId() == id) {
                toRemove = c;
                break;
            }
        }

        if (toRemove == null) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No existe la taza " + id);
            }
            return;
        }

        if (toRemove.hasLids()) {
            for (Lid lid : toRemove.getLids()) {
                lid.makeInvisible();
            }
            toRemove.getLids().clear();
        }

        toRemove.makeInvisible();
        cups.remove(toRemove);
        rebuildTower();
    }

    /**
     * Elimina la tapa superior de la taza indicada.
     */
    public void removeLid(int id) {

        Cup cup = getCupById(id);

        if (cup == null) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No existe la taza " + id);
            }
            return;
        }

        if (!cup.hasLids()) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "La taza " + id + " no tiene tapa.");
            }
            return;
        }

        cup.removeTopLid();
        rebuildTower();
    }

    /**
     * Elimina la última taza insertada.
     */
    public void popCup() {

        if (cups.isEmpty()) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "La torre está vacía.");
            }
            return;
        }

        Cup removedCup = cups.get(cups.size() - 1);

        removedCup.removeAllLids();
        removedCup.makeInvisible();

        cups.remove(cups.size() - 1);

        rebuildTower();
    }

    //Reordenamiento

    /**
     * Invierte el orden actual de la torre.
     */
    public void reverseTower() {

        if (cups.isEmpty()) return;

        ArrayList<Cup> original = new ArrayList<>(cups);
        Collections.reverse(cups);

        if (!fitsWithinHeight()) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No se puede invertir. Supera la altura máxima.");
            }
            return;
        }

        clearTowerVisual();

        for (Cup c : original) {
            pushCup(c.getSize());
            for (Lid lid : c.getLids()) {
                pushLid(c.getId());
            }
        }
    }

    /**
     * Ordena la torre de mayor a menor tamaño.
     */
    public void orderTower() {

        if (cups.isEmpty()) return;

        ArrayList<Cup> original = new ArrayList<>(cups);

        Collections.sort(original,
                (c1, c2) -> Integer.compare(c2.getSize(), c1.getSize()));

        if (!fitsWithinHeight()) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No se puede ordenar. Supera la altura máxima.");
            }
            return;
        }

        clearTowerVisual();

        for (Cup c : original) {
            pushCup(c.getSize());
            for (Lid lid : c.getLids()) {
                pushLid(c.getId());
            }
        }
    }

    // Consultas

    /**
     * Retorna la altura actual en centímetros.
     */
    public int height() {

        if (cups.isEmpty()) return 0;

        int topY = BASE_Y;

        for (Cup c : cups) {
            if (c.getY() < topY) topY = c.getY();
            for (Lid lid : c.getLids()) {
                if (lid.getY() < topY) topY = lid.getY();
            }
        }

        return (BASE_Y - topY) / BLOCK_SIZE;
    }

    /**
     * Retorna la altura actual en píxeles.
     */
    public int getCurrentHeight() {

        if (cups.isEmpty()) return 0;

        int topY = BASE_Y;

        for (Cup c : cups) {
            if (c.getY() < topY) topY = c.getY();
            for (Lid lid : c.getLids()) {
                if (lid.getY() < topY) topY = lid.getY();
            }
        }

        return BASE_Y - topY;
    }

    /**
     * Retorna los identificadores de tazas con tapas.
     */
    public int[] liddedCups() {

        ArrayList<Integer> result = new ArrayList<>();

        for (Cup c : cups) {
            if (c.hasLids()) result.add(c.getId());
        }

        int[] array = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            array[i] = result.get(i);
        }

        return array;
    }

    /**
     * Retorna la lista de elementos apilados.
     */
    public String[][] stackingItems() {

        ArrayList<String[]> items = new ArrayList<>();

        for (Cup c : cups) {
            items.add(new String[]{"Cup", String.valueOf(c.getId())});
            for (Lid lid : c.getLids()) {
                items.add(new String[]{"Lid", String.valueOf(lid.getId())});
            }
        }

        String[][] result = new String[items.size()][2];
        for (int i = 0; i < items.size(); i++) {
            result[i] = items.get(i);
        }

        return result;
    }

    /**
     * Verifica si la torre está en estado válido.
     */
    public boolean ok() {

        if (height() > maxHeight) return false;

        int minY = BASE_Y - maxHeight;

        for (Cup c : cups) {
            if (c.getY() < minY) return false;
            for (Lid lid : c.getLids()) {
                if (lid.getY() < minY) return false;
            }
            if (c.getLids() == null) return false;
        }

        return true;
    }

    //Control visual

    /**
     * Hace visible la torre.
     */
    public void makeVisible() {
        isVisible = true;
        drawHeightMarks();
        for (Cup c : cups) c.makeVisible();
    }

    /**
     * Hace invisible la torre.
     */
    public void makeInvisible() {
        isVisible = false;
        for (Cup c : cups) c.makeInvisible();
    }

    /**
     * Finaliza el simulador y limpia todos los elementos.
     */
    public void exit() {

        for (Cup c : cups) {
            for (Lid lid : c.getLids()) {
                lid.makeInvisible();
            }
            c.makeInvisible();
        }

        cups.clear();

        if (heightMarks != null) {
            for (Rectangle r : heightMarks) {
                r.makeInvisible();
            }
            heightMarks.clear();
        }

        isVisible = false;
    }

    //Métodos auxiliares

    /**
     * Reconstruye la torre desde la base.
     */
    private void rebuildTower() {

        for (Cup c : cups) c.makeInvisible();

        int currentTop = BASE_Y;

        for (Cup c : cups) {
            int targetX = TOWER_X - (c.getPixelWidth() / 2);
            int targetY = currentTop - c.getRealPixelHeight();
            c.moveTo(targetX, targetY);
            currentTop = targetY;
            if (isVisible) c.makeVisible();
        }
    }

    /**
     * Verifica si la torre cabe dentro de la altura máxima.
     */
    private boolean fitsWithinHeight() {

        int currentTop = BASE_Y;

        for (Cup c : cups) {
            int projectedY = currentTop - c.getRealPixelHeight();
            currentTop = projectedY;
            for (Lid lid : c.getLids()) {
                currentTop -= BLOCK_SIZE;
            }
        }

        int totalHeight = BASE_Y - currentTop;
        return totalHeight <= maxHeight;
    }

    /**
     * Limpia visualmente la torre.
     */
    private void clearTowerVisual() {

        for (Cup c : cups) {
            for (Lid lid : c.getLids()) {
                lid.makeInvisible();
            }
            c.makeInvisible();
        }

        cups.clear();
    }

    /**
     * Dibuja las marcas de altura.
     */
    private void drawHeightMarks() {

        for (Rectangle r : heightMarks) r.makeInvisible();
        heightMarks.clear();

        int marks = maxHeight / BLOCK_SIZE;

        for (int i = 0; i <= marks; i++) {

            Rectangle mark = new Rectangle();
            mark.changeColor("black");
            mark.changeSize(3, width);

            int y = BASE_Y - (i * BLOCK_SIZE);

            mark.moveHorizontal(TOWER_X - width / 2);
            mark.moveVertical(y);

            if (isVisible) mark.makeVisible();

            heightMarks.add(mark);
        }
    }

    /**
     * Retorna la coordenada superior más alta.
     */
    private int getTopY() {

        int top = BASE_Y;

        for (Cup c : cups) {
            if (c.getY() < top) top = c.getY();
            for (Lid lid : c.getLids()) {
                if (lid.getY() < top) top = lid.getY();
            }
        }

        return top;
    }

    /**
     * Retorna una taza por identificador.
     */
    private Cup getCupById(int id) {
        for (Cup c : cups) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    /**
     * Retorna un color asociado al tamaño.
     */
    private String getColorForSize(int s) {
        String[] colors = {"red", "blue", "green", "yellow", "magenta", "black"};
        return colors[s % colors.length];
    }
}
