**retrospectiva**



Mini-ciclo 1: Diseño estructural

Se definieron las clases principales (Tower, Cup, Lid), sus atributos y responsabilidades. Se justificó porque era necesario asegurar una correcta separación de responsabilidades antes de implementar lógica compleja.



Mini-ciclo 2: Construcción básica y representación visual

Se implementó la construcción geométrica de las tazas y tapas, junto con su visualización en el Canvas. Este ciclo permitió validar la correcta representación gráfica antes de trabajar en la lógica de apilamiento.



Mini-ciclo 3: Lógica de apilamiento y anidamiento

Se desarrolló la lógica para insertar tazas dentro de otras o encima según las reglas del problema. Fue necesario aislar este ciclo porque implicaba cálculos espaciales y validación estructural.



Mini-ciclo 4: Gestión de tapas

Se implementó la relación entre taza y tapa, garantizando que se movieran juntas y respetaran la altura máxima.



Mini-ciclo 5: Reordenamiento y validaciones

Se implementaron orderTower, reverseTower, eliminación de elementos y verificación de altura máxima.



Mini-ciclo 6: Consultas y control del simulador

Se desarrollaron métodos como height, stackingItems, liddedCups, ok y exit para completar los requisitos funcionales.



Cada mini-ciclo permitió validar funcionalidad incrementalmente y reducir errores acumulativos.



2\. ¿Cuál es el estado actual del proyecto en términos de mini-ciclos? ¿Por qué?



El proyecto se encuentra en un estado funcional completo. Todos los mini-ciclos han sido implementados e integrados. Actualmente el sistema cumple los requisitos funcionales y de usabilidad definidos, incluyendo validación de altura máxima, representación visual, control de errores y consistencia estructural.



3\. ¿Cuál fue el tiempo total invertido por cada uno de ustedes?



Tiempo total invertido: 20 horas/hombre

Distribución: 10 horas cada integrante.



4\. ¿Cuál consideran fue el mayor logro? ¿Por qué?\*\*



El mayor logro fue implementar correctamente la lógica de apilamiento y anidamiento respetando la altura máxima y el comportamiento conjunto de taza y tapa. Esto fue significativo porque implicó coordinar lógica geométrica, validación estructural y representación visual sin inconsistencias.



5\. ¿Cuál consideran que fue el mayor problema técnico? ¿Qué hicieron para resolverlo?\*\*



El mayor problema técnico fue sincronizar la lógica espacial (coordenadas en píxeles) con la lógica conceptual de la torre (bloques y centímetros). Inicialmente se generaban inconsistencias en altura y posicionamiento.



Se resolvió estandarizando el uso de BLOCK\_SIZE como unidad base, centralizando los cálculos de altura y reconstruyendo la torre completamente en cada reordenamiento para garantizar coherencia visual y estructural.



6\. ¿Qué hicieron bien como equipo? ¿Qué se comprometen a mejorar?\*\*



Se trabajó con buena distribución de responsabilidades: uno enfocado en lógica estructural y el otro en representación visual y validaciones. Se realizaron pruebas frecuentes tras cada mini-ciclo.



Como mejora, nos comprometemos a diseñar primero pruebas unitarias antes de implementar la lógica, para alinearnos mejor con la práctica de Testing en XP.



7\. Considerando las prácticas XP, ¿cuál fue la más útil? ¿Por qué?\*\*



La práctica más útil fue Designing con ciclos cortos. Dividir el desarrollo en mini-ciclos permitió detectar errores temprano y evitar acumulación de fallos estructurales.



También fue valioso el principio de simplicidad incremental, implementando primero comportamiento básico antes de complejizar la lógica.



8\. ¿Qué referencias usaron? ¿Cuál fue la más útil?\*\*



Se consultaron:



Oracle. (2023). Java Platform, Standard Edition API Specification. https://docs.oracle.com/javase/8/docs/api/



Kolling, M., \& Barnes, D. (BlueJ documentation). Using BlueJ.



OpenAI. (2025). ChatGPT.



Google. (2025). Gemini AI Assistant.



La referencia más útil fue la documentación oficial de Oracle para validar estructuras como ArrayList y HashMap, ya que permitió asegurar un uso correcto de las colecciones y evitar errores de tipo y referencia.

