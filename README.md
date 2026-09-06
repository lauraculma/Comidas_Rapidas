# Sistema de Gestión de Comidas Rápidas (FastFood Management System)

Aplicación de escritorio desarrollada en **Java** bajo el paradigma de **Programación Orientada a Objetos (POO)** y arquitectura por capas (MVC / DAO). El sistema centraliza las operaciones de atención comercial, control de clientes y actualización de existencias de inventario en tiempo real con persistencia en base de datos relacional.

---

## 📋 Tabla de Contenidos
- [Descripción General](#-descripción-general)
- [Características Principales y Roles](#-características-principales-y-roles)
- [Arquitectura y Estructura del Proyecto](#-arquitectura-y-estructura-del-proyecto)
- [Requisitos del Sistema](#-requisitos-del-sistema)
- [Configuración y Puesta en Marcha](#-configuración-y-puesta-en-marcha)
- [Control de Calidad y Métricas](#-control-de-calidad-y-métricas)
- [Integrantes del Grupo](#-integrantes-del-grupo)

---

## 📌 Descripción General

El software resuelve la necesidad operativa de un establecimiento de comidas rápidas mediante la automatización de flujos comerciales:
* Gestión integral de clientes y catálogo de productos (CRUD).
* Registro de órdenes y facturación en el punto de pago.
* **Actualización y descuento automático de inventario** de forma atómica tras cada venta confirmada.
* Control de existencias mínimas para prevenir desabastecimientos.

---

## 👥 Características Principales y Roles

El sistema implementa control de acceso basado en tres perfiles operativos (**RBAC**):

### 1. 🛡️ Administrador
* Autenticación segura y administración de cuentas de usuario (creación, asignación de roles y bajas).
* Configuración de listas de precios y categorías de productos.
* Consulta de reportes consolidados de ventas y balance de existencias.

### 2. 💵 Cajero / Vendedor
* **CRUD de Clientes:** Registro, búsqueda por identificación y actualización de datos de contacto.
* **Punto de Venta (POS):** Visualización del menú de comidas rápidas con validación de disponibilidad en stock.
* **Liquidación de Pedidos:** Cálculo automático de subtotales, totales y emisión de comprobantes.
* **Sincronización:** Descuento inmediato de stock al confirmar el cobro.

### 3. 📦 Encargado de Inventario
* **CRUD de Productos:** Registro y mantenimiento del catálogo de comidas rápidas.
* **Entradas de Mercancía:** Abastecimiento y ajuste de existencias.
* **Registro de Mermas:** Bajas justificadas por productos caducados o defectuosos.
* **Alertas de Reposición:** Detección visual cuando el stock actual es menor o igual al umbral mínimo configurado.

---
