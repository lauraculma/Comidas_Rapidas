FRONTEND - COMIDA RAPIDA
========================

Este resources está preparado para Spring Boot + Thymeleaf.

ESTRUCTURA
----------
templates/
  login.html
  dashboard-admin.html
  dashboard-vendedor.html
  dashboard-inventario.html
  usuarios.html
  usuario-form.html
  productos.html
  producto-form.html
  producto-composicion.html
  clientes.html
  cliente-form.html
  disponibilidad.html
  nueva-venta.html
  adiciones.html
  inventario.html
  materia-form.html
  movimientos.html
  movimiento-form.html
  perfil.html
  403.html
  fragments/sidebar.html
  fragments/topbar.html

static/
  css/app.css
  js/app.js
  img/logo.svg

DEPENDENCIA
-----------
Agregar spring-boot-starter-thymeleaf.

CRITERIO DE DISEÑO
------------------
- Interfaz propia, sin Bootstrap: paleta cálida, sidebar oscuro, jerarquía visual clara.
- Tres experiencias por tipo de usuario: Administrador, Vendedor e Inventario.
- El JS es solo de interfaz. La lógica de negocio debe permanecer en Model/Service.
- Los th:* son puntos de integración con Controller/Model.
- El sidebar espera una variable de vista `role`: ADMIN, VENDEDOR o INVENTARIO.
  Esa variable NO obliga a agregar atributo rol al modelo; el Controller puede derivarla del tipo concreto Usuario.
- producto-composicion.html soporta la relación ProductoComidaRapida -> ComposicionProducto -> MateriaPrima.
- adiciones.html representa el caso de uso opcional Seleccionar adiciones.

RUTAS SUGERIDAS
---------------
GET  /login
GET  /dashboard

GET  /admin/usuarios
GET  /admin/usuarios/nuevo
GET  /admin/usuarios/{id}/editar
POST /admin/usuarios/guardar

GET  /admin/productos
GET  /admin/productos/nuevo
GET  /admin/productos/{id}/editar
GET  /admin/productos/{id}/composicion
POST /admin/productos/guardar

GET  /vendedor/clientes
GET  /vendedor/clientes/nuevo
POST /vendedor/clientes/guardar
GET  /vendedor/productos
GET  /vendedor/venta/nueva
GET  /vendedor/venta/adiciones

GET  /inventario
GET  /inventario/nueva-materia
POST /inventario/materias/guardar
GET  /inventario/movimientos
GET  /inventario/movimientos/nuevo
POST /inventario/movimientos/guardar

GET  /perfil
POST /perfil/actualizar
POST /logout
