import java.util.ArrayList;
import java.util.Collections; 
import java.util.Comparator;
import java.util.HashMap;

public class Tower {
    
    private static final int TOWER_X = 500; // Centro de pantalla
    private static final int BASE_Y = 900;  // Suelo
    private static final int BLOCK_SIZE = 25;
    
    private ArrayList<Cup> cups;
    private HashMap<Integer, Boolean> tappedCups;
    private boolean isVisible;
    private boolean isTap = false;
    
    

    public Tower() {
        cups = new ArrayList<>();
        tappedCups = new HashMap<>();
        isVisible = false;
    }

    
    public void pushCup(int size) {
        int id = size;
        
        // 1. Validar Duplicados
        for (Cup c : cups) {
            if (c.getId() == id) {
                System.out.println("--- ERROR: La taza " + id + " ya existe. ---");
                return;
            }
        }

        // 2. Crear Taza
        String color = getColorForSize(size);
        Cup newCup = new Cup(id, size, color);
        
        // Siempre nace libre
        tappedCups.put(newCup.getId(), false);
        
        // Centrado X
        int widthPx = newCup.getPixelWidth();
        int targetX = TOWER_X - (widthPx / 2);
        int targetY = 0; // Inicializar

        if (cups.isEmpty()) {
            // CASO BASE: Suelo
            targetY = BASE_Y - newCup.getRealPixelHeight();
            System.out.println("Base: Taza " + id + " en el suelo.");
        } 
        else {
            // --- ALGORITMO DE BÚSQUEDA DE SOPORTE ---
            
            Cup effectiveSupport = null; // La taza sobre la que nos apoyaremos físicamente
            boolean isNesting = false;   // ¿Nos apoyamos en el fondo (true) o en el borde (false)?
            
            // Recorremos desde la última taza hacia abajo
            for (int i = cups.size() - 1; i >= 0; i--) {
                Cup candidate = cups.get(i);
                
                // Si la candidata ya está tapada por otra cosa, la ignoramos y bajamos más
                if (tappedCups.get(candidate.getId())) {
                    continue;
                }
                
                // COMPARACIÓN DE TAMAÑO
                if (newCup.getSize() < candidate.getSize()) {
                    // ¡ENCONTRAMOS CONTENEDOR! (La nueva cabe en la candidata)
                    
                    if (effectiveSupport != null) {
                        // Caso "Sándwich" (7 -> 2 -> 5):
                        // Ya habíamos encontrado una taza menor (2) sobre la que apoyarnos.
                        // Ahora encontramos la contenedora (7).
                        // Nos quedamos apoyados sobre la menor (2), pero paramos de buscar.
                        System.out.println("   -> SÁNDWICH: " + id + " entra en " + candidate.getId() + " pero se apoya en " + effectiveSupport.getId());
                    } else {
                        // Caso "Anidar Simple" (7 -> 2):
                        // No nos habíamos apoyado en nada antes. Nos apoyamos en el fondo de esta.
                        effectiveSupport = candidate;
                        isNesting = true;
                        System.out.println("   -> ANIDAR: " + id + " al fondo de " + candidate.getId());
                    }
                    
                    // En cualquier caso, si cabemos aquí, dejamos de buscar hacia abajo.
                    break;
                    
                } else {
                    // ¡SOMOS MÁS GRANDES! (Apilar / Puente)
                    // La nueva es mayor que la candidata.
                    // Nos apoyamos sobre esta candidata (effectiveSupport).
                    // Y marcamos esta candidata como TAPADA.
                    
                    effectiveSupport = candidate;
                    isNesting = false; // Apilamos sobre su borde
                    
                    tappedCups.put(candidate.getId(), true); // La tapamos
                    
                    System.out.println("   ... " + id + " es mayor que " + candidate.getId() + ", bajando más...");
                    
                    // IMPORTANTE: NO hacemos break.
                    // Seguimos buscando abajo. Ejemplo (8 -> 6 -> 12):
                    // 12 tapa al 6. Sigue bajando.
                    // 12 tapa al 8. Sigue bajando.
                }
            }
            
            // --- CÁLCULO FINAL DE Y ---
            if (effectiveSupport != null) {
                if (isNesting) {
                    // Apoyarse en el fondo interno
                    int floorY = effectiveSupport.getY() + effectiveSupport.getRealPixelHeight();
                    int insideFloorY = floorY - BLOCK_SIZE;
                    targetY = insideFloorY - newCup.getRealPixelHeight();
                } else {
                    // Apoyarse en el borde superior (Apilar)
                    targetY = effectiveSupport.getY() - newCup.getRealPixelHeight();
                }
            } else {
                // Caso extremo: La taza es más grande que TODAS las anteriores.
                // Se apoya en el suelo (cubriendo a toda la torre)
                 targetY = BASE_Y - newCup.getRealPixelHeight();
                 System.out.println("   -> GIGANTE: " + id + " cubre toda la torre hasta el suelo.");
            }
        }

        // 3. Mover y Guardar
        newCup.moveTo(targetX, targetY);
        if (isVisible) newCup.makeVisible();
        cups.add(newCup);
    }
    


    public void popCup() {

    if (!cups.isEmpty()) {
        Cup removedCup = cups.remove(cups.size() - 1);
        removedCup.makeInvisible();
        
        // 1. Quitar la taza que se fue del mapa
        tappedCups.remove(removedCup.getId());
        
        // 2. ¡IMPORTANTE! Liberar a la taza que estaba debajo
        // Como tu lógica de "quién tapa a quién" es dinámica (bucle for),
        // es difícil saber exactamente cuál liberar sin recalcular.
        
        // OPCIÓN SENCILLA (Fuerza bruta):
        // Resetear todo el mapa a false y recalcular estado (menos eficiente pero seguro)
        // O simplemente asumir que la inmediatamente anterior queda libre si estaba tapada.
        
        if (!cups.isEmpty()) {
             Cup topCup = cups.get(cups.size() - 1);
             // Liberamos la nueva cima por si acaso estaba tapada
             tappedCups.put(topCup.getId(), false); 
        }
    }
    }


    public void makeVisible() {
        isVisible = true;
        for (Cup c : cups) {
            c.makeVisible();
        }
    }
    
    
    public void makeInvisible() {
        isVisible = false;
        for (Cup c : cups) {
            c.makeInvisible();
        }
    }

    private String getColorForSize(int s) {
        String[] colors = {"red", "blue", "green", "yellow", "magenta", "black"};
        return colors[s % colors.length];
    }
    // Método auxiliar para buscar tazas por ID en el ArrayList
    private Cup getCupById(int id) {
        for (Cup c : cups) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }
    
}