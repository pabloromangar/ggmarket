document.addEventListener('DOMContentLoaded', function() {
    
    // 1. Buscamos el elemento MÁS IMPORTANTE.
    const buscador = document.getElementById('buscador');

    // 2. ========= LA SOLUCIÓN CLAVE =========
    // Si el elemento 'buscador' NO existe en la página actual,
    // detenemos la ejecución de este script para evitar errores.
    if (!buscador) {
        return; // Salimos de la función y no hacemos nada más.
    }

    // 3. Si llegamos aquí, significa que el buscador SÍ existe.
    // Ahora podemos buscar el resto de elementos y añadir los listeners de forma segura.
    const resultados = document.getElementById('resultados');
    const overlay = document.getElementById('overlay');
    const defaultImg = '/img/default.webp';

    buscador.addEventListener('input', function() {
        const query = buscador.value.trim();

        if (query.length < 3) {
            resultados.style.display = 'none';
            resultados.innerHTML = '';
            return;
        }

        // Usamos fetch, que es un poco más moderno que XMLHttpRequest
        fetch('/api/productos/digitales/buscar?nombre=' + encodeURIComponent(query))
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la respuesta del servidor');
                }
                return response.json();
            })
            .then(data => {
                if (data.length === 0) {
                    resultados.innerHTML = '<div class="p-3">No se encontraron productos</div>';
                } else {
                    // Usamos map y join para construir el HTML, es más limpio
                    const html = data.map(p => `
                        <a href="/tienda/productoDigital/${p.id}" class="list-group-item list-group-item-action d-flex align-items-center">
                            <img src="${p.imagenUrl || defaultImg}" alt="${p.nombre}" style="width: 50px; height: 50px; object-fit: cover; margin-right: 15px;">
                            <div>
                                <div class="fw-bold">${p.nombre}</div>
                                <div>€ ${p.precio.toFixed(2)}</div>
                            </div>
                        </a>
                    `).join('');
                    resultados.innerHTML = `<div class="list-group list-group-flush">${html}</div>`;
                }
                resultados.style.display = 'block';
            })
            .catch(error => {
                console.error('Error en la búsqueda:', error);
                resultados.innerHTML = '<div class="p-3 text-danger">Error al cargar resultados</div>';
                resultados.style.display = 'block';
            });
    });

    buscador.addEventListener('focus', function() {
        overlay.style.display = 'block';
    });

    buscador.addEventListener('blur', function() {
        // Usamos un pequeño retardo para permitir que se haga clic en los resultados
        setTimeout(function() {
            overlay.style.display = 'none';
            resultados.style.display = 'none';
        }, 200);
    });

    // Nota: El listener de 'click' en 'resultados' no es necesario
    // porque ahora los resultados son etiquetas <a> que ya tienen el enlace.

    overlay.addEventListener('click', function() {
        buscador.blur(); // Esto oculta el overlay y los resultados
    });
});