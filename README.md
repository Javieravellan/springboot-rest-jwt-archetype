# spring-archetype
Arquetipo mínimo maven para proyectos REST con Spring boot.
Autenticación basada en Json Web Token, control de excepciones centralizado, Lombok y Mapstruct listo para usarse. 
También incorpora compatibilidad para la creación de imagen Docker al momento de la compilación.
## Requisitos para ejecución
- Maven
- Java version 17+

## Descargar repositorio
~~~bash
git clone https://github.com/javierchiquito/spring-archetype.git
cd ./spring-archetype
~~~

## Ejecución de prueba
Puede editar archivo archetype.properties en carpeta test/resources.
~~~bash
mvn clean verify
~~~

### Ver proyecto de salida en:
~~~bash
cd spring-archetype/target/test-classes/projects/basic/project/test
~~~

## Instalación
~~~bash
mvn clean install
~~~
Este comando instala el arquetipo dentro de su repositorio local de maven

## Generación de proyecto con el arquetipo
~~~bash
mvn archetype:generate \
-DarchetypeGroupId=com.eclipsoft.archetypes \
-DarchetypeArtifactId=spring-archetype \
-DarchetypeVersion=1.0.2-RC \
-DarchetypeCatalog=local
~~~
Este comando ejecutará el arquetipo en modo interactivo y le pedirá algunos datos para su proyecto como el nombre del paquete y algunos parámetros para la creación de la imagen de Docker
## Abra el proyecto generado en su IDE favorito
Antes de ejecutarlo, por favor de los permisos de ejecución a su archivo mvnw:
~~~bash
chmod +x ./mvnw
~~~

## Si desea crear un script para generar proyectos de manera facil, haga lo siguiente:
~~~bash
echo mvn archetype:generate \
-DarchetypeGroupId=com.eclipsoft.archetypes \
-DarchetypeArtifactId=spring-archetype \
-DarchetypeVersion=1.0.2-RC \
-DarchetypeCatalog=local >> sp-archetype.sh && chmod +x sp-archetype.sh
~~~
Luego, edite su archivo ~/.bashrc
~~~bash
echo 'alias sp-archetype="path/to/sp-archetype.sh"' >> ~/.bashrc
source ~/.bashrc
sp-archetype # ejecuta el arquetipo para crear su proyecto desde cualquier lugar.
# finalmente, después de la salida del proyecto:
cd ./project-generate
chmod +x ./mvnw
# Ejecución
./mvnw
~~~