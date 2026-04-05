(function () {
    // Overlay einmalig erzeugen
    const overlay = document.createElement("div");
    overlay.className = "nav-overlay";
    document.body.appendChild(overlay);

    function openMenu() {
        const links = document.querySelector(".myLinks");
        if (!links) return;
        links.classList.add("open");
        overlay.classList.add("open");
        document.body.style.overflow = "hidden"; // Scrollen sperren
    }

    function closeMenu() {
        const links = document.querySelector(".myLinks");
        if (!links) return;
        links.classList.remove("open");
        overlay.classList.remove("open");
        document.body.style.overflow = "";
    }

    // Globale Funktion – weiterhin per onclick="myFunction()" aufrufbar
    window.myFunction = function () {
        const links = document.querySelector(".myLinks");
        if (links && links.classList.contains("open")) {
            closeMenu();
        } else {
            openMenu();
        }
    };

    // Overlay schließt das Menü
    overlay.addEventListener("click", closeMenu);

    // Escape-Taste schließt das Menü
    document.addEventListener("keydown", function (e) {
        if (e.key === "Escape") closeMenu();
    });
})();