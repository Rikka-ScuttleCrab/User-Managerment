
// /Fe/js/modal/users.js

document.addEventListener("DOMContentLoaded", () => {
    const modals = [
        "/Fe/admin/modal/users/create.html",
        "/Fe/admin/modal/users/role.html",
        "/Fe/admin/modal/users/reset-password.html"
    ];

    const container = document.getElementById("modal-content");

    modals.forEach(path => {
        fetch(path)
            .then(res => res.text())
            .then(html => {
                container.insertAdjacentHTML("beforeend", html);
            });
    });

    fetch("/Fe/admin/modal/users/edit.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("modal-container").innerHTML = html;
        });

    fetch("/Fe/admin/modal/users/active.html")
        .then(res => res.text())
        .then(html => {
            document.getElementById("confirm-box").innerHTML = html;
        });
});

/* CONFIRM MODAL */

function openConfirm(userId) {

    selectedUserId = userId;

    const user =
        AdminState.allUsers.find(
            user => user.id === userId
        );

    if (!user) {
        return;
    }

    const isDisable = user.active;

    const actionText =
        isDisable
            ? "vô hiệu hoá"
            : "kích hoạt";

    const actionColor =
        isDisable
            ? "#ff4d4f"
            : "#52c41a";

    document.getElementById(
        "confirmText"
    ).innerHTML = `
      Bạn chắc chắn muốn 
      <strong
        style="
          color:${actionColor};
        "
      >
        ${actionText}
      </strong>

      tài khoản

      <strong
        style="
          color:#000;
        "
      >
        ${user.username}
      </strong>

      này?
    `;

    document.getElementById(
        "confirmModal"
    ).style.display = "flex";
}

function closeConfirm() {

    selectedUserId = null;

    document.getElementById("confirmModal").style.display =
        "none";
}

function confirmActiveUser() {

    if (selectedUserId !== null) {

        activeUser(selectedUserId);

    }
}

/* MODAL */

function openCreateModal() {

    resetCreateUserForm();

    document.getElementById("createUserModal").style.display =
        "flex";
}

function closeCreateUserModal() {

    document.getElementById("createUserModal").style.display =
        "none";

    resetCreateUserForm();
}

function openRoleModal(userId) {

    selectedRoleUserId = userId;

    const user =
        AdminState.allUsers.find(
            u => u.id === userId
        );

    if (!user) {
        return;
    }

    const container =
        document.getElementById(
            "roleCheckboxList"
        );

    container.innerHTML = "";

    allRoles.filter(role =>

        role.roleName?.toUpperCase() !== "ADMIN")
        
        .forEach(role => {

        const roleName =
            role.roleName?.toUpperCase();

        const checked =
            user.roles?.includes(
                role.roleName
            );

        // Ẩn READER toàn bộ

        const isHiddenRole =

            roleName === "READER"

            ||

            (

                AdminState.currentUser?.username === user.username

                &&

                roleName === "ADMIN"
            );

        container.innerHTML += `

        <label
          class="role-checkbox-item"

          style="
            ${isHiddenRole ? "display:none;" : ""}
          "
        >

          <input
            type="checkbox"

            class="role-checkbox"

            value="${role.roleId}"

            ${checked ? "checked" : ""}

            ${isHiddenRole
                ? "disabled"
                : ""
            }
          >

          ${role.roleName}

        </label>
      `;
    });

    document.getElementById(
        "roleModal"
    ).style.display = "flex";
}

function closeRoleModal() {

    selectedRoleUserId = null;

    document.getElementById(
        "roleModal"
    ).style.display = "none";
}

function openEditModal(userId) {

    selectedEditUserId = userId;

    const user =
        AdminState.allUsers.find(
            u => u.id === userId
        );

    if (!user) {
        return;
    }

    document.getElementById(
        "editEmail"
    ).value = user.email || "";

    document.getElementById(
        "editNickname"
    ).value = user.nickname || "";

    document.getElementById(
        "editGender"
    ).value = user.gender || "OTHER";

    document.getElementById(
        "editUserError"
    ).innerText = "";

    document.getElementById(
        "editUserModal"
    ).style.display = "flex";
}

function closeEditModal() {

    selectedEditUserId = null;

    document.getElementById(
        "editUserModal"
    ).style.display = "none";
}

function openResetPasswordModal(userId) {

    const user =
        AdminState.allUsers.find(
            user => user.id === userId
        );

    if (!user) {
        return;
    }

    selectedResetUserId =
        userId;

    document.getElementById(
        "resetUsername"
    ).innerText =
        user.username || "-";

    document.getElementById(
        "resetEmail"
    ).innerText =
        user.email || "-";

    document.getElementById(
        "resetPasswordModal"
    ).style.display = "flex";
}

function closeResetPasswordModal() {

    selectedResetUserId = null;

    document.getElementById(
        "resetPasswordModal"
    ).style.display = "none";
}

function confirmResetPasswordStep2() {

    document.getElementById(
        "resetPasswordConfirmModal"
    ).style.display = "flex";
}

function closeResetPasswordConfirmModal() {

    document.getElementById(
        "resetPasswordConfirmModal"
    ).style.display = "none";
}

function resetCreateUserForm() {

    const errorEl =
        document.getElementById("createError");

    if (errorEl) {
        errorEl.innerText = "";
    }

    document.getElementById("createUsername").value = "";

    document.getElementById("createPassword").value = "";

    document.getElementById("createEmail").value = "";

    document.getElementById("createNickname").value = "";

    document.getElementById("createGender").value =
        "MALE";
}