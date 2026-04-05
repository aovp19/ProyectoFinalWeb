// ── PAGINADOR GENÉRICO ──
window.Paginador = function(opciones) {
    const {
        datos,
        contenedor,
        paginacion,
        info,
        porPagina = 10,
        renderFila
    } = opciones;

    let todos      = [...datos];
    let filtrados  = [...datos];
    let pagina     = 1;

    function render() {
        const inicio  = (pagina - 1) * porPagina;
        const slice   = filtrados.slice(inicio, inicio + porPagina);
        const total   = Math.ceil(filtrados.length / porPagina);

        // Info
        if (info) {
            document.getElementById(info).textContent =
                `${filtrados.length} resultado(s) — página ${pagina} de ${Math.max(1, total)}`;
        }

        // Filas
        const tbody = document.getElementById(contenedor);
        if (slice.length === 0) {
            tbody.innerHTML = `<tr><td colspan="20" style="text-align:center;padding:2rem;color:#888">
                Sin resultados</td></tr>`;
        } else {
            tbody.innerHTML = slice.map(renderFila).join('');
        }

        // Paginación
        const nav = document.getElementById(paginacion);
        if (!nav) return;
        if (total <= 1) { nav.innerHTML = ''; return; }

        let html = `<nav><ul class="pagination pagination-sm mb-0">`;
        html += `<li class="page-item ${pagina === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="return false" 
               onmousedown="__pag_${contenedor}.ir(${pagina - 1})">‹</a></li>`;
        for (let i = 1; i <= total; i++) {
            html += `<li class="page-item ${i === pagina ? 'active' : ''}">
                <a class="page-link" href="#" onclick="return false"
                   onmousedown="__pag_${contenedor}.ir(${i})">${i}</a></li>`;
        }
        html += `<li class="page-item ${pagina === total ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="return false"
               onmousedown="__pag_${contenedor}.ir(${pagina + 1})">›</a></li>`;
        html += `</ul></nav>`;
        nav.innerHTML = html;
    }

    const api = {
        ir(n) {
            const total = Math.ceil(filtrados.length / porPagina);
            if (n < 1 || n > total) return;
            pagina = n;
            render();
        },
        filtrar(fn) {
            filtrados = todos.filter(fn);
            pagina = 1;
            render();
        },
        limpiar() {
            filtrados = [...todos];
            pagina = 1;
            render();
        },
        actualizar(nuevosDatos) {
            todos     = [...nuevosDatos];
            filtrados = [...nuevosDatos];
            pagina    = 1;
            render();
        }
    };

    // Guardar referencia global para los onclick inline
    window[`__pag_${contenedor}`] = api;
    render();
    return api;
};