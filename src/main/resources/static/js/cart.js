// Este console.log te ayuda a confirmar que el archivo se está cargando.
// Búscalo en la consola de tu navegador (F12).
console.log("cart.js: Cargado y listo.");

// Esperamos a que todo el HTML de la página esté completamente cargado.
document.addEventListener('DOMContentLoaded', function() {

    // Comprobamos si la variable global de autenticación existe.
    // Esta variable debe ser definida en tu HTML con Thymeleaf.
    // <script th:inline="javascript">const userIsAuthenticated = ...;</script>
    // --- DELEGACIÓN DE EVENTOS ---
    // En lugar de añadir un listener a cada botón, añadimos uno solo al <body>.
    // Esto es más eficiente y funciona mejor con contenido dinámico.
    document.body.addEventListener('click', function(event) {
        
        // Comprobamos si el elemento en el que se hizo clic (o uno de sus padres)
        // tiene la clase '.btn-add-to-cart'.
        const cartButton = event.target.closest('.btn-add-to-cart');

        // Si el clic no fue en un botón del carrito, no hacemos nada y salimos de la función.
        if (!cartButton) {
            return; 
        }

        // --- SI LLEGAMOS AQUÍ, SIGNIFICA QUE SE HIZO CLIC EN UN BOTÓN DEL CARRITO ---

        // ¡LA SOLUCIÓN CLAVE! Prevenimos la acción por defecto y detenemos la propagación.
        event.preventDefault();
        event.stopPropagation();
        
        console.log("cart.js: Clic en 'Añadir al Carrito' interceptado. Producto ID: " + cartButton.dataset.productId);

        // 1. Lógica de Autenticación
        if (!userIsAuthenticated) {
            console.log("cart.js: Usuario no autenticado. Redirigiendo a /login.");
            window.location.href = '/login';
            return; // Detenemos la ejecución
        }

        // 2. Obtención de datos para la petición AJAX
        const productId = cartButton.dataset.productId;
        const csrfTokenElement = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderElement = document.querySelector('meta[name="_csrf_header"]');

        if (!csrfTokenElement || !csrfHeaderElement) {
            showErrorPopup('Error de seguridad (CSRF). Por favor, recarga la página.');
            return;
        }

        const csrfToken = csrfTokenElement.getAttribute('content');
        const csrfHeaderName = csrfHeaderElement.getAttribute('content');

        // 3. Petición AJAX (XMLHttpRequest)
        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/api/carrito/agregar', true);
        
        // Cabeceras
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.setRequestHeader(csrfHeaderName, csrfToken);

        // Callback para cuando la petición finalice
        xhr.onload = function() {
            if (xhr.status >= 200 && xhr.status < 300) {
                try {
                    const data = JSON.parse(xhr.responseText);
                    if (data.success) {
                        showSuccessPopup(data.message || '¡Producto añadido al carrito!');
                    } else {
                        showErrorPopup(data.message || 'No se pudo añadir el producto.');
                    }
                } catch (e) {
                    showErrorPopup('Respuesta inválida del servidor.');
                }
            } else if (xhr.status === 403) {
                console.log("cart.js: Sesión expirada o acceso denegado (403). Redirigiendo a /login.");
                window.location.href = '/login';
            } else {
                showErrorPopup('Error del servidor (Código: ' + xhr.status + ').');
            }
        };

        // Callback para errores de red
        xhr.onerror = function() {
            showErrorPopup('Error de conexión. No se pudo contactar al servidor.');
        };
        
        // Creamos y enviamos el cuerpo de la petición
        const payload = JSON.stringify({
            productoId: productId,
            cantidad: 1
        });
        
        console.log("cart.js: Enviando petición AJAX a /api/carrito/agregar...");
        xhr.send(payload);
    });

    // --- Funciones auxiliares para notificaciones ---
    function showSuccessPopup(message) {
        // Reemplaza este alert con una notificación más elegante si lo deseas
        alert(message);
    }

    function showErrorPopup(message) {
        // Reemplaza este alert con una notificación de error
        alert(message);
    }
});