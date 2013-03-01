Índice
======

1 -¿Qué es Muki?
================
**Muki** es una herramienta que permite generar rápidamente el código que automatiza la comunicación entre los clientes iOS y el servidor JEE a través de un servicio RESTful. A partir de la descripción del servicio, Muki genera clases adicionales en Objective-C que pueden ser fácilmente integradas en las aplicaciones iOS y clases en Java que permiten implementar rápidamente un servicio RESTful siguiendo el estándar [JAX-RS](http://jax-rs-spec.java.net).

Todo el proceso de comunicación por HTTP y la serialización y conversión de datos entre las aplicaciones es realizado automáticamente por las clases generadas. 

![Muki1](muki1_es.png)

Así, con Muki las aplicaciones escritas para iOS pueden fácilmente conectarse con servicios RESTful implementados en Java. El código generado es 100% legible y limpio. Las clases generadas por Muki abstraen la comunicación entre los clientes y el servidor y ocultan los detalles de la comunicación que se realiza en HTTP (métodos GET, POST, PUT y DELETE) y de la serialización de los objetos que viajan en XML y JSON. El siguiente fragmento de código muestra las invocaciones que realiza una aplicación iOS para comunicarse con un servidor remoto, utilizando las clases generadas por Muki.

    // Instanciar el proxy para aceeder al servicio
    CustomerControllerStub  *service = [[CustomerControllerStub alloc] initControllerUrl: @"http://localhost:8080/demo-server/store"];
    
    // Enviar un objeto al servidor
    NSError *error;
    Customer *newCustomer = [[Customer alloc] init];
    newCustomer.name = @"Paul Smith";
    newCustomer.telephone = @"678 900 000";
    [service addCustomer:newCustomer error:&error];
    
    // Recuperar un objeto del servidor
    Customer *anotherCustomer = [service getCustomerId:@"12345667" error:&error];;


2 - Requerimientos para utilizar Muki
=====================================
2.1 - Requerimientos para correr el proceso de generación de clases
-------------------------------------------------------------------
*   Instalación de **Java** (JRE 1.5+)
*   muki-generator-1.0.jar
*   [commons-collections-3.2.1.jar](http://commons.apache.org/collections/), [commons-lang-2.4.jar](http://commons.apache.org/lang/), [velocity-1.6.1.jar](http://velocity.apache.org)
*   Nótese que otras versiones de las librerías posiblemente también funcionen, pero ésas son las versiones que hemos utilizado en nuestras pruebas.


2.2 - Requerimientos del cliente iOS
-------------------------------------------------------------------------
*   Es necesario tener una instalación de Xcode 4.5.x

2.3 - Requerimientos de la aplicación JEE
----------------------------------------------------------------------------
*   Un framework que implemente la especificación [JAX-RS](http://jax-rs-spec.java.net), como [RESTEasy](http://www.jboss.org/resteasy) y otros</li>
*   Opcional: es posible integrar las clases generadas con [Spring Framework<](http://www.springsource.org/spring-framework) y cualquier otro framework. En nuestras pruebas, hemos desplegado el servidor en [Google App Engine](https://developers.google.com/appengine/)utilizando las librerías de RESTEasy v2.0.1.


3 - ¿Cómo se utiliza Muki?
==========================
La creación de un servicio con Muki se resume en 3 pasos:
**PASO 1:** Crear la definición del servicio usando un documento XML. La definición contiene las operaciones que expone el servicio y las estructuras de datos que representan los parámetros y resources.
** PASO 2:** Invocar el proceso de generación, desde Java.
** PASO 3:** Integrar las clases generadas en las aplicaciones.

![Muki2](muki2_es.png)


4 - Definiendo un servicio
==========================
Los servicios creados por Muki comienzan con una descripción de las estructuras de datos y las operaciones para servir las peticiones de los clientes. La descripción del servicio debe estar disponible en un documento XML.

La descripción del servicio tiene 2 partes: por un lado está la definición de los **models**, que son las estructuras de datos que representan los parámetros de entrada y salida (resources); por otro lado están los **controllers** que procesan las peticiones HTTP y sirven los web resources del servicio.

El siguiente fragmento muestra la estructura de definición de un servicio en Muki. El esquema completo del documento XML está disponible en: [muki-service-description-v01.xsd](muki-service-description-v01.xsd).

	<ns2:project name="MukiDemo" xmlns:ns2="http://muki/service-description/">
	    <b><model-definitions ... ></b>
	        <model name="CustomerData">
	            ...
	        </model>
	        ...
	    <b></model-definitions></b>
	    <b><controller-definitions ... ></b>
	        <controller name="CustomerController" ... >
	            <get-operation ... />
	            <post-operation ... />
	            <put-operation ... />
	            <delete-operation ... />
	            ...
	        </controller>
	        ...
	    <b></controller-definitions></b>
	</ns2:project>


4.1 - Definiendo los modelos para representar los resources
-----------------------------------------------------------
En Muki, un **model** es un objeto que representa datos en la aplicación y que se usa enviar y recibir información entre los clientes iOS y el servidor. Un model puede verse como una clase que representan un web resource. Con la descripción de los *models*, Muki genera clases que automatizan el proceso de serializar y deserializar los objetos en formato JSON y XML. Para la serialización en Java, Muki genera las clases de los *models* con anotaciones JAXB. Para la serialización en iOS, Muki genera las clases de los *models* y además clases de soporte en Objective-C.

El siguiente fragmento muestra la definición de los *models*:

    <model-definitions java-package="...">
        <model name="Model1">
            <simple-attr type="..." name="..." />
            <simple-attr type="..." name="..." />
            ...
        </model>
        <model name="Model2">
            <simple-attr type="..." name="..." />
            <list-attr items-type="Model1" name="items" />
            ...
        </model>
        ...
    </model-definitions>

Las definiciones de los *models* van dentro de un elemento **&lt;model-definitions java-package = " ... "&gt;**. El valor del atributo **java-package** es el nombre del paquete donde se generarán las clases Java (beans). Cada model se define con un elemento **&lt;model name = " ... "&gt;**. El valor del atributo **name** es el nombre que se utiliza para generar la correspondiente clase en Java y en Objective-C. Cada model tiene uno o varios atributos, que pueden ser de tipo simple o bien representar listas. Los atributos simples pueden tener tipos básicos (boolean, integer, string, etc) o bien referencias a otro models. Los atributos para listas solo pueden tener referencias a otros models; no es posible tener listas de tipos básicos.

La siguiente tabla muestra los tipos básicos que utiliza Muki y sus correspondencias con tipos de Java y Objective-C cuando se generan las clases.

<table>
    <tr>
        <th align="center"><b>Muki basic type</b></th>
        <th align="center"><b>Java type</b></th>
        <th align="center"><b>Objective-C type</b></th>
    </tr>
    <tr>
        <td align="center">STRING</td>
        <td align="center">String</td>
        <td align="center">NSString</td>
    </tr>
    <tr>
        <td align="center">LONG</td>
        <td align="center">long</td>
        <td align="center">long long</td>
    </tr>
    <tr>
        <td align="center">INT</td>
        <td align="center">int</td>
        <td align="center">NSInteger</td>
    </tr>
    <tr>
        <td align="center">DOUBLE</td>
        <td align="center">double</td>
        <td align="center">double</td>
    </tr>
    <tr>
        <td align="center">BOOLEAN</td>
        <td align="center">boolean</td>
        <td align="center">BOOL</td>
    </tr>
</table>

Tomemos por ejemplo el siguiente diagrama con 2 *models*. Hay un model llamado **TrackData** que tiene atributos de tipos básicos (String, Long, Boolean, etc.) y también hay un model llamado **AlbumData** que tiene 2 atributos de tipo String, una referencia a **TrackData** y un atributo que es una lista de **TrackData**:

![Muki4](muki4_es.png)

El siguiente fragmento corresponde a la descripción que Muki requiere para crear las clases en Java y Objetive-C para el modelo anterior: 

    <model-definitions java-package="demo.model">
        <model name="TrackData">
            <simple-attr type="STRING" name="title" />
            <simple-attr type="INT" name="lengthInSeconds" />
            <simple-attr type="LONG" name="catalogId" />
            <simple-attr type="BOOLEAN" name="newRelease" />
            <simple-attr type="DOUBLE" name="price" />
        </model>
        <model name="AlbumData">
            <simple-attr type="LONG" name="catalogId" />
            <simple-attr type="STRING" name="title" />
            <simple-attr type="STRING" name="artist" />
            <simple-attr type="TrackData" name="mainTrack" />
            <list-attr items-type="TrackData" name="tracks" />
        </model>
    </model-definitions>

Con la definición anterior, Muki genera las siguientes clases en Java. Las clases tienen anotaciones JAXB para la serialización en XML y JSON:

	@XmlRootElement(name = "trackdata")
	@XmlType(name = "TrackData", propOrder = {})
	public class TrackData implements Serializable {
	    private static final long serialVersionUID = 1L;
	    private String title;
	    private int lengthInSeconds;
	    private long catalogId;
	    private boolean newRelease;
	    private double price;

	    @XmlElement(name = "title")
	    public String getTitle() {
	        return this.title;  
	    }
	 
	    public void setTitle(String newValue) {
	        this.title = newValue;  
	    }
	            
	    @XmlAttribute(name = "price")
	    public double getPrice() {
	        return this.price;  
	    }

	    ...
	}

Y también:

	@XmlRootElement(name = "albumdata")
	@XmlType(name = "AlbumData", propOrder = {})
	public class AlbumData implements Serializable {
	    private static final long serialVersionUID = 1L;
	    private long catalogId;
	    private String title;
	    private String artist;
	    private TrackData mainTrack;
	    private List<TrackData> tracks = new ArrayList<TrackData>();

	    @XmlElement(name = "title")
	    public String getTitle() {
	        return this.title;  
	    }
	 
	    @XmlElement(name = "mainTrack")
	    public TrackData getMainTrack() {
	        return this.mainTrack;  
	    }
	 
	    public void setMainTrack(TrackData newValue) {
	        this.mainTrack = newValue;  
	    }
	    
	    @XmlElementWrapper(name = "tracks")
	    @XmlElement(name = "trackdata")
	    public List<TrackData> getTracks() {
	        return this.tracks;  
	    }
	 
	    public void addToTracks(TrackData aValue) {
	        this.tracks .add(aValue);   
	    }
	    
	    public void removeFromTracks(TrackData aValue) {
	        this.tracks .remove(aValue);   
	    }

	    ...
	}

Con la definición anterior, Muki genera las siguientes clases en Objective-C. Nótese que a diferencia de Java, las clases de Objective-C no tienen anotaciones y todo el proceso de serialización en XML y JSON es realizado por clases auxiliares.

	@interface TrackData : NSObject {
	    NSString *title;
	    NSInteger lengthInSeconds;
	    long long catalogId;
	    BOOL newRelease;
	    double price;
	}
	    @property (nonatomic, strong) NSString *title;
	    @property NSInteger lengthInSeconds;
	    @property long long catalogId;
	    @property BOOL newRelease;
	    @property double price;
	    - (id)init;
	@end

Y también:

	@interface AlbumData : NSObject {
	    long long catalogId;
	    NSString *title;
	    NSString *artist;
	    TrackData *mainTrack;
	    NSMutableArray *tracks;
	}
	    @property long long catalogId;
	    @property (nonatomic, strong) NSString *title;
	    @property (nonatomic, strong) NSString *artist;
	    @property (nonatomic, strong) TrackData *mainTrack;
	    @property (nonatomic, strong) NSMutableArray *tracks;
	    - (id)init;
	    - (void)addToTracks: (TrackData *)anObject;
	    - (void)removeFromTracks: (TrackData *)anObject;
	@end

