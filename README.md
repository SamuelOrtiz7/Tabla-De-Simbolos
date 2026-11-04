# <span style="color:#1E88E5">TABLA DE SÍMBOLOS</span>

## <span style="color:#00897B">AUTÓMATAS Y COMPILADORES</span>

Proyecto académico sobre las tablas de símbolos
y su funcionamiento dentro de los compiladores.

---

## <span style="color:#00897B">Contenido</span>

1. Introducción
2. Definición
3. Características
4. Operaciones
5. Estructuras de datos
6. Ámbitos
7. Caso práctico
8. Conclusiones
9. Bibliografía

---

# <span style="color:#1E88E5">1. Introducción</span>

La **<span style="color:#F57C00">tabla de símbolos</span>** es una estructura de datos
utilizada durante el proceso de compilación.

Un compilador traduce un programa fuente a otro lenguaje
y lo hace por fases:

- Análisis léxico
- Análisis sintáctico
- Análisis semántico
- Generación de código intermedio
- Optimización
- Generación de código objeto

En todas estas fases el compilador necesita recordar
qué nombres aparecen en el programa y qué significan.
Esa información se guarda en la tabla de símbolos.

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

---

## <span style="color:#00897B">Relación con los autómatas</span>

El analizador léxico se construye con **<span style="color:#F57C00">autómatas finitos</span>**.
El autómata reconoce que `contador` es un identificador válido,
pero no recuerda si ya se declaró ni de qué tipo es.

Esa información se guarda en la tabla de símbolos,
que funciona como **<span style="color:#F57C00">puente</span>** entre el análisis léxico
y el análisis semántico.

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

---

## <span style="color:#00897B">Herramientas generadoras</span>

Herramientas como Lex/Yacc, JFlex/Cup o JavaCC
generan el analizador léxico y el sintáctico,
pero **<span style="color:#F57C00">no generan la tabla de símbolos</span>**.

El programador debe implementarla y usarla
desde las acciones semánticas.

<span style="color:#757575">**Fuente:** Gálvez Rojas, S. y Mora Mata, M. Á. (2005).
*Java a tope: Traductores y compiladores con Lex/Yacc, JFlex/Cup y JavaCC* (Ed. electrónica).
Universidad de Málaga.</span>

---

# <span style="color:#1E88E5">2. Definición</span>

La tabla de símbolos es una **<span style="color:#F57C00">estructura de datos</span>**
que guarda información sobre los identificadores
del programa fuente.

Cada entrada relaciona un nombre con sus atributos:

- **<span style="color:#F57C00">Nombre:</span>** el lexema, por ejemplo `suma`
- **<span style="color:#F57C00">Categoría:</span>** variable, constante, función o parámetro
- **<span style="color:#F57C00">Tipo:</span>** `int`, `float`, `boolean`, etc.
- **<span style="color:#F57C00">Ámbito:</span>** global, local o de bloque
- **<span style="color:#F57C00">Dirección:</span>** posición o desplazamiento en memoria
- **<span style="color:#F57C00">Parámetros:</span>** número y tipo, si es una función

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

---

## <span style="color:#00897B">La tabla como diccionario</span>

La tabla de símbolos funciona como un **<span style="color:#F57C00">diccionario</span>**:

- La palabra que se busca es el nombre del identificador.
- La definición son sus atributos.
- Debe permitir agregar, buscar y borrar de forma rápida.

Ejemplo:

- `x` → variable, `int`, global
- `suma` → función, recibe `(int, int)` y devuelve `float`

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

---

# <span style="color:#1E88E5">3. Características</span>

## <span style="color:#00897B">Es compartida por todas las fases</span>

- El analizador léxico o sintáctico **<span style="color:#F57C00">crea</span>** las entradas.
- El analizador semántico **<span style="color:#F57C00">agrega y verifica</span>** los tipos.
- El generador de código **<span style="color:#F57C00">consulta</span>** direcciones y tamaños.

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

## <span style="color:#00897B">Es eficiente y dinámica</span>

- Se consulta cada vez que aparece un identificador,
  por eso debe ser **<span style="color:#F57C00">rápida</span>**.
- **<span style="color:#F57C00">Crece</span>** conforme aparecen declaraciones.
- Al salir de un bloque se **<span style="color:#F57C00">borran</span>** las variables locales.

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

## <span style="color:#00897B">Depende del lenguaje</span>

Los atributos que se guardan cambian según
lo que el lenguaje necesite verificar y traducir.

<span style="color:#757575">**Fuente:** Gálvez Rojas, S. y Mora Mata, M. Á. (2005).
*Java a tope: Traductores y compiladores con Lex/Yacc, JFlex/Cup y JavaCC* (Ed. electrónica).
Universidad de Málaga.</span>

