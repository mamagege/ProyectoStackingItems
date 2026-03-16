import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.HeadlessException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Field;
import java.util.ArrayList;



/**
 * Clase de pruebas de unidad para tapas en Tower.
 *
 * @author Juan Diego Gaita, Oscar Lasso
 * @version 1.0
 */
public class TowerLidsTest
{
    @Test
    public void ShouldCreateLid() {
        // Arrange
        Tower tower = new Tower(300, 1000);

        // Act
        tower.pushLid(2);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(1, items.length);
        assertArrayEquals(new String[]{"Lid", "2"}, items[0]);
        assertEquals(1, tower.height());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldPlaceLidOnMatchingCup() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(3);

        // Act
        tower.pushLid(3);

        // Assert
        String[][] items = tower.stackingItems();
        assertTrue(containsItem(items, "Cup", "3"));
        assertTrue(containsItem(items, "Lid", "3"));
        assertEquals(2, items.length);
        assertArrayEquals(new int[]{3}, tower.liddedCups());
        assertEquals(10, tower.height());
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
    public void ShouldPlaceLidInsideCup() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        int previousHeight = tower.height();

        // Act
        tower.pushLid(2);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(2, items.length);
        assertTrue(containsItem(items, "Lid", "2"));
        assertEquals(previousHeight, tower.height(), "Una tapa menor dentro de la taza superior no debe incrementar altura.");
        assertTrue(tower.ok());
    }

    //Resolver
    @Test
    public void ShouldCoverCupWithLidInside() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        tower.pushLid(2);
        tower.pushLid(3);

        // Act
        tower.pushLid(4);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(4, items.length);
        assertTrue(containsItem(items, "Cup", "4"));
        assertTrue(containsItem(items, "Lid", "4"));
        assertTrue(containsItem(items, "Lid", "2"));
        assertTrue(containsItem(items, "Lid", "3"));
        assertEquals(14, tower.height());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldRemoveLidFromCupById() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(3);
        tower.pushLid(3);

        // Act
        tower.removeLid(3);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(1, items.length);
        assertArrayEquals(new String[]{"Cup", "3"}, items[0]);
        assertEquals(9, tower.height());
        assertEquals(0, tower.liddedCups().length);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldRemoveTopLid() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(3);
        tower.pushLid(5);
        int previousHeight = tower.height();

        // Act
        tower.removeLid(5);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(1, items.length);
        assertArrayEquals(new String[]{"Cup", "3"}, items[0]);
        assertEquals(previousHeight - 1, tower.height());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldPopLastLid() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        tower.pushLid(2);
        tower.pushLid(6);

        // Act
        tower.popLid();

        // Assert
        String[][] itemsAfterFirstPop = tower.stackingItems();
        assertTrue(containsItem(itemsAfterFirstPop, "Lid", "2"));
        assertFalse(containsItem(itemsAfterFirstPop, "Lid", "6"));

        // Act
        tower.popLid();

        // Assert
        String[][] itemsAfterSecondPop = tower.stackingItems();
        assertFalse(containsItem(itemsAfterSecondPop, "Lid", "2"));
        assertEquals(1, itemsAfterSecondPop.length);
        assertArrayEquals(new String[]{"Cup", "4"}, itemsAfterSecondPop[0]);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntPopLidWhenEmptyTower() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(2);
        String[][] beforeItems = tower.stackingItems();
        int beforeHeight = tower.height();

        // Act
        tower.popLid();

        // Assert
        assertArrayEquals(beforeItems, tower.stackingItems());
        assertEquals(beforeHeight, tower.height());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntRemoveLidWhenIdDoesNotExist() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(3);
        tower.pushLid(3);
        String[][] beforeItems = tower.stackingItems();
        int beforeHeight = tower.height();

        // Act
        tower.removeLid(99);

