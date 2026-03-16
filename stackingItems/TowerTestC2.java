import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 * Clase de pruebas de unidad C2 para métodos de construcción e intercambio en Tower.
 *
 * Alcance ciclo 2:
 * - Constructor Tower(cups)
 * - swap()
 * - cover()
 * - swapToReduce()
 *
 * Patrón aplicado: AAA + nomenclatura Should/Shouldnt.
 */
public class TowerTestC2 {

    @Test
    public void ShouldBuildTower() {
        // Arrange

        // Act
        Tower tower = new Tower(4);

        // Assert
        String[][] items = tower.stackingItems();
        assertEquals(4, items.length);
        assertArrayEquals(new String[]{"Cup", "1"}, items[0]);
        assertArrayEquals(new String[]{"Cup", "2"}, items[1]);
        assertArrayEquals(new String[]{"Cup", "3"}, items[2]);
        assertArrayEquals(new String[]{"Cup", "4"}, items[3]);
        assertTrue(tower.height() > 0);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntBuildinNegativeOrZeroConstructor() {
        // Arrange

        // Act
        Tower zeroTower = new Tower(0);
        Tower negativeTower = new Tower(-3);

        // Assert
        assertEquals(0, zeroTower.stackingItems().length);
        assertEquals(0, negativeTower.stackingItems().length);
        assertEquals(0, zeroTower.height());
        assertEquals(0, negativeTower.height());
    }

    @Test
    public void ShouldSwapTwoCups() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(2);
        tower.pushLid(2);
        tower.pushCup(5);
        tower.pushLid(5);

        // Act
        tower.swap(new String[]{"cup", "2"}, new String[]{"cup", "5"});

        // Assert
        String[][] items = tower.stackingItems();
        assertArrayEquals(new String[]{"Cup", "5"}, items[0]);
        assertArrayEquals(new String[]{"Lid", "5"}, items[1]);
        assertArrayEquals(new String[]{"Cup", "2"}, items[2]);
        assertArrayEquals(new String[]{"Lid", "2"}, items[3]);
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldSwapLids() {
        // Arrange
        Tower tower = new Tower(300, 2000);
        tower.pushCup(6);
        tower.pushLid(2);
        tower.pushLid(4);
        tower.pushCup(3);
        tower.pushLid(1);
        tower.pushLid(3);

        // Act
        tower.swap(new String[]{"lid", "4"}, new String[]{"lid", "3"});

        // Assert
        String[][] items = tower.stackingItems();
        assertTrue(containsItem(items, "Cup", "6"));
        assertTrue(containsItem(items, "Cup", "3"));
        assertTrue(containsItem(items, "Lid", "4"));
        assertTrue(containsItem(items, "Lid", "3"));
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntSwapDiferentsObjects() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        tower.pushLid(4);
        String[][] before = tower.stackingItems();

        // Act
        tower.swap(new String[]{"cup", "4"}, new String[]{"lid", "4"});

        // Assert
        assertArrayEquals(before, tower.stackingItems());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntSwapAnyErrorStructure() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(3);
        tower.pushCup(5);
        String[][] before = tower.stackingItems();

        // Act
        tower.swap(new String[]{"cup"}, new String[]{"cup", "5"});
        tower.swap(null, new String[]{"cup", "3"});
        tower.swap(new String[]{"cup", "abc"}, new String[]{"cup", "5"});

        // Assert
        assertArrayEquals(before, tower.stackingItems());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldCover() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(5);
        tower.pushCup(2);
        tower.pushLid(5); // lid 5 queda independiente al no caber en taza 2
        tower.pushLid(1); // secuencia extraña, tapa pequeña en taza superior

        // Act
        tower.cover();

        // Assert
        int[] lidded = tower.liddedCups();
        assertArrayEquals(new int[]{5}, lidded, "La taza 5 debe quedar cubierta tras mover su tapa correspondiente.");
        assertTrue(containsItem(tower.stackingItems(), "Lid", "1"), "La tapa no coincidente debe permanecer en torre.");
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntChangeTowerWhenCover() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        tower.pushLid(2);
        String[][] before = tower.stackingItems();
        int[] liddedBefore = tower.liddedCups();

        // Act
        tower.cover();

        // Assert
        assertArrayEquals(before, tower.stackingItems());
        assertArrayEquals(liddedBefore, tower.liddedCups());
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldSuggestSwap() {
        // Arrange
        Tower tower = new Tower(300, 2000);
        tower.pushCup(2);
        tower.pushCup(6);
        tower.pushCup(3);
        tower.pushCup(7);
        int beforeHeight = tower.height();

        // Act
        String[][] suggestedSwap = tower.swapToReduce();

        // Assert
        assertEquals(2, suggestedSwap.length, "Debe proponer un intercambio con dos referencias.");
        assertEquals("cup", suggestedSwap[0][0]);
        assertEquals("cup", suggestedSwap[1][0]);

        tower.swap(suggestedSwap[0], suggestedSwap[1]);

        assertTrue(tower.height() < beforeHeight,
                "Al aplicar el intercambio sugerido, la altura debe reducirse al menos en 1.");
        assertTrue(tower.ok());
    }

    @Test
    public void ShouldntSuggestSwap() {
        // Arrange
        Tower tower = new Tower(300, 1000);
        tower.pushCup(4);
        tower.pushLid(4);

        // Act
        String[][] suggestedSwap = tower.swapToReduce();

        // Assert
        assertEquals(0, suggestedSwap.length);
        assertTrue(tower.ok());
    }

    private boolean containsItem(String[][] items, String type, String id) {
        return Arrays.stream(items)
                .anyMatch(item -> type.equals(item[0]) && id.equals(item[1]));
    }
}