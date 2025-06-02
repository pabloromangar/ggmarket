document.addEventListener('DOMContentLoaded', function() {
  var buscador = document.getElementById('buscador');
  var resultados = document.getElementById('resultados');
  var overlay = document.getElementById('overlay');

  buscador.addEventListener('input', function() {
    var query = buscador.value.trim();

    if (query.length < 3) {
      resultados.style.display = 'none';
      resultados.innerHTML = '';
      return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/api/productos/digitales/buscar?nombre=' + encodeURIComponent(query), true);
    xhr.onreadystatechange = function() {
      if (xhr.readyState === 4) {
        if (xhr.status === 200) {
          try {
            var data = JSON.parse(xhr.responseText);
            console.log(data);
            if (data.length === 0) {
              resultados.innerHTML = '<div>No se encontraron productos</div>';
            } else {
              var html = '';
              for (var i = 0; i < data.length; i++) {
                var p = data[i];
                html += '<div data-id="' + p.id + '">' + p.nombre + ' - $' + p.precio + '</div>';
              }
              resultados.innerHTML = html;
            }
            resultados.style.display = 'block';
          } catch (e) {
            resultados.innerHTML = '<div>Error al parsear resultados</div>';
            resultados.style.display = 'block';
          }
        } else {
          resultados.innerHTML = '<div>Error al cargar resultados</div>';
          resultados.style.display = 'block';
        }
      }
    };
    xhr.send();
  });

  buscador.addEventListener('focus', function() {
    overlay.style.display = 'block';
  });

  buscador.addEventListener('blur', function() {
    setTimeout(function() {
      overlay.style.display = 'none';
      resultados.style.display = 'none';
    }, 200);
  });

  resultados.addEventListener('click', function(e) {
    var target = e.target;
    if (target && target.hasAttribute('data-id')) {
      var id = target.getAttribute('data-id');
      window.location.href = '/productos/digitales/' + id;
    }
  });

  overlay.addEventListener('click', function() {
    buscador.blur();
  });
});
