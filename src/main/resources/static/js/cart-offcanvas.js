document.addEventListener('DOMContentLoaded', () => {
    
    // ===================================================================
    // 1. DECLARACIÓN DE ELEMENTOS DEL DOM (Ámbito superior)
    // ===================================================================
    const cartOffcanvasElement = document.getElementById('cartOffcanvas');
    if (!cartOffcanvasElement) return;

    // Estas variables ahora son accesibles por TODAS las funciones dentro de este bloque.
    const itemsContainer = document.getElementById('cart-offcanvas-items-container');
    const placeholder = document.getElementById('cart-offcanvas-placeholder');
    const subtotalEl = document.getElementById('cart-offcanvas-subtotal');
    const totalEl = document.getElementById('cart-offcanvas-total');
    const cartCounter = document.getElementById('cart-item-count');

    // Comprobación defensiva: si faltan elementos clave, no continuamos.
    if (!itemsContainer || !placeholder || !subtotalEl || !totalEl) {
        console.error("Error crítico: Faltan elementos esenciales del DOM en el offcanvas del carrito.");
        return;
    }

    // ===================================================================
    // FUNCIÓN: Cargar el contenido del carrito
    // ===================================================================
    const loadCartContent = async () => {
        itemsContainer.innerHTML = ''; 
        placeholder.textContent = 'Cargando productos...';
        placeholder.style.display = 'block';

        try {
            const response = await fetch('/api/carrito/contenido');
            if (!response.ok) throw new Error('Respuesta del servidor no fue OK');
            
            const data = await response.json();
            
            placeholder.style.display = 'none';

            if (data.items && data.items.length > 0) {
                data.items.forEach(item => {
                    itemsContainer.insertAdjacentHTML('beforeend', createCartItemHTML(item));
                });
            } else {
                placeholder.textContent = 'Tu carrito está vacío.';
                placeholder.style.display = 'block';
            }
            updateCartTotals(data.total || 0, data.items ? data.items.length : 0);

        } catch (error) {
            placeholder.textContent = 'Error al cargar el carrito.';
            console.error('Error en loadCartContent:', error);
        }
    };

    // Carga el contenido cuando se abre el offcanvas
    cartOffcanvasElement.addEventListener('show.bs.offcanvas', loadCartContent);

    // ===================================================================
    // GESTIÓN DE CLICS DENTRO DEL CARRITO
    // ===================================================================
    // ...
// GESTIÓN DE CLICS DENTRO DEL CARRITO (Un solo listener para todo)
// ===================================================================
itemsContainer.addEventListener('click', (event) => {
    
    // --- INICIO DE DEPURACIÓN DETALLADA ---
    console.log("-------------------------------------");
    console.log("Clic detectado dentro del contenedor del carrito.");

    const itemElement = event.target.closest('.cart-item');
    
    if (!itemElement) {
        console.log("El clic no fue dentro de un elemento '.cart-item'. Ignorando.");
        return;
    }
    console.log("Elemento '.cart-item' encontrado:", itemElement);

    const productId = itemElement.dataset.productId;
    console.log("Valor leído de 'itemElement.dataset.productId':", productId, "(Tipo: " + typeof productId + ")");
    // --- FIN DE DEPURACIÓN DETALLADA ---


    // Continuamos con la lógica normal...
    if (!productId) {
        alert("Error crítico: No se pudo obtener el ID del producto desde el elemento HTML.");
        return;
    }

    const payload = { productId: productId }; // Usamos el valor leído

    if (event.target.closest('.delete-cart-item-btn')) {
        console.log("Acción: Eliminar. Enviando payload:", payload);
        handleUpdateCart('/api/carrito/eliminar', payload);
    
    } else if (event.target.closest('.btn-quantity-change')) {
        const quantityDisplay = itemElement.querySelector('.quantity-display');
        const change = parseInt(event.target.closest('.btn-quantity-change').dataset.change);
        const newQuantity = parseInt(quantityDisplay.textContent) + change;

        if (newQuantity < 1) {
            console.log("Acción: Eliminar (por cantidad < 1). Enviando payload:", payload);
            handleUpdateCart('/api/carrito/eliminar', payload);
        } else {
            payload.nuevaCantidad = newQuantity;
            console.log("Acción: Actualizar cantidad. Enviando payload:", payload);
            handleUpdateCart('/api/carrito/actualizar-cantidad', payload);
        }
    }
});
// ...

    // ===================================================================
    // FUNCIÓN REUTILIZABLE para peticiones AJAX
    // ===================================================================
   const handleUpdateCart = async (url, payload) => {
    
    // --- LÍNEA DE DEPURACIÓN FINAL ---
    console.log(`[handleUpdateCart] URL: ${url}`);
    console.log("[handleUpdateCart] Payload a enviar:", payload);
    console.log("[handleUpdateCart] Payload en formato JSON:", JSON.stringify(payload));
    // ---------------------------------
    
    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]')?.content;

        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', [csrfHeaderName]: csrfToken },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
             const errorData = await response.json().catch(() => ({}));
             throw new Error(errorData.message || 'Error en la respuesta del servidor');
        }

        const data = await response.json();

        if (data.success) {
            await loadCartContent();
        } else {
            alert(data.message || 'La operación falló.');
        }
    } catch (error) {
        console.error(`Error en handleUpdateCart (${url}):`, error);
        alert('Error de conexión: ' + error.message);
    }
};
    
    // ===================================================================
    // FUNCIONES AUXILIARES
    // ===================================================================
    const createCartItemHTML = (item) => {
        const subtotal = (item.precio * item.cantidad).toFixed(2);
        return `
            <div class="d-flex mb-3 cart-item" data-product-id="${item.productoId}">
                <img src="${item.imagenUrl || '/img/default.webp'}" alt="${item.nombre}" style="width: 64px; height: 64px; object-fit: cover; border-radius: 8px;">
                <div class="ms-3 flex-grow-1">
                    <p class="fw-bold mb-1 small">${item.nombre}</p>
                    <div class="d-flex align-items-center justify-content-between mt-2">
                        <div class="quantity-control d-flex align-items-center">
                            <button class="btn btn-sm text-white btn-quantity-change" data-change="-1">-</button>
                            <span class="mx-2 quantity-display">${item.cantidad}</span>
                            <button class="btn btn-sm text-white btn-quantity-change" data-change="1">+</button>
                        </div>
                        <span class="fw-bold item-subtotal">€${subtotal}</span>
                    </div>
                </div>
                <button class="btn btn-sm text-white ms-2 delete-cart-item-btn"><i class="bi bi-trash"></i></button>
            </div>
        `;
    };

    const updateCartTotals = (total, totalItems) => {
        // Ahora las variables del DOM son accesibles aquí
        if (subtotalEl) subtotalEl.textContent = `€${total.toFixed(2)}`;
        if (totalEl) totalEl.textContent = `€${total.toFixed(2)}`;
        
        if (cartCounter) {
            const spanNumero = cartCounter.querySelector('span:first-child');
            if (totalItems > 0) {
                if (spanNumero) spanNumero.textContent = totalItems;
                cartCounter.style.display = 'flex';
            } else {
                cartCounter.style.display = 'none';
            }
        }
    };
});