---

# <span style="color:#1E88E5">4. Operaciones</span>

## <span style="color:#00897B">Operaciones básicas</span>

- **<span style="color:#F57C00">Insertar:</span>** agrega un nombre con sus atributos.
  Se usa al encontrar una declaración como `int x;`
- **<span style="color:#F57C00">Buscar:</span>** devuelve los atributos de un nombre
  o indica que no existe. Se usa en cada uso, como `x = 5;`
- **<span style="color:#F57C00">Eliminar:</span>** quita los nombres que dejan de ser visibles.
  Se usa al terminar un bloque o una función.

Antes de insertar, el compilador busca en el ámbito actual
para detectar si la variable ya fue declarada.

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

## <span style="color:#00897B">Operaciones de ámbito</span>

- **<span style="color:#F57C00">Entrar a un ámbito:</span>** se abre un nuevo nivel en la tabla.
- **<span style="color:#F57C00">Salir de un ámbito:</span>** se borran las declaraciones locales
  y se regresa al nivel anterior.

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

## <span style="color:#00897B">Actualizar atributos</span>

El analizador léxico puede guardar solo el nombre
y después el analizador semántico **<span style="color:#F57C00">completa</span>**
el tipo y la dirección.

<span style="color:#757575">**Fuente:** Gálvez Rojas, S. y Mora Mata, M. Á. (2005).
*Java a tope: Traductores y compiladores con Lex/Yacc, JFlex/Cup y JavaCC* (Ed. electrónica).
Universidad de Málaga.</span>

---

# <span style="color:#1E88E5">5. Estructuras de datos</span>

## <span style="color:#00897B">Opciones de implementación</span>

- **<span style="color:#F57C00">Lista lineal:</span>** búsqueda O(n).
  Fácil de programar, pero lenta con muchos nombres.
- **<span style="color:#F57C00">Árbol binario de búsqueda:</span>** búsqueda O(log n).
  Mantiene los nombres ordenados, pero puede desbalancearse.
- **<span style="color:#F57C00">Tabla hash:</span>** búsqueda O(1) en promedio.
  Es la más rápida y la más usada en compiladores.

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

## <span style="color:#00897B">Tabla hash con encadenamiento</span>

- Una **<span style="color:#F57C00">función hash</span>** convierte el nombre en una posición.
- Cada posición (**<span style="color:#F57C00">cubeta</span>**) apunta a una lista de entradas.
- Si dos nombres caen en la misma cubeta, se encadenan.

La nueva declaración se inserta al inicio de la lista,
así la búsqueda encuentra primero la más interna.

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

## <span style="color:#00897B">Una tabla por ámbito</span>

Cada ámbito tiene su propia tabla, enlazada
con la tabla del ámbito que la contiene.
La búsqueda va del ámbito actual **<span style="color:#F57C00">hacia afuera</span>**.

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

En Java esto se implementa con `HashMap` o `Hashtable`.

<span style="color:#757575">**Fuente:** Gálvez Rojas, S. y Mora Mata, M. Á. (2005).
*Java a tope: Traductores y compiladores con Lex/Yacc, JFlex/Cup y JavaCC* (Ed. electrónica).
Universidad de Málaga.</span>

---

# <span style="color:#1E88E5">6. Ámbitos</span>

## <span style="color:#00897B">Definición de ámbito</span>

El **<span style="color:#F57C00">ámbito</span>** es la parte del programa
donde una declaración es visible.

La tabla de símbolos aplica las reglas de ámbito
para relacionar cada uso de un nombre
con su declaración correcta.

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

## <span style="color:#00897B">Tipos de ámbito</span>

- **<span style="color:#F57C00">Estático:</span>** se decide por cómo está escrito el programa.
  Lo usan C, Java y Pascal.
- **<span style="color:#F57C00">Dinámico:</span>** depende del orden de las llamadas
  en tiempo de ejecución.

Niveles típicos:

- **<span style="color:#F57C00">Global:</span>** todo el programa
- **<span style="color:#F57C00">De función:</span>** parámetros y variables locales
- **<span style="color:#F57C00">De bloque:</span>** lo que está entre llaves

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

## <span style="color:#00897B">Regla del anidamiento más cercano</span>

Si un nombre se declara en varios bloques,
gana la declaración del **<span style="color:#F57C00">bloque más interno</span>**.

- `int x = 1;` → x global
- `void f() {`
- `int x = 2;` → oculta a la x global
- `printf("%d", x);` → imprime 2
- `}`

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

---

# <span style="color:#1E88E5">7. Caso práctico</span>

