# MS-ticket-system

Este proyecto consiste en un sistema de gestión de tickets basado en una arquitectura de microservicios, desarrollado como parte de la formación en **Ingeniería en Informática**. El sistema permite gestionar desde la creación de eventos y preventas hasta la validación de accesos y streaming.

## 🚀 Estructura del Proyecto

El sistema está dividido en los siguientes módulos independientes:

* **ms-eventos:** Gestión de eventos y programación.
* **ms-recintos:** Administración de lugares y mapas de asientos.
* **ms-tickets:** Emisión y control de boletos.
* **ms-ventas:** Procesamiento de transacciones comerciales.
* **ms-validacion:** Verificación de tickets en tiempo real.
* **ms-artistas:** Gestión de perfiles de artistas y promotores.
* **ms-preventa:** Lógica de acceso exclusivo previo a la venta general.
* **ms-devoluciones:** Gestión de reembolsos y cancelaciones.
* **ms-streaming:** Soporte para eventos virtuales y transmisiones.

## 🛠️ Tecnologías Utilizadas

* **Backend:** Java 21 con **Spring Boot 4.0.6**.
* **Base de Datos:** MySQL (conector J) gestionado a través de **Spring Data JPA**.
* **Comunicación:** **WebClient** (Spring WebFlux) para llamadas entre microservicios.
* **Productividad:** **Lombok** para reducción de código repetitivo y **Bean Validation** para integridad de datos.
* **DevOps:** Automatización con **GitHub Actions** y contenedorización con **Docker**.

## 👥 Equipo de Desarrollo

* Abrahan Vivas
* Renato Uribe
