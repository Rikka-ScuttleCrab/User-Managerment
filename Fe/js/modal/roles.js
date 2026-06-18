    // /Fe/js/modal/roles.js
    document.addEventListener("DOMContentLoaded", () => {
        const basePath = "/Fe/admin/modal/roles/";
        const modals = [
            "users.html",
            "create_edit.html",
            "delete.html"
        ];
        const container = document.getElementById("modal-content");
        modals.forEach(file => {
            fetch(basePath + file)
                .then(res => res.text())
                .then(html => {
                    container.insertAdjacentHTML("beforeend", html);
                });
        });
    });
    // fetch("/Fe/admin/users/modal/edit.html")
    //     .then(res => res.text())
    //     .then(html => {
    //     document.getElementById("modal-container").innerHTML = html;
    //     });