## <span style="color:#00897B">Programa a analizar</span>

- `int x;` → nivel 0 (global)
- `float suma(int a, int b) {` → nivel 1 (función)
- `float x;` → oculta a la x global
- `x = a + b;`
- `{`
- `int i;` → nivel 2 (bloque)
- `i = 0;`
- `}`
- `return x;`
- `}`

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

## <span style="color:#00897B">Paso a paso</span>

1. `int x;` → inserta `x` (int) en el nivel global.
2. `float suma(int a, int b)` → inserta `suma`,
   entra al nivel 1 e inserta `a` y `b`.
3. `float x;` → no existe en el nivel 1, se inserta
   y oculta a la `x` global.
4. `x = a + b;` → encuentra `x`, `a` y `b` en el nivel 1
   y verifica que los tipos sean compatibles.
5. `{ int i; }` → entra al nivel 2, inserta `i`
   y al cerrar la llave elimina ese nivel.
6. `return x;` → encuentra la `x` de tipo `float`.
7. Fin de la función → elimina el nivel 1.

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

## <span style="color:#00897B">Contenido de la tabla en el bloque interno</span>

- `i` → variable local, `int`, nivel 2
- `a` → parámetro, `int`, nivel 1
- `b` → parámetro, `int`, nivel 1
- `x` → variable local, `float`, nivel 1
- `x` → variable global, `int`, nivel 0
- `suma` → función `(int, int)` → `float`, nivel 0

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

## <span style="color:#00897B">Errores que detecta</span>

- **<span style="color:#E53935">No declarada:</span>** usar `i` fuera de su bloque.
- **<span style="color:#E53935">Redeclarada:</span>** escribir `int a;` dentro de `suma`.

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

## <span style="color:#00897B">Implementación en Java</span>

- `Simbolo` → clase con `nombre`, `categoria` y `tipo`
- `Deque<HashMap<String, Simbolo>>` → pila de ámbitos
- `entrarAmbito()` → `ambitos.push(new HashMap<>())`
- `salirAmbito()` → `ambitos.pop()`
- `insertar()` → revisa redeclaración y hace `put`
- `buscar()` → recorre del nivel interno al global

<span style="color:#757575">**Fuente:** Gálvez Rojas, S. y Mora Mata, M. Á. (2005).
*Java a tope: Traductores y compiladores con Lex/Yacc, JFlex/Cup y JavaCC* (Ed. electrónica).
Universidad de Málaga.</span>

---

# <span style="color:#1E88E5">8. Conclusiones</span>

- La tabla de símbolos **<span style="color:#F57C00">une todas las fases</span>** del compilador.
- Sus operaciones principales son **<span style="color:#F57C00">insertar, buscar y eliminar</span>**.
- La **<span style="color:#F57C00">tabla hash</span>** es la estructura más eficiente.
- Permite detectar variables **<span style="color:#E53935">no declaradas</span>** o **<span style="color:#E53935">redeclaradas</span>**.

<span style="color:#757575">**Fuente:** Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
*Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
Pearson Educación.</span>

<span style="color:#757575">**Fuente:** Louden, K. C. (2004).
*Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
International Thomson Editores.</span>

- Con JFlex/Cup o JavaCC la tabla se programa a mano,
  por eso entenderla es **<span style="color:#F57C00">necesario</span>** para construir un compilador.

<span style="color:#757575">**Fuente:** Gálvez Rojas, S. y Mora Mata, M. Á. (2005).
*Java a tope: Traductores y compiladores con Lex/Yacc, JFlex/Cup y JavaCC* (Ed. electrónica).
Universidad de Málaga.</span>

---

# <span style="color:#1E88E5">9. Bibliografía</span>

- Aho, A. V., Lam, M. S., Sethi, R. y Ullman, J. D. (2008).
  *Compiladores: principios, técnicas y herramientas* (2.ª ed.; A. Vidal Romero Elizondo, Trad.).
  Pearson Educación.
  [Registro del libro](https://biblioteca.frro.utn.edu.ar/bib/557)

- Gálvez Rojas, S. y Mora Mata, M. Á. (2005).
  *Java a tope: Traductores y compiladores con Lex/Yacc, JFlex/Cup y JavaCC* (Ed. electrónica).
  Universidad de Málaga.
  [Datos del libro](https://pt.slideshare.net/slideshow/java-a-tope/13532689)

- Louden, K. C. (2004).
  *Construcción de compiladores: principios y práctica* (E. Alatorre Miguel, Trad.).
  International Thomson Editores.
  [Registro del libro](https://catalogosiidca.csuca.org/Record/UP.101395?lng=en)