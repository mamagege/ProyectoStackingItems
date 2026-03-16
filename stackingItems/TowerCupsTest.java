import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.HeadlessException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Field;






/**
 * Clase de Test de pruebas de unidad para Cup en Tower.
 *
 * @author  Juan Diego Gaita, Oscar Lasso
 * @version 1.0
 */
public class TowerCupsTest
{
    
    @Test 
    public void ShouldCreateCup(){
        // Arrange
        Tower tower = new Tower(800,800);
        
        // Act
        tower.pushCup(1);
        
        // Assert
        String[][] stackingItems = tower.stackingItems() ;
        assertEquals(1,stackingItems.length);
        assertArrayEquals(new String[]{"Cup","1"},stackingItems[0]);
        assertEquals(1,tower.height());
        
    }
    
    @Test
    
    public void ShouldNestSmallCup() {
         // Arrange
        Tower tower = new Tower(800,800);
        tower.pushCup(3);
        int normalHeight = tower.height();

        // Act
        tower.pushCup(2);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(2, items.length);
        assertEquals(normalHeight, tower.height(), "Al anidar una taza más pequeña, la altura de la torre no debe aumentar.");
        
    }
    
    @Test
    void ShouldStackBiggerCup() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(2);
        int baseHeight = tower.height();

        // Act
        tower.pushCup(3);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(2, items.length);
        assertTrue(tower.height() > baseHeight, "Una taza más grande encima debe incrementar altura por apilamiento.");
    }
    
    @Test
    public void ShouldntCreateCupWhenIdIsZeroOrNegative() {
        // Arrange
        Tower tower = new Tower(300, 1000);

        // Act
        tower.pushCup(0);
        tower.pushCup(-1);

        // Assert
        assertEquals(0, tower.stackingItems().length);
        assertEquals(0, tower.height());
    }
    
    @Test
    public void ShouldntCreateCupWhenRepeatedId() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(2);

        // Act
        tower.pushCup(2);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(1, items.length);
        assertEquals("2", items[0][1]);
    }
    
    @Test
    public void ShouldntCreateCupInMaxHeight() {
        // Arrange
        Tower tower = new Tower(300, 300);
        tower.pushCup(2);

        // Act
        tower.pushCup(3);
        

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(1, items.length);
        assertTrue(tower.ok());
    }
    
    @Test
    public void ShouldStackCupInLid() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(3);
        tower.pushLid(4); 
        int heightBefore = tower.height();

        // Act
        tower.pushCup(2);

        // Assert
        assertEquals(2, countByType(tower.stackingItems(), "Cup"));
        assertEquals(heightBefore + 5, tower.height(), "Con tapa independiente, la taza debe apilarse y sumar su altura completa.");
    }
    
     @Test
    public void ShouldKeepTowerConsistent() {
        // Arrange
        Tower tower = new Tower(300, 1000);

        // Act
        tower.pushCup(4);
        tower.pushCup(2);
        tower.pushCup(5);
        int heightAfterThirdCup = tower.height();
        tower.pushCup(3);
        tower.pushCup(1);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(5, countByType(items, "Cup"));
        assertTrue(tower.ok());
        assertEquals(heightAfterThirdCup, tower.height(), "La cuarta  y quinta taza (más pequeña) debe anidarse dentro de la superior y no aumentar altura.");
        assertEquals(5, distinctCupIds(items).size(), "No debe haber ids de taza duplicados.");
    }
    
    @Test 
    
    public void ShouldKeepTowerConsistentSandwich() {
        // Arrange
        Tower tower = new Tower(300, 1000);

        // Act
        tower.pushCup(1);
        tower.pushCup(4);
        tower.pushCup(2);
        int heightAfterThirdCup = tower.height();
        tower.pushCup(3);
                
        // Assert
        
        
        String[][] items = tower.stackingItems();
        assertEquals(4, countByType(items, "Cup"));
        assertTrue(tower.ok());
        assertEquals(heightAfterThirdCup + 2, tower.height() );
    
        
        
    }
    
     @Test
    public void ShouldRemoveCupByIdAndKeepTowerConsistent() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(1);
        tower.pushCup(3);
        tower.pushCup(2);
        int previousHeight = tower.height();

        // Act
        tower.removeCup(3);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(2, countByType(items, "Cup"));
        assertFalse(containsCupId(items, "3"), "La taza con id 3 debe eliminarse de la torre.");
        assertTrue(tower.height() <= previousHeight, "Al eliminar una taza, la altura no debería aumentar.");
        assertTrue(tower.ok());
    }
    
    @Test
    public void ShouldntRemoveCupWhenIdDoesNotExist() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(1);
        tower.pushCup(2);
        String[][] itemsBefore = tower.stackingItems();
        int heightBefore = tower.height();

        // Act
        tower.removeCup(99);

        // Assert
        assertArrayEquals(itemsBefore, tower.stackingItems());
        assertEquals(heightBefore, tower.height());
        assertTrue(tower.ok());
    }
    
    @Test
    public void ShouldPopLastInsertedCup() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(2);
        tower.pushCup(4);

        // Act
        tower.popCup();

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(1, countByType(items, "Cup"));
        assertArrayEquals(new String[]{"Cup", "2"}, items[0]);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntPopCupWhenTowerIsEmpty() {
        // Arrange
        Tower tower = new Tower(300, 1000);

        // Act
        tower.popCup();

        // Assert
        assertEquals(0, tower.stackingItems().length);
        assertEquals(0, tower.height());
        assertTrue(tower.ok());
    }
    
     @Test
    public void ShouldNestCupWhenTopIsInnerLid() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushLid(2);
        tower.pushCup(1);
        tower.pushLid(1);
        tower.pushCup(4);
        tower.pushLid(3);
        int heightBefore = tower.height();

        // Act
        tower.pushCup(2);

        // Assert
        assertEquals(heightBefore, tower.height(),
                "La taza 2 debe anidarse sobre la tapa interna superior y no apilarse por una tapa independiente inferior.");
        assertTrue(tower.ok());
    }
    
     @Test
    public void ShouldNestInAncestorContainerWhenTopCupCannotContain() {
        // Arrange
        Tower tower = new Tower(300, 2000);

        // Act
        tower.pushCup(5);
        tower.pushCup(1);
        tower.pushCup(3);

        Cup cup1 = getCupById(tower, 1);
        Cup cup3 = getCupById(tower, 3);

        tower.pushCup(2);
        tower.pushCup(4);

        Cup cup4 = getCupById(tower, 4);

        // Assert
        assertNotNull(cup1);
        assertNotNull(cup3);
        assertNotNull(cup4);
        assertTrue(cup3.getY() < cup1.getY(),
                "La taza 3 debe quedar encima de la taza 1 para no taparla visualmente.");
        assertTrue(cup4.getY() < cup3.getY(),
                "La taza 4 debe quedar encima de la taza 3 dentro de la taza 5.");
        assertTrue(tower.ok());
    }
    
    //Métodos privados auxiliares
    
    private Cup getCupById(Tower tower, int id) {
        try {
            Field cupsField = Tower.class.getDeclaredField("cups");
            cupsField.setAccessible(true);
            java.util.List<Cup> cups = (java.util.List<Cup>) cupsField.get(tower);

            for (Cup cup : cups) {
                if (cup.getId() == id) {
                    return cup;
                }
            }
            return null;
        } catch (ReflectiveOperationException e) {
            fail("No fue posible inspeccionar las tazas de la torre en la prueba.");
            return null;
        }
    }
    
    
     private boolean containsCupId(String[][] items, String id) {
        return Arrays.stream(items)
                .anyMatch(item -> "Cup".equals(item[0]) && id.equals(item[1]));
    }
    

    private long countByType(String[][] items, String type) {
        return Arrays.stream(items).filter(item -> type.equals(item[0])).count();
    }

    private Set<String> distinctCupIds(String[][] items) {
        Set<String> ids = new HashSet<>();
        Arrays.stream(items)
                .filter(item -> "Cup".equals(item[0]))
                .forEach(item -> ids.add(item[1]));
        return ids;
    }
    

    
}