        // Assert
        assertArrayEquals(beforeItems, tower.stackingItems());
        assertEquals(beforeHeight, tower.height());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldKeepConsistentLidAndCup() {
        // Arrange
        Tower tower = new Tower(300, 1000);

        // Act
        tower.pushCup(5);
        tower.pushLid(2);
        tower.pushLid(7);
        tower.removeLid(7);
        tower.pushLid(5);
        tower.popLid();

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(2, items.length);
        assertTrue(containsItem(items, "Cup", "5"));
        assertTrue(containsItem(items, "Lid", "2"));
        assertFalse(containsItem(items, "Lid", "5"));
        assertFalse(containsItem(items, "Lid", "7"));
        assertTrue(tower.ok());
    }
    
    @Test
    public void ShouldStackSmallLidInBigLid() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(3);
        tower.pushCup(4);
        tower.pushLid(2);
        int heightBefore = tower.height();

        // Act
        tower.pushLid(1);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(4, items.length);
        assertTrue(containsItem(items, "Cup", "3"));
        assertTrue(containsItem(items, "Cup", "4"));
        assertTrue(containsItem(items, "Lid", "2"));
        assertTrue(containsItem(items, "Lid", "1"));
        assertEquals(heightBefore, tower.height(),
                "Una tapa interna más pequeña debe quedar sobre la tapa interna anterior.");
        assertTrue(tower.ok());
    }
    
    @Test
    public void ShouldCoverCupWithNestedLids() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        tower.pushLid(2);
        tower.pushLid(3);
        tower.pushLid(1);

        // Act
        tower.pushLid(4);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(5, items.length);
        assertTrue(containsItem(items, "Cup", "4"));
        assertTrue(containsItem(items, "Lid", "1"));
        assertTrue(containsItem(items, "Lid", "2"));
        assertTrue(containsItem(items, "Lid", "3"));
        assertTrue(containsItem(items, "Lid", "4"));
        assertEquals(14, tower.height(),
                "La tapa 4 debe cubrir la taza 4 sin elevarse por tapas internas.");
        assertTrue(tower.ok());
    }
    
    @Test
    public void ShouldPopTopLid() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushLid(2);
        tower.pushLid(6);

        // Act
        tower.popLid();

        // Assert
        String[][] items = tower.stackingItems();
        assertTrue(containsItem(items, "Lid", "2"));
        assertFalse(containsItem(items, "Lid", "6"));
        assertEquals(1, items.length);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldPopAloneLid() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        tower.pushLid(2);
        tower.pushLid(6);

        // Act
        tower.popLid();

        // Assert
        String[][] items = tower.stackingItems();
        assertTrue(containsItem(items, "Cup", "4"));
        assertTrue(containsItem(items, "Lid", "2"));
        assertFalse(containsItem(items, "Lid", "6"));
        assertEquals(2, items.length);
        assertTrue(tower.ok());
    }
    
     @Test
    public void ShouldStackLidInNestingCup() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushLid(1);
        tower.pushLid(2);
        tower.pushCup(3);
        tower.pushCup(4);
        int heightBefore = tower.height();

        // Act
        tower.pushLid(3);

        // Assert
        String[][] items = tower.stackingItems();
        int cup3Index = indexOf(items, "Cup", "3");
        int cup4Index = indexOf(items, "Cup", "4");
        int lid3Index = indexOf(items, "Lid", "3");

        assertTrue(cup3Index >= 0 && cup4Index >= 0 && lid3Index >= 0);
        assertTrue(cup3Index < cup4Index,
                "La taza 3 debe seguir debajo de la taza 4 en la secuencia.");
        assertTrue(lid3Index > cup4Index,
                "La tapa 3 debe quedar asociada a la taza 4 superior, no a la taza 3.");
        assertEquals(heightBefore, tower.height(),
                "La tapa 3 debe anidarse dentro de la taza 4 y no incrementar la altura de la torre.");
        assertTrue(tower.ok());
    }

    private int indexOf(String[][] items, String type, String id) {
        for (int i = 0; i < items.length; i++) {
            if (type.equals(items[i][0]) && id.equals(items[i][1])) {
                return i;
            }
        }
        return -1;
    }
    
    
    
    
    

    private boolean containsItem(String[][] items, String type, String id) {
        return Arrays.stream(items)
                .anyMatch(item -> type.equals(item[0]) && id.equals(item[1]));
    }
}