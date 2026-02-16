import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Representa la torre de tazas y tapas.
 */
public class Tower {

    private int width;
    private int maxHeight;

    private ArrayList<Cup> cups;
    private ArrayList<Lid> lids;

    private boolean isVisible;
    private boolean lastActionOK;

    /**
     * Constructor
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.cups = new ArrayList<>();
        this.lids = new ArrayList<>();
        this.isVisible = false;
        this.lastActionOK = true;
    }

    /**
     * Agrega una taza
     */
    public void pushCup(int id, int size, String color) {

        if (existsCup(id)) {
            error("Ya existe una taza con ese id.");
        return;
        }

        Cup cup = new Cup(id, size, color);

        cups.add(cup);

        int newHeight = calculateHeight();

        if (newHeight > maxHeight) {
            cups.remove(cup);
            error("No cabe en la torre.");
            return;
        }

        repositionCups();

        if (isVisible) {
            cup.makeVisible();
        }

        lastActionOK = true;
    }


    /**
     * Agrega una tapa
     */
    public void pushLid(int id, int size, String color) {

        Cup cup = findCup(id);

        if (cup == null) {
            error("No existe taza para colocar la tapa.");
            return;
            }

        if (cup.hasLid()) {
            error("La taza ya tiene tapa.");
            return;
            }

        Lid lid = new Lid(id, size, color);
        cup.placeLid(lid);
        lids.add(lid);

        if (isVisible) {
            lid.makeVisible();
            }

        lastActionOK = true;
    }

    /**
     * Retorna altura total simple (mini-ciclo 1)
     */
    public int height() {

        int total = 0;

        for (Cup c : cups) {
            total += 1;  // temporal (mini-ciclo)
        }

        return total;
    }

    /**
     * Hace visible la torre
     */
    public void makeVisible() {
        for (Cup c : cups) {
            c.makeVisible();
        }

        for (Lid l : lids) {
            l.makeVisible();
        }

        isVisible = true;
    }

    /**
     * Hace invisible la torre
     */
    public void makeInvisible() {
        for (Cup c : cups) {
            c.makeInvisible();
        }

        for (Lid l : lids) {
            l.makeInvisible();
        }

        isVisible = false;
    }

    /**
     * Retorna estado de última operación
     */
    public boolean ok() {
        return lastActionOK;
    }

    // ==========================
    // MÉTODOS PRIVADOS AUXILIARES
    // ==========================

    private boolean existsCup(int id) {
        for (Cup c : cups) {
            if (c.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private Cup findCup(int id) {
        for (Cup c : cups) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    private void error(String message) {
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message);
        }
        lastActionOK = false;
    }
    
    private int calculateHeight() {

        if (cups.isEmpty()) return 0;

        int max = 0;

        for (int i = 0; i < cups.size(); i++) {
            int candidate = cups.get(i).getHeight() + i;
            max = Math.max(max, candidate);
        }

        return max;
    }
    
    private void repositionCups() {

        for (int i = 0; i < cups.size(); i++) {

            Cup cup = cups.get(i);

            // Cada taza baja i bloques
            cup.moveVertical(i);
        }
    }
}
