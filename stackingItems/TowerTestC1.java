import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas de unidad C1 para métodos de consulta, orden y estado en Tower.
 *
 * Patrón: AAA + nombres should/shouldnt.
 */
public class TowerTestC1 {

    @Test
    public void ShouldOrderTowerFromBiggerToSmallerCupIds() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(2);
        tower.pushCup(5);
        tower.pushCup(3);

        // Act
        tower.orderTower();

        // Assert
        String[][] items = tower.stackingItems();
        assertArrayEquals(new String[]{"Cup", "5"}, items[0]);
        assertArrayEquals(new String[]{"Cup", "3"}, items[1]);
        assertArrayEquals(new String[]{"Cup", "2"}, items[2]);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldOrderTowerKeepingLidsWithTheirCup() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(2);
        tower.pushLid(2);
        tower.pushCup(4);
        tower.pushLid(4);

        // Act
        tower.orderTower();

        // Assert
        String[][] items = tower.stackingItems();
        assertArrayEquals(new String[]{"Cup", "4"}, items[0]);
        assertArrayEquals(new String[]{"Lid", "4"}, items[1]);
        assertArrayEquals(new String[]{"Cup", "2"}, items[2]);
        assertArrayEquals(new String[]{"Lid", "2"}, items[3]);
        assertArrayEquals(new int[]{4, 2}, tower.liddedCups());
    }

    @Test
    public void ShouldntChangeEmptyTowerWhenOrderTower() {
        // Arrange
        Tower tower = new Tower(300, 1000);

        // Act
        tower.orderTower();

        // Assert
        assertEquals(0, tower.height());
        assertEquals(0, tower.stackingItems().length);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldReverseTowerOrderForCupsAndStandaloneLids() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(1);
        tower.pushCup(3);
        tower.pushLid(7);
        tower.pushLid(8);

        // Act
        tower.reverseTower();

        // Assert
        String[][] items = tower.stackingItems();
        assertArrayEquals(new String[]{"Cup", "3"}, items[0]);
        assertArrayEquals(new String[]{"Cup", "1"}, items[1]);
        assertArrayEquals(new String[]{"Lid", "8"}, items[2]);
        assertArrayEquals(new String[]{"Lid", "7"}, items[3]);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntBreakTowerWhenReverseEmptyTower() {
        // Arrange
        Tower tower = new Tower(300, 1000);

        // Act
        tower.reverseTower();

        // Assert
        assertEquals(0, tower.stackingItems().length);
        assertEquals(0, tower.height());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldReturnExpectedHeightWithNestedAndStackedElements() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(3);   // altura base = 9
        tower.pushLid(3);   // +1
        tower.pushCup(2);   // + 5
        tower.pushLid(1);   // tapa interna, no suma
        tower.pushLid(7);   // tapa externa independiente, +1

        // Act
        int currentHeight = tower.height();

        // Assert
        assertEquals(16, currentHeight);
    }

    @Test
    public void ShouldReturnLiddedCupsOnlyForCupsWithLids() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(1);
        tower.pushCup(4);
        tower.pushCup(2);
        tower.pushLid(4);
        tower.pushLid(2);

        // Act
        int[] lidded = tower.liddedCups();

        // Assert
        assertArrayEquals(new int[]{4, 2}, lidded);
    }

    @Test
    public void ShouldReturnStackingItemsInInsertionStructureOrder() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        tower.pushLid(2);
        tower.pushCup(1);
        tower.pushLid(6);

        // Act
        String[][] items = tower.stackingItems();

        // Assert
        assertEquals(4, items.length);
        assertArrayEquals(new String[]{"Cup", "4"}, items[0]);
        assertArrayEquals(new String[]{"Lid", "2"}, items[1]);
        assertArrayEquals(new String[]{"Cup", "1"}, items[2]);
        assertArrayEquals(new String[]{"Lid", "6"}, items[3]);
    }

    @Test
    public void ShouldExitAndClearAllTowerData() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(5);
        tower.pushLid(5);
        tower.pushLid(8);

        // Act
        tower.exit();

        // Assert
        assertEquals(0, tower.height());
        assertEquals(0, tower.stackingItems().length);
        assertEquals(0, tower.liddedCups().length);
        assertTrue(tower.ok());
    }
    

}