Índice
======

1 -¿Qué es Muki?
================
**Muki** es una herramienta que permite generar rápidamente el código que automatiza la comunicación entre los clientes iOS y el servidor JEE a través de un servicio RESTful. A partir de la descripción del servicio, Muki genera clases adicionales en Objective-C que pueden ser fácilmente integradas en las aplicaciones iOS y clases en Java que permiten implementar rápidamente un servicio RESTful siguiendo el estándar [JAX-RS](http://jax-rs-spec.java.net).

Todo el proceso de comunicación por HTTP y la serialización y conversión de datos entre las aplicaciones es realizado automáticamente por las clases generadas. 

![Muki1](https://raw.github.com/97bytes/Muki/master/documentation/Muki1_es.png)

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

![Muki2](https://raw.github.com/97bytes/Muki/master/documentation/Muki2_es.png)


4 - Definiendo un servicio
==========================
Los servicios creados por Muki comienzan con una descripción de las estructuras de datos y las operaciones para servir las peticiones de los clientes. La descripción del servicio debe estar disponible en un documento XML.

La descripción del servicio tiene 2 partes: por un lado está la definición de los **models**, que son las estructuras de datos que representan los parámetros de entrada y salida (resources); por otro lado están los **controllers** que procesan las peticiones HTTP y sirven los web resources del servicio.

El siguiente fragmento muestra la estructura de definición de un servicio en Muki. El esquema completo del documento XML está disponible en: [muki-service-description-v01.xsd](muki-service-description-v01.xsd).
	&lt;ns2:project name="MukiDemo" xmlns:ns2="http://muki/service-description/"&gt;
	    <b>&lt;model-definitions ... &gt;</b>
	        &lt;model name="CustomerData"&gt;
	            ...
	        &lt;/model&gt;
	        ...
	    <b>&lt;/model-definitions&gt;</b>
	    <b>&lt;controller-definitions ... &gt;</b>
	        &lt;controller name="CustomerController" ... &gt;
	            &lt;get-operation ... /&gt;
	            &lt;post-operation ... /&gt;
	            &lt;put-operation ... /&gt;
	            &lt;delete-operation ... /&gt;
	            ...
	        &lt;/controller&gt;
	        ...
	    <b>&lt;/controller-definitions&gt;</b>
	&lt;/ns2:project&gt;

