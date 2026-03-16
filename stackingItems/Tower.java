import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una torre de tazas y tapas.
 * Controla apilamiento, anidamiento, altura máxima,
 * ordenamiento y representación visual.
 * @author Juan Gaitán and Oscar Lasso
 */
public class Tower {

    //Constantes de posicionamiento

    private static final int TOWER_X = 500;
    private static final int BASE_Y = 700;
    private static final int BLOCK_SIZE = 25;

    //Estructuras principales

    private List<Cup> cups;
    private List<Lid> standaloneLids;
    private List<Rectangle> heightMarks;
    private List<Lid> lidInsertionOrder;
    
    
    //Estado de la torre
       

    private boolean isVisible;
    private int width;
    private int maxHeight;
    
    private static final int DefaultWidth = 300;
    private static final int DefaultMaxHeight = 1000;


    /**
     * Construye una torre con ancho visual y altura máxima.
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        cups = new ArrayList<>();
        heightMarks = new ArrayList<>();
        standaloneLids = new ArrayList<>();
        lidInsertionOrder = new ArrayList<>();
        drawHeightMarks();
        isVisible = false;
    }
    
    /**
     * Construye una torre con el número de tazas dado.
     */
    public Tower(int cups) {
        this(DefaultWidth, DefaultMaxHeight);

        if (cups <= 0) {
            return;
        }
        int vc = 1;
        for (int id = cups; vc <= id; vc++) {
            pushCup(vc);
        }
    }

    
    /**
     * Agrega una tapa a la taza indicada.
     * Valida altura máxima antes de insertarla.
     */
    public void pushLid(int id) {
        
        Cup cup = getCupById(id);
        
        
        if (lidExistsInTower(id)) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "La tapa " + id + " ya existe en la torre.");
            }
            return;
        }
        
        
        int lidSize = sizeFromId(id);
        Lid lid = new Lid(id, lidSize, getColorForSize(id));
        Cup targetCup = resolveTargetCupForLid(cup, lid);
        
        boolean addsTowerHeight = targetCup == null || lid.getSize() >= targetCup.getSize();
        int projectedHeight = getCurrentHeight() + (addsTowerHeight ? BLOCK_SIZE : 0);

        if (projectedHeight > maxHeight) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No cabe la tapa, supera la altura máxima.");
            }
            return;
        }

        
        if (targetCup != null) {

            targetCup.addLid(lid);
            lidInsertionOrder.add(lid);
            int nestedLids = countNestedLids(targetCup);
            int coverLids = countCoverLids(targetCup);
            int lidX = targetCup.getX() + ((targetCup.getSize() - lid.getSize()) * BLOCK_SIZE) / 2;
            int lidY;
            if (lid.getSize() < targetCup.getSize()) {
                int innerFloorY = targetCup.getY() + targetCup.getRealPixelHeight() - (2 * BLOCK_SIZE);
                lidY = innerFloorY - ((nestedLids - 1) * BLOCK_SIZE);
                
            } else {
                lidY = targetCup.getY() - BLOCK_SIZE - ((coverLids - 1) * BLOCK_SIZE);
            }
            lid.moveTo(lidX, lidY);
            if (isVisible) lid.makeVisible();
            return;
        }
        
        int lidX = TOWER_X - ((lid.getSize() * BLOCK_SIZE) / 2);
        int lidY = getTopY() - BLOCK_SIZE;
        lid.moveTo(lidX, lidY);
        
        standaloneLids.add(lid);
        lidInsertionOrder.add(lid);
        
        if (isVisible) lid.makeVisible();
    }
    

    /**
     * Agrega una taza respetando reglas de anidamiento y altura máxima.
     */
    public void pushCup(int id) {
        if (id <= 0) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "El identificador de la taza debe ser mayor que 0.");
            return;
        }

        int size = sizeFromId(id);
  
        for (Cup c : cups) {
            if (c.getId() == id) {
                if (isVisible) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "La taza " + id + " ya existe.");
                }
                return;
            }
        }

        String color = getColorForSize(id);
        Cup newCup = new Cup(id, size, color);

        int widthPx = newCup.getPixelWidth();
        int targetX = TOWER_X - (widthPx / 2);
        int targetY;

        Cup topCup = cups.isEmpty() ? null : cups.get(cups.size() - 1);
        
        int topY = getTopY();
        

        if (topCup == null) {
             if (topY < BASE_Y) {
                targetY = topY - newCup.getRealPixelHeight();
            } else {
                targetY = BASE_Y - newCup.getRealPixelHeight();
            }
            
        } else {

            boolean topHasLid = topCup.hasLids();
            boolean fitsInsideTop = newCup.getSize() < topCup.getSize();
            boolean canNestWithCurrentTop = !topHasLid || canNestAboveInnerLid(topCup);

            if (hasStandaloneLidAtTop(topY)) {
                targetY = topY - newCup.getRealPixelHeight();
            } else if (fitsInsideTop && canNestWithCurrentTop) {
                targetY = getNestedTargetY(topCup, newCup);
            
            } else {
                 Cup ancestorContainer = findAncestorContainerFor(topCup, newCup.getSize(), cups.size() - 1);
                if (ancestorContainer != null) {
                    Cup supportCup = findBestSupportInsideAncestor(ancestorContainer, newCup.getSize(), cups.size() - 1);
                    if (supportCup != null) {
                        targetY = getStackedAboveCupY(supportCup, newCup);
                    } else {
                        targetY = getNestedTargetY(ancestorContainer, newCup);
                    }
                } else {
                       targetY = topY - newCup.getRealPixelHeight();
                }
                
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
                lidInsertionOrder.remove(lid);
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

    if (cup != null) {
            if (!cup.hasLids()) {
                if (isVisible) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                    "La taza " + id + " no tiene tapa.");
            }
            return;
        }

        cup.removeTopLid();
        removeLastInsertedReferenceById(id);
        rebuildTower();     
        return;
    }
    Lid removed = removeStandaloneLidById(id);
    if (removed == null) {
        if (isVisible) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "No existe la tapa " + id);
        }
        return;
    }


    removed.makeInvisible();
    lidInsertionOrder.remove(removed);
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
        
        removeLidReferencesByCup(removedCup);
        removedCup.removeAllLids();
        removedCup.makeInvisible();

        cups.remove(cups.size() - 1);

        rebuildTower();
    }
    
    /**
     * Elimina la última tapa insertada en la torre.
     */
    public void popLid() {
        if (lidInsertionOrder.isEmpty()) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "La torre no tiene tapas.");
            }
            return;
        }

        Lid lastInsertedLid = lidInsertionOrder.remove(lidInsertionOrder.size() - 1);

        if (!standaloneLids.remove(lastInsertedLid)) {
            for (Cup cup : cups) {
                if (cup.getLids().remove(lastInsertedLid)) {
                    break;
                }
            }
        }

        lastInsertedLid.makeInvisible();
        rebuildTower();
    }
    

    //Reordenamiento

    /**
     * Invierte el orden actual de la torre.
     */
    public void reverseTower() {

         if (cups.isEmpty() && standaloneLids.isEmpty()) return;

        ArrayList<Cup> original = new ArrayList<>(cups);
        ArrayList<Lid> originalStandalone = new ArrayList<>(standaloneLids);
        
        Collections.reverse(cups);
        
        for (Cup cup : cups) {
            Collections.reverse(cup.getLids());
        }
        Collections.reverse(standaloneLids);
        
        if (!fitsWithinHeight()) {
            Collections.reverse(cups);
            for (Cup cup : cups) {
                Collections.reverse(cup.getLids());
            }
            Collections.reverse(standaloneLids);
            
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No se puede invertir. Supera la altura máxima.");
            }
            return;
        }

        rebuildTower();
    }

    /**
     * Intercambio dos objetos de la torre y deben ser del mismo tipo. Sin cambiar la lógica visual o interna. 
     *
     */
    public void swap (String[] o1, String[] o2){
         if (!isValidObjectRef(o1) || !isValidObjectRef(o2)) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Cada objeto debe tener formato {\"tipo\", \"id\"}.");
            }
            return;
        }

        String type1 = o1[0].trim().toLowerCase();
        String type2 = o2[0].trim().toLowerCase();

        int id1;
        int id2;

        try {
            id1 = Integer.parseInt(o1[1].trim());
            id2 = Integer.parseInt(o2[1].trim());
        } catch (NumberFormatException e) {
            if (isVisible) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Los identificadores deben ser numéricos.");
            }
            return;
        }

        if (type1.equals("cup") && type2.equals("cup")) {
            int cupIndex1 = findCupIndexById(id1);
            int cupIndex2 = findCupIndexById(id2);

            if (cupIndex1 == -1 || cupIndex2 == -1) {
                if (isVisible) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "No se encontraron ambas tazas para intercambiar.");
                }
                return;
            }

            Collections.swap(cups, cupIndex1, cupIndex2);
            rebuildTower();
            return;
        }

        if (type1.equals("lid") && type2.equals("lid")) {
            int[] lidRef1 = findLidRefById(id1);
            int[] lidRef2 = findLidRefById(id2);

            if (lidRef1 == null || lidRef2 == null) {
                if (isVisible) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "No se encontraron ambas tapas para intercambiar.");
                }
                return;
            }

            Cup cup1 = cups.get(lidRef1[0]);
            Cup cup2 = cups.get(lidRef2[0]);

            Lid lid1 = cup1.getLids().get(lidRef1[1]);
            Lid lid2 = cup2.getLids().get(lidRef2[1]);

            cup1.getLids().set(lidRef1[1], lid2);
            cup2.getLids().set(lidRef2[1], lid1);

            rebuildTower();
            return;
        }

        if (isVisible) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Solo se pueden intercambiar objetos del mismo tipo (cup-cup o lid-lid).");
        }    
    
    }
    
    /**
     * Ordena la torre de mayor a menor tamaño.
     */
    public void orderTower() {

        if (cups.isEmpty()) return;

        ArrayList<Cup> original = new ArrayList<>(cups);
        ArrayList<Lid> originalStandalone = new ArrayList<>(standaloneLids);
        

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
            pushCup(c.getId());
            for (Lid lid : c.getLids()) {
                pushLid(c.getId());
            }
        }
        
        for (Lid lid : originalStandalone) {
            pushLid(lid.getId());
        }
        
    }

    /**
     * Cubre las tazas que no tienen tapa usando las tapas ya existentes en la torre.
     */
    public void cover() {
        boolean movedAnyLid = false;

        for (Cup cup : cups) {
            if (cup.hasLids()) {
                continue;
            }

            Lid matchingLid = detachLidById(cup.getId());
            if (matchingLid == null) {
                continue;
            }

            cup.addLid(matchingLid);
            movedAnyLid = true;
        }

        if (movedAnyLid) {
            rebuildTower();
        }
    }
    
    /**
     * Retorna un intercambio que reduzca la altura de la torre.
     *
     * El resultado tiene el formato:
     * {{"cup|lid", "id"}, {"cup|lid", "id"}}
     *
     * Si no existe un intercambio que reduzca la altura al menos en 1,
     * retorna una matriz vacía.
     */
    public String[][] swapToReduce() {
        int currentHeight = height();
        int bestHeight = currentHeight;
        String[][] bestSwap = new String[0][0];

        for (int i = 0; i < cups.size(); i++) {
            for (int j = i + 1; j < cups.size(); j++) {
                Collections.swap(cups, i, j);
                rebuildTower();

                int candidateHeight = height();
                if (candidateHeight < bestHeight) {
                    bestHeight = candidateHeight;
                    bestSwap = new String[][]{
                        {"cup", String.valueOf(cups.get(i).getId())},
                        {"cup", String.valueOf(cups.get(j).getId())}
                    };
                }

                Collections.swap(cups, i, j);
                rebuildTower();
            }
        }

        ArrayList<LidLocation> lidLocations = getAllLidLocations();
        for (int i = 0; i < lidLocations.size(); i++) {
            for (int j = i + 1; j < lidLocations.size(); j++) {
                LidLocation first = lidLocations.get(i);
                LidLocation second = lidLocations.get(j);

                Lid firstLid = first.getLid(this);
                Lid secondLid = second.getLid(this);
                first.setLid(this, secondLid);
                second.setLid(this, firstLid);

                rebuildTower();

                int candidateHeight = height();
                if (candidateHeight < bestHeight) {
                    bestHeight = candidateHeight;
                    bestSwap = new String[][]{
                        {"lid", String.valueOf(firstLid.getId())},
                        {"lid", String.valueOf(secondLid.getId())}
                    };
                }

                firstLid = first.getLid(this);
                secondLid = second.getLid(this);
                first.setLid(this, secondLid);
                second.setLid(this, firstLid);
                rebuildTower();
            }
        }

        return (currentHeight - bestHeight) >= 1 ? bestSwap : new String[0][0];
    }
    
    // Consultas

    /**
     * Retorna la altura actual en centímetros.
     */
    public int height() {

        if (cups.isEmpty() && standaloneLids.isEmpty()) return 0;
        int topY = BASE_Y;

        for (Cup c : cups) {
            if (c.getY() < topY) topY = c.getY();
            for (Lid lid : c.getLids()) {
                if (lid.getY() < topY) topY = lid.getY();
            }
        }
        
        for (Lid lid : standaloneLids) {
            if (lid.getY() < topY) topY = lid.getY();
        }
        

        return (BASE_Y - topY) / BLOCK_SIZE;
    }

    /**
     * Retorna la altura actual en píxeles.
     */
    public int getCurrentHeight() {

        if (cups.isEmpty() && standaloneLids.isEmpty()) return 0;

        int topY = BASE_Y;

        for (Cup c : cups) {
            if (c.getY() < topY) topY = c.getY();
            for (Lid lid : c.getLids()) {
                if (lid.getY() < topY) topY = lid.getY();
            }
        }
        
         for (Lid lid : standaloneLids) {
            if (lid.getY() < topY) topY = lid.getY();
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
        
        for (Lid lid : standaloneLids) {
            items.add(new String[]{"Lid", String.valueOf(lid.getId())});
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
        
        for (Lid lid : standaloneLids) {
            if (lid.getY() < minY) return false;
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
        for (Cup c : cups) {
            c.makeVisible();
        }
        for (Lid lid : standaloneLids) {
            lid.makeVisible();
        }

        for (Lid lid : getAllLidsInTower()) {
            lid.makeVisible();
        }
        
    }

    /**
     * Hace invisible la torre.
     */
    public void makeInvisible() {
        isVisible = false;
        for (Cup c : cups) {
            c.makeInvisible();
        }
        for (Lid lid : standaloneLids) {
            lid.makeInvisible();
        }
        for (Lid lid : getAllLidsInTower()) {
            lid.makeInvisible();
        }

        for (Rectangle mark : heightMarks) {
            mark.makeInvisible();
        }
        
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
        
        for (Lid lid : standaloneLids) {
            lid.makeInvisible();
        }
        
        cups.clear();
        standaloneLids.clear();
        lidInsertionOrder.clear();
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
        for (Lid lid : standaloneLids) lid.makeInvisible();
        

        int currentTopY = BASE_Y;
        Cup topCup = null;

        for (int cupIndex = 0; cupIndex < cups.size(); cupIndex++) {
            Cup c = cups.get(cupIndex);
            int targetX = TOWER_X - (c.getPixelWidth() / 2);
            int targetY;

            if (topCup == null) {
                targetY = BASE_Y - c.getRealPixelHeight();
            } else {
                boolean topHasLid = topCup.hasLids();
                boolean fitsInsideTop = c.getSize() < topCup.getSize();
                boolean canNestWithCurrentTop = !topHasLid || canNestAboveInnerLid(topCup);
                
                
                        
                if (hasStandaloneLidAtTop(currentTopY)) {
                    targetY = currentTopY - c.getRealPixelHeight();
                 } else if (fitsInsideTop && canNestWithCurrentTop) {
                    targetY = getNestedTargetY(topCup, c);
                } else {
                    Cup ancestorContainer = findAncestorContainerFor(topCup, c.getSize(), cupIndex - 1);
                    if (ancestorContainer != null) {
                        Cup supportCup = findBestSupportInsideAncestor(ancestorContainer, c.getSize(), cupIndex - 1);
                        if (supportCup != null) {
                            targetY = getStackedAboveCupY(supportCup, c);
                        } else {
                            targetY = getNestedTargetY(ancestorContainer, c);
                        }
                    } else {
                        targetY = targetY = currentTopY - c.getRealPixelHeight();
                    }
                }
            }
            
            c.moveTo(targetX, targetY);
            
            if (isVisible) c.makeVisible();
            
            if (c.getY() < currentTopY) {
                currentTopY = c.getY();
            }
            for (Lid lid : c.getLids()) {
                if (lid.getY() < currentTopY) {
                    currentTopY = lid.getY();
                }
            }

            topCup = c;
        }
        for (Lid lid : standaloneLids) {
            int lidX = TOWER_X - ((lid.getSize() * BLOCK_SIZE) / 2);
            int lidY = currentTopY - BLOCK_SIZE;
            lid.moveTo(lidX, lidY);
            if (isVisible) lid.makeVisible();
            currentTopY = lidY;
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
        currentTop -= standaloneLids.size() * BLOCK_SIZE;
        
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
        
        for (Lid lid : standaloneLids) {
            lid.makeInvisible();
        }
        
        cups.clear();
        standaloneLids.clear();
        lidInsertionOrder.clear();
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
        
        for (Lid lid : standaloneLids) {
            if (lid.getY() < top) top = lid.getY();
        }
        
        return top;
    }
    
    private Lid removeStandaloneLidById(int id) {
        for (int i = 0; i < standaloneLids.size(); i++) {
            Lid lid = standaloneLids.get(i);
            if (lid.getId() == id) {
                standaloneLids.remove(i);
                return lid;
            }
        }
        return null;
    }
    
     private Lid detachLidById(int id) {
        Lid detached = removeStandaloneLidById(id);
        if (detached != null) {
            return detached;
        }

        for (Cup sourceCup : cups) {
            ArrayList<Lid> lids = sourceCup.getLids();
            for (int lidIndex = 0; lidIndex < lids.size(); lidIndex++) {
                Lid lid = lids.get(lidIndex);
                if (lid.getId() == id) {
                    lids.remove(lidIndex);
                    return lid;
                }
            }
        }

        return null;
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
    
    private void removeLidReferencesByCup(Cup cup) {
        for (Lid lid : cup.getLids()) {
            lidInsertionOrder.remove(lid);
        }
    }

    private void removeLastInsertedReferenceById(int id) {
        for (int i = lidInsertionOrder.size() - 1; i >= 0; i--) {
            if (lidInsertionOrder.get(i).getId() == id) {
                lidInsertionOrder.remove(i);
                return;
            }
        }
    }
    

     private int findCupIndexById(int id) {
        for (int i = 0; i < cups.size(); i++) {
            if (cups.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private int[] findLidRefById(int id) {
        for (int cupIndex = 0; cupIndex < cups.size(); cupIndex++) {
            ArrayList<Lid> cupLids = cups.get(cupIndex).getLids();
            for (int lidIndex = 0; lidIndex < cupLids.size(); lidIndex++) {
                if (cupLids.get(lidIndex).getId() == id) {
                    return new int[]{cupIndex, lidIndex};
                }
            }
        }
        return null;
    }

    private boolean isValidObjectRef(String[] objectRef) {
        return objectRef != null
                && objectRef.length == 2
                && objectRef[0] != null
                && objectRef[1] != null;
    }

    
    private int sizeFromId(int id) {
        return (2 * id) - 1;
    }
    
    /**
     * Retorna un color asociado al tamaño.
     */
    private String getColorForSize(int s) {
        String[] colors = {"red", "blue", "green", "yellow", "magenta", "black"};
        return colors[s % colors.length];
    }
    
    private boolean lidExistsInTower(int id) {
        for (Cup c : cups) {
            for (Lid lid : c.getLids()) {
                if (lid.getId() == id) {
                    return true;
                }
            }
        }

        for (Lid lid : standaloneLids) {
            if (lid.getId() == id) {
                return true;
            }
        }

        return false;
    }
    
    private ArrayList<LidLocation> getAllLidLocations() {
        ArrayList<LidLocation> locations = new ArrayList<>();

        for (int cupIndex = 0; cupIndex < cups.size(); cupIndex++) {
            ArrayList<Lid> cupLids = cups.get(cupIndex).getLids();
            for (int lidIndex = 0; lidIndex < cupLids.size(); lidIndex++) {
                locations.add(new LidLocation(cupIndex, lidIndex, -1));
            }
        }

        for (int standaloneIndex = 0; standaloneIndex < standaloneLids.size(); standaloneIndex++) {
            locations.add(new LidLocation(-1, -1, standaloneIndex));
        }

        return locations;
    }

    private static class LidLocation {
        private final int cupIndex;
        private final int lidIndex;
        private final int standaloneIndex;

        LidLocation(int cupIndex, int lidIndex, int standaloneIndex) {
            this.cupIndex = cupIndex;
            this.lidIndex = lidIndex;
            this.standaloneIndex = standaloneIndex;
        }

        Lid getLid(Tower tower) {
            if (standaloneIndex >= 0) {
                return tower.standaloneLids.get(standaloneIndex);
            }
            return tower.cups.get(cupIndex).getLids().get(lidIndex);
        }

        void setLid(Tower tower, Lid lid) {
            if (standaloneIndex >= 0) {
                tower.standaloneLids.set(standaloneIndex, lid);
            } else {
                tower.cups.get(cupIndex).getLids().set(lidIndex, lid);
            }
        }
    }
    
    
     private int getNestedTargetY(Cup containerCup, Cup nestedCup) {
        int supportY = containerCup.getY() + containerCup.getRealPixelHeight() - BLOCK_SIZE;

        if (canNestAboveInnerLid(containerCup)) {
            Lid innerLid = containerCup.getLids().get(containerCup.getLids().size() - 1);
            supportY = innerLid.getY();
        }

        return supportY - nestedCup.getRealPixelHeight();
    }

    private boolean canNestAboveInnerLid(Cup cup) {
        if (!cup.hasLids()) {
            return false;
        }

        Lid topLid = cup.getLids().get(cup.getLids().size() - 1);
        return topLid.getSize() < cup.getSize();
    }
    
    private Cup findAncestorContainerFor(Cup topCup, int candidateCupSize, int maxIndex) {
        if (topCup == null || maxIndex <= 0) {
            return null;
        }

        for (int i = maxIndex - 1; i >= 0; i--) {
            Cup container = cups.get(i);
            boolean topIsInsideContainer = topCup.getY() > container.getY();
            boolean candidateFitsContainer = candidateCupSize < container.getSize();
            boolean canNestOnContainerFloor = !container.hasLids() || canNestAboveInnerLid(container);

            if (topIsInsideContainer && candidateFitsContainer && canNestOnContainerFloor) {
                return container;
            }
        }

        return null;
    }
    
     private boolean hasStandaloneLidAtTop(int topY) {
        for (Lid lid : standaloneLids) {
            if (lid.getY() == topY) {
                return true;
            }
        }
        return false;
    }
    
    private int countNestedLids(Cup cup) {
        int nested = 0;
        for (Lid lid : cup.getLids()) {
            if (lid.getSize() < cup.getSize()) {
                nested++;
            }
        }
        return nested;
    }

    private int countCoverLids(Cup cup) {
        int covers = 0;
        for (Lid lid : cup.getLids()) {
            if (lid.getSize() >= cup.getSize()) {
                covers++;
            }
        }
        return covers;
    }
    
     private int getStackedAboveCupY(Cup supportCup, Cup newCup) {
        return supportCup.getY() - newCup.getRealPixelHeight();
    }

    private Cup findBestSupportInsideAncestor(Cup ancestorCup, int candidateCupSize, int maxIndex) {
        Cup bestSupport = null;

        for (int i = 0; i <= maxIndex && i < cups.size(); i++) {
            Cup candidate = cups.get(i);

            if (candidate == ancestorCup) {
                continue;
            }

            boolean isInsideAncestor = candidate.getY() > ancestorCup.getY()
                    && candidate.getSize() < ancestorCup.getSize();
            boolean fitsAboveCandidate = candidate.getSize() < candidateCupSize;

            if (!isInsideAncestor || !fitsAboveCandidate) {
                continue;
            }

            if (bestSupport == null || candidate.getSize() > bestSupport.getSize()) {
                bestSupport = candidate;
            }
        }

        return bestSupport;
    }
    
    private ArrayList<Lid> getAllLidsInTower() {
        ArrayList<Lid> lids = new ArrayList<>();

        for (Cup cup : cups) {
            for (Lid lid : cup.getLids()) {
                if (!lids.contains(lid)) {
                    lids.add(lid);
                }
            }
        }

        for (Lid lid : standaloneLids) {
            if (!lids.contains(lid)) {
                lids.add(lid);
            }
        }

        for (Lid lid : lidInsertionOrder) {
            if (!lids.contains(lid)) {
                lids.add(lid);
            }
        }

        return lids;
    }
    
    private Cup resolveTargetCupForLid(Cup requestedCup, Lid lid) {
        Cup targetCup = requestedCup;

        if (cups.isEmpty()) {
            return targetCup;
        }

        Cup topCup = cups.get(cups.size() - 1);
        boolean lidFitsInsideTopCup = lid.getSize() < topCup.getSize();
        boolean canStackInsideTopCup = !topCup.hasLids() || canNestAboveInnerLid(topCup);

        if (targetCup == null) {
            boolean hasElementAboveTopCup = getTopY() < topCup.getY();
            if (hasElementAboveTopCup) {
                return null;
            }
            if (lidFitsInsideTopCup && canStackInsideTopCup) {
                return topCup;
            }
            return null;
        }

        if (targetCup != topCup && targetCup.getY() > topCup.getY()
                && lidFitsInsideTopCup && canStackInsideTopCup) {
            return topCup;
        }

        return targetCup;
    }
    
    
    
}
