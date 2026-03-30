(function () {

    // ── CONFIGURACIÓN DE NAVEGACIÓN ──
    const NAV_ITEMS = [
        {
            section: "Principal",
            items: [
                { label: "Dashboard",  icon: "bi-grid",         href: "/dashboard.html" },
                { label: "Encuestas",  icon: "bi-file-text",    href: "/encuestas.html" },
                { label: "Ver en mapa",icon: "bi-map",          href: "/mapa.html"      },
            ]
        },
        {
            section: "Administración",
            adminOnly: true,
            items: [
                { label: "Usuarios", icon: "bi-people", href: "/usuarios.html", adminOnly: true }
            ]
        }
    ];

    // ── DETECTA LA PÁGINA ACTIVA ──
    function isActive(href) {
        return window.location.pathname === href ||
            window.location.pathname.endsWith(href);
    }

    // ── GENERA LAS INICIALES DEL USUARIO ──
    function getInitials() {
        const nombre   = localStorage.getItem("nombre")   || "";
        const apellido = localStorage.getItem("apellido") || "";
        if (nombre && apellido) return (nombre[0] + apellido[0]).toUpperCase();
        const email = localStorage.getItem("email") || "?";
        return email[0].toUpperCase();
    }

    function getDisplayName() {
        const nombre   = localStorage.getItem("nombre");
        const apellido = localStorage.getItem("apellido");
        if (nombre && apellido) return `${nombre} ${apellido}`;
        return localStorage.getItem("email") || "Usuario";
    }

    function getRol() {
        const rol = localStorage.getItem("rol") || "";
        return rol === "ADMIN" ? "Administrador" : "Encuestador";
    }

    // ── GENERA EL HTML DEL SIDEBAR ──
    function buildSidebar() {
        const rol = localStorage.getItem("rol");
        const isAdmin = rol === "ADMIN";

        let sectionsHTML = "";

        NAV_ITEMS.forEach(section => {
            if (section.adminOnly && !isAdmin) return;

            let itemsHTML = "";

            section.items.forEach(item => {
                if (item.adminOnly && !isAdmin) return;

                const active = isActive(item.href) ? "active" : "";

                itemsHTML += `
                <a href="${item.href}" class="sidebar-item ${active}">
                    <i class="bi ${item.icon}"></i>
                    ${item.label}
                </a>
            `;
            });

            sectionsHTML += `
            <div class="sidebar-section">
                <div class="sidebar-section-label">${section.section}</div>
                ${itemsHTML}
            </div>
        `;
        });

        return `
        <div class="mobile-topbar">
            <a href="/dashboard.html" class="sidebar-logo" style="text-decoration:none;">
                <div class="sidebar-logo-sq">
                    <i class="bi bi-clipboard-data-fill"></i>
                </div>
                <span class="sidebar-logo-name">ENCUESTAS PUCMM</span>
            </a>

            <button class="hamburger-btn" onclick="toggleSidebarMenu()">
                <i class="bi bi-list"></i>
            </button>
        </div>

        <div class="sidebar-menu" id="sidebarMenu">
            ${sectionsHTML}

            <div class="sidebar-spacer"></div>

            <a href="/perfil.html" class="sidebar-user" style="text-decoration:none;">
                <div class="sidebar-avatar">${getInitials()}</div>
                <div>
                    <div class="sidebar-user-name">${getDisplayName()}</div>
                    <div class="sidebar-user-role">${getRol()}</div>
                </div>
            </a>
        </div>
    `;
    }

    // ── INYECTA EL SIDEBAR EN EL DOM ──
    function injectSidebar() {
        const sidebar = document.querySelector(".sidebar");
        if (!sidebar) return;
        sidebar.innerHTML = buildSidebar();
    }

    // ── INYECTA LOS ESTILOS BASE (si no están ya) ──
    function injectStyles() {
        if (document.getElementById("base-styles")) return;
        const link = document.createElement("link");
        link.id   = "base-styles";
        link.rel  = "stylesheet";
        link.href = "/css/estilos.css";
        document.head.appendChild(link);

        const fonts = document.createElement("link");
        fonts.rel  = "stylesheet";
        fonts.href = "https://fonts.googleapis.com/css2?family=DM+Sans:wght@300;400;500;600&family=Playfair+Display:wght@600&display=swap";
        document.head.appendChild(fonts);
    }

    // ── FUNCIÓN GLOBAL PARA CERRAR SESIÓN ──
    window.cerrarSesion = function () {
        localStorage.clear();
        window.location.href = "/index.html";
    };

    window.toggleSidebarMenu = function () {
        document.getElementById("sidebarMenu").classList.toggle("show");
    };

    // ── INIT ──
    document.addEventListener("DOMContentLoaded", function () {
        injectStyles();
        injectSidebar();
    });

})();