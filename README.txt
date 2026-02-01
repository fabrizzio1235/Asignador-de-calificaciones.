Tarea 1 de la Asignatura Diseño de Software: Asignador de Calificaciones en Java

Integrantes del equipo:
Fabrizzio Sebastián España Chi
Francois Bilal Pageau Santos

Ya que nuestro código no requiere de ninguna librería de terceros, el método de ejecución es simple.
Hay de dos maneras: Meter los archivos .java en un IDE de confianza dentro de un nuevo proyecto y que el IDE haga su magia.
Seleccionar el archivo main.java.AsignadorDeCalificaciones.java en la Terminal y ejecutar el comando "javac main.java.AsignadorDeCalificaciones.java" para crear el archivo compilado.
Posteriormente, escribir el comando "java main.java.AsignadorDeCalificaciones" y el programa se ejecutará sin problemas.

El programa lee de un archivo tipo CSV (Comma Separated Values) la información de un salón de clases. Después, mete la información dentro de un ArrayList y permite al 
usuario asignar y modificar las calificaciones de cada uno de los estudiantes. Ya que todos los alumnos tengan su calificación, la aplicación permite crear un archivo
CSV donde se encontrará la lista de clases con sus calificaciones asignadas, ordenada de manera ascendente.

Modificación T2:

En la segunda parte de esta actividad, se agregó un sistema básico de inicio de sesión para el usuario, utilizando como "base de datos" un documento csv que se encuentra en el proyecto.
Además, se le habilitó al usuario la opción de escribir directamente desde el programa el nombre del archivo donde se encuentran las calificaciones y el nombre del archivo en donde se
guardarán los resultados de la asignación de calificaciones.

-Crear la clase main.java.Usuario tomó muy poco, ya que es muy simple. - 5 min.
-Crear el algoritmo de encriptamiento no tomó mucho tiempo, ya que una librería ya disponía del algoritmo. De todos modos, había que hacer un casteo del resultado de dicho algoritmo
para que encajara con el archivo CSV. - 15 min.
-Crear la clase de main.java.Login con todas sus funcionalidades (login, crearUsuario) fue lo que más tiempo demoró, ya que se trataba de unir todas las funcionalidades anteriores en un todo
y habilitar la interacción del usuario en este. Hubieron unos cuantos errores de capa 8 que se solucionaron al final del día. - 3 horas.
-Crear las funcionalidades extra que se mencionan en el final del primer párrafo no llevó mucho tiempo, ya que no era la primera vez que se hacía. - 15 min.

Modificación T3:

En la tercera parte de la unidad, se mejoró la interfaz de interacción con el usuario, por terminal. Ahora es mucho más flexible, intuitiva y práctica. Por lo que se modificaron e insertaron
nuevos métodos de clases ya existentes para lograrlo. Y se implementó la funcionalidad de generar reportes en PDF. Se integró el framework Maven para automatizar la gestión de dependencias, Es un gestor de librerías. En lugar de que tú busques, descargues y pegues archivos .jar manualmente, maven lee tu pom.xml, va a internet, descarga OpenPDF por ti y lo conecta a tu proyecto automáticamente. Si desea correrlo por la terminal dirijase a la raíz del project, una vez ahi ejecute los siguientes dos comandos 'mvn clean install' (compila y empaqueta todo el proyecto) para ejecutar es con 'mvn exec:java'. Al igual que es posible correrlo por medio de una IDE como intelliJ por ejemplo.

Instalación de mvn: https://maven.apache.org/install.html

-La mejora de interfaz no fue complicado pero si tardado, porque se tenia que adaptar cierta parte del código, la mayor parte del tiempo se lo llevó el planteamiento de implementación, adaptación de código anterior, implementar mas validaciones y  unos cuantos errores causados por la lógica del progrmador - 1 hora
-Elegir libreria de PDF, se tenían 3 opciones, el propuesto por el profesor, uno que funciona como html (se cuenta con la experiencia de este), y openPDF. Al final se eligió el que era más sencillo de usar, ya que iba a ser usado para algo sencillo se utilizo la libreria openPDF -15 min
-Implementación del framework maven Este no fue complicado ya que se contaba con experiencia-15 min
-Crear clase main.java.GeneradorReportePDF y su implementación fue muy sencilla gracias a la liberia elegida, ya que no se necesitaba hacer grandes cosas se usaron muy pocos métodos, ya que era muy autónomo y se adaptaba my bien al propósito - 25 min


Modificación T4
En la cuarta parte se implementó la interfaz grafica al sistema, Se utilizo el framwork JAVAFX ya que se contaba con cierta experiencia por Asignaturas pasadas.

Para compilar/ejecutar este proyecto, se necesita el framework maven instalado (https://maven.apache.org/install.html)

Suponiendo que se esta en el directorio del proyecto, en la raíz de este. Desde la terminal, ejecutar los siguientes dos comandos:
Compilar ------> mvn clean install
ejecutar ------> mvn javafx:run

O bien tambíen contando con un IDE como intelliJ, Dentro del proyeto, Puede darle al run desde Main.
-Implementación de JAVAFX al proyecto y al pom, Esta parte fue complicada, ya que a pesar de haber interactuado co este framework, no se sabía con exactitud como implementarlo desde 0, ya que se tiene experiencia con este framweork por trabajar en proyectos previamente terminados de otros programadores, entonces inicar desde 0 fue un poco complicado, además de que para facilitarnos esta T4, debíamos adaptar mejor la estructura del proyecto, para que los framworks funcionen mejor -30min
-Diseño e Implementación de las interfaces por medio del formato fxml,  y con ayuda de la estilización CSS (se cuenta con experiencia previa). EL uso de ambos facilitó la creación de interfaces, pero era un tanto complicado, sobre todo en pensar cual sería la forma más sencilla de hacer una interfaz intuitiva. - 2 hrs
-Conectar los archivos FXML con los archivos java existentes de la T3, fue sin duda la parte más complicada del proyecto, ya que se tuvo que agregar, modificar y borrar logica, de lo que se tenia anteriormente, además de haber demasiados errores por parte de los programadores. - 5hrs