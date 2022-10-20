# jwt-swagger-cucumber-envers-springboot

- Repositorio de [GitHub](https://github.com/emilioutn/desafio-tenpo)

## Comenzado..

A continuación, se describen una serie de pasos para poner el servicio a funcionar y procedimientos a tener en cuenta, 
en caso de realizar cambios.

## Requisitos

- [ ] JDK 17.
- [ ] Tener Docker instalado.
- [ ] Tener Docker compose instalado.
- [ ] Gradle versión 7.1+

## Configuración inicial

- [ ] Ubicarse en la raíz del proyecto, y ejecutar el siguiente comando:

```
docker-compose.yml up -d
```

- Este archivo permite generar contenedores docker de las siguientes imágenes:
 
Última versión de la Base de Datos Posgres, 
pgAdmin4 y 
Microservicio desafío.

- Al instalarse el motor de BD también se creará una nueva Base de Datos de nombre "tenpo".

- Cabe destacar que, la imagen del microservicio se encuentra alojada en DockerHub, bajo el nombre de usuario "criters".
Manualmente es posible descargarla con el siguiente comando:

```
docker pull criters/calculate-server
```

## Iniciar pgAdmin

Para iniciar pgAdmin, una vez iniciados los contenedores, dirigirse a la siguiente URL desde el navegador web: [pgAdmin4](http://localhost:5050)

Se desplagará la pantalla de loguin del servicio de pgAdmin, donde deberá colocar las siguientes credenciales:

        Usuario:    tenpo@tenpo.cl
        Contraseña: root

Una vez dentro, podrá generar una nueva conexión a la Base de Datos previamente generada.

### Configurar una conexión con posgres local

Para configurar una nueva conexión local, completar los siguientes datos: 

Dashboard -> Add New Server -> Add New Server

- Solapa General:

        Name: localhost
        
- Solapa Connection: 

        Hostname/address: local_pgdb
        port:             5432
        username:         tenpo
        password:         root

## Test y visualización

### Para ejecutar los casos de prueba 

- Abrir el proyecto y configurarlo para correr con jdk 17. 
- Desde IntelliJ hacer los siguientes pasos:
  - File -> Settings... -> Gradle -> en el apartado Gradle JVM, seleccionar la versión del jdk 17.
  - File -> Project Structure ->
    - Solapa Project: Seleccionar SDK 17, Proyect lenguage level: 17.
    - Solapa Modules: Verificar que se encuentre seleccionada la versión Lenguage level 17.
    - Solapa Platfolm Settings - SDKs -> Seleccionar 17.
- Compilar Gradle, nuevamente: 
  - click derecho en la raíz del proyecto -> Rebuild ..
- Ejecutar con click derecho, run a la clase principal de nombre: CalculateApplication.class 

En esta instancia ya será posible ejecutar los casos de prueba.

#### Nota
Se han definido un conjunto de Test Case, considerando los casos más relevantes.

## Consideraciones

El proyecto tiene las siguientes herramientas disponibles:

- Jwt y Spring Security para la seguridad.
- Swagger (se puede ingresar con la siguiente url: [Swagger](http://localhost:8080/tenpo/swagger-ui/)
- Envers (se puede verificar la traza de cada una de las entidades creadas/editadas). No se han expuesto endpoint para hacer pruebas de envers,
por no estar en el alcance de este proyecto. Sin embargo, es posible visualizar los cambios por medio de consultas a la Base de Datos.

internamente, además, se encuentra integrado con: 

- Lombok. 
- Mapstruct. 
- Checkstyle. 
- Zalando.

Si bien el proyecto cuenta con [Swagger](http://localhost:8080/tenpo/swagger-ui/) para poder realizar las pruebas, en la raíz del mismo, se encuentra un archivo de Postman que podría ser importado.
El mismo contiene todos los endpoint con la configuración inicial de parámetros de entrada a cada uno de los enpoint publicados.

## Postman y funcionalidad

Una vez importado, se visualizará el siguiente listado de endpoint. Los mismos se encuentran organizados según 
funcionalidad que se desea probar:

Users

  - (POST) 1 - Create standard user 
    - Crea un usuario Standard. Inicialmente la Base de Datos no cuenta con usuarios creados. 
    - No posee todos los permisos.
    - Debe ejecutarse primero, para poder hacer login posteriormente.

  - (PUT) 3 - Edit user 
    - Access Token es requerido.
    - Permite editar un usuario previamente creado.
    - Podrán editars los siguientes campos:
      - Contraseña.
      - Habilitado/deshabilitado.
      - ROL: Los Roles disponibles son: 
      ```
      ROLE_USER
      ROLE_ADMIN
      ```
      - Un usuario standard se crea con rol ROLE_USER. Cabe destacar que este rol no posee todos permisos.
      - Un usuario con rol ROLE_ADMIN poseerá todos los permisos del sistema.
      - Los roles son creados al inicio de la aplicación y no pueden agregarse ni modificarse.

  - (DEL) Delete User
    - Access Token es requerido.
    - Solo puede hacerlo un usuario con rol: ROLE_ADMIN
    - Elimina un usuario existente.

History

    - (GET) All Call History
      - Access Token es requerido.
      - Obtiene el historial, paginado, de todas las llamadas realizadas en el sistema.
      - Solo puede hacerlo un usuario con rol: ROLE_ADMIN

    - (GET) Call History by Method
      - Access Token es requerido.
      - Obtiene el historial, paginado, de todas las llamadas realizadas en el sistema, según Método.
      - Los métodos pueden ser: GET, POST, PUT, DELETE.
      - Solo puede hacerlo un usuario con rol: ROLE_ADMIN

    - (GET) Call History by Username
      - Access Token es requerido.
      - Obtiene el historial, paginado, de todas las llamadas realizadas en el sistema, según username.
      - Solo puede hacerlo un usuario con rol: ROLE_ADMIN

Calculate

    - (GET) Calculate
      - Access Token es requerido.
      - Contiene la lógica de petición del cálculo de dos números.
      - Se incluye un mock que permite generar, de manera aleatoria, errore de conexión al servidor de porcentajes.
      - Se incluye una clase NO mock, que permitiría apuntar a un servidor de porcentajes real. Actualmente se encuentra deshabilitado.
        Es posible habilitarlo, cambiando la etiqueta @Qualifier del servicio CalculateService.class.
      - Existe un cron que ejecuta cada 30 minutos y verifica si hay cambios de porcentajes, del servidor de porcentajes, 
        tomando en consideración lo pedido en el enunciado.
    
Login

    - Permite hacer el login de un usuario previamente creado. Del login se podrá obtener el Acces Token para poder 
      enviar a travez del Header de cada endpoint.
  
## Considerar al momento del desarrollo

Como el proyecto está desplegado en un contenedor Docker, junto a la Base de Datos, al momento de hacer modificaciones, se deberán realizar los siguientes pasos, 
para poder actualizar la imagen de docker:

- [ ] Generar nuevamente el JAR del proyecto.
- [ ] Generar nueva imagen del JAR. Para ello, en la raíz del proyecto ya se encuentra configurado un archivo Dockerfile. Por lo que únicamente será necesario ejecutar el siguiente comando, para generar dicha imagen:
        
        docker build -t criters/calculate-server .

- [ ] La imagen ya se ha subido a DockerHub. En caso de modificarla, será necesario actualizarla. 
Para ello, se debe conectar con el usuario "criters" y subir la imagen con una nueva versión.
Los pasos son:

  - Loguearse:

```
    docker login
```

  - Ingresar usuario y contraseña y luego hacer push a DockerHub:

```
    docker push criters/calculate-server:VX
```

, donde VX corresponde al tag de la nueva versión.

  - Ejecutar nuevamente el archivo docker-compose.yml que se encuentra en la raíz del proyecto:

```
docker-compose.yml up -d
```

## Enunciado

```
Debes desarrollar una API REST en Spring Boot utilizando java 11 o superior, con las siguientes funcionalidades:
a. Sign up usuarios.
b. Login usuarios.
c. Debe contener un servicio llamado por api-rest que reciba 2 números, los sume, y le aplique una suba de un porcentaje que debe ser adquirido de un servicio externo (por ejemplo, si el servicio recibe 5 y 5 como valores, y el porcentaje devuelto por el servicio externo es 10, entonces (5 + 5) + 10% = 11). Se deben tener en cuenta las siguientes consideraciones:
El servicio externo puede ser un mock, tiene que devolver el % sumado.
Dado que ese % varía poco, debe ser consumido cada media hora.
Si el servicio externo falla, se debe devolver el último valor retornado. Si no hay valor, debe retornar un error la api.
Si el servicio externo falla, se puede reintentar hasta 3 veces.
d. Historial de todos los llamados a todos los endpoint junto con la respuesta en caso de haber sido exitoso. Responder en Json, con data paginada. El guardado del historial de llamadas no debe sumar tiempo al servicio invocado.
e. El historial y la información de los usuarios se debe almacenar en una database PostgreSQL.
f. Incluir errores http. Mensajes y descripciones para la serie 4XX.


2. Se deben incluir tests unitarios.
3. Esta API debe ser desplegada en un docker container. Este docker puede estar en un dockerhub público. La base de datos también debe correr en un contenedor docker. Recomendación usar docker compose
4. Debes agregar un Postman Collection o Swagger para que probemos tu API
5. Tu código debe estar disponible en un repositorio público, junto con las instrucciones de cómo desplegar el servicio y cómo utilizarlo
```