// js/admin.js

/* =========================
   DATA
========================= */

let allUsers = [];

// FILTER
let currentRole = "";
let currentGender = "";
let currentStatus = "";
let searchKeyword = "";

// SORT
let isIdDesc = false;

  /* =========================
    PAGE PERMISSION
  ========================= */


  async function checkUsersPage() {

    const hasUsersPagePermission =
      await checkPermission(
        "users",
        "VIEW"
      );

    if (!hasUsersPagePermission) {

      alert("Bạn không có quyền truy cập");

      window.location.replace("/Fe/");

      return false;
    }
    document.body.style.display = "flex";
    return true;
  }

/* =========================
   LOAD USERS
========================= */

  async function loadUsers() {

    try {

      const res = await fetch(
        `${API_BASE}/v1/api/admin/users`,
        {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );

      const result = await res.json();

      allUsers = result.data || [];

      renderUsers();

    } catch (err) {

      console.log(err);
    }
  }

/* =========================
   LOAD ROLES
========================= */

  async function loadRoles() {

    try {

      const res = await fetch(
        `${API_BASE}/v1/api/admin/roles`,
        {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );

      const result = await res.json();

      const roles = result.data || [];

      const roleSelect =
        document.getElementById("createRole");

      if (!roleSelect) {
        return;
      }

      roleSelect.innerHTML = "";

      roles.forEach(role => {

        roleSelect.innerHTML += `
          <option value="${role.roleName}">
            ${role.roleName}
          </option>
        `;
      });

    } catch (err) {

      console.log(err);
    }
  }

/* =========================
   CREATE USER
========================= */

  async function createUser() {

    const username =
      document.getElementById("createUsername").value.trim();

    const password =
      document.getElementById("createPassword").value.trim();

    const email =
      document.getElementById("createEmail").value.trim();

    const nickname =
      document.getElementById("createNickname").value.trim();

    const gender =
      document.getElementById("createGender").value;

    const role =
      document.getElementById("createRole").value;

    const errorEl =
      document.getElementById("createError");

    errorEl.innerText = "";

    // VALIDATE

    if (!username) {

      errorEl.innerText =
        "Vui lòng nhập username";

      return;
    }

    if (!password) {

      errorEl.innerText =
        "Vui lòng nhập mật khẩu";

      return;
    }

    if (!email) {

      errorEl.innerText =
        "Vui lòng nhập email";

      return;
    }

    if (!gender) {

      errorEl.innerText =
        "Vui lòng chọn giới tính";

      return;
    }

    if (!role) {

      errorEl.innerText =
        "Vui lòng chọn role";

      return;
    }

    try {

      const body = {
        username,
        password,
        email,
        gender,
        role
      };

      // nickname optional

      if (nickname) {
        body.nickname = nickname;
      }

      const res = await fetch(
        `${API_BASE}/v1/api/admin/users/create`,
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },

          body: JSON.stringify(body)
        }
      );

      const result = await res.json();

      if (!res.ok) {

        errorEl.innerText =
          result.message ||
          "Tạo người dùng thất bại";

        return;
      }

      closeCreateUserModal();

      await loadUsers();

      alert("Tạo người dùng thành công");

    } catch (err) {

      console.log(err);

      errorEl.innerText =
        "Không thể kết nối server";
    }
  }

/* =========================
   RENDER USERS
========================= */

  function renderUsers() {

    const table =
      document.getElementById("userTable");

    let users = [...allUsers];

    // FILTER ROLE

    if (currentRole) {

      users = users.filter(
        user =>
          user.roles?.toUpperCase() ===
          currentRole.toUpperCase()
      );
    }

    // FILTER GENDER

    if (currentGender) {

      users = users.filter(
        user =>
          user.gender?.toUpperCase() ===
          currentGender.toUpperCase()
      );
    }

    // FILTER STATUS

    if (currentStatus) {

      users = users.filter(user => {

        if (currentStatus === "ACTIVE") {
          return user.active === true;
        }

        if (currentStatus === "INACTIVE") {
          return user.active === false;
        }

        return true;
      });
    }

    // SORT ID

    users.sort((a, b) => {

      return isIdDesc
        ? b.id - a.id
        : a.id - b.id;
    });

    // SEARCH

    if (searchKeyword) {

      const keyword =
        searchKeyword.toLowerCase();

      users = users.filter(user => {

        return (

          String(user.id)
            .toLowerCase()
            .includes(keyword)

          ||

          user.username
            ?.toLowerCase()
            .includes(keyword)

          ||

          user.nickname
            ?.toLowerCase()
            .includes(keyword)

          ||

          user.email
            ?.toLowerCase()
            .includes(keyword)

        );

      });
    }

    table.innerHTML = "";

    users.forEach((user) => {

      table.innerHTML += `
        <tr>

          <td>${user.id}</td>

          <td>${user.username}</td>

          <td>${user.email}</td>

          <td>${user.nickname || ""}</td>

          <td>${user.role}</td>

          <td>${user.gender}</td>

          <td>
            <span class="status ${user.active ? 'active' : 'inactive'}">
              ${user.active ? 'Hoạt động' : 'Bị khóa'}
            </span>
          </td>

          <td>

            <button class="action-btn edit-btn">
              Sửa
            </button>

            <button
              class="action-btn delete-btn"
              onclick="openConfirm(${user.id})"
            >
              ${user.active ? 'Vô hiệu hoá' : 'Kích hoạt'}
            </button>

          </td>

        </tr>
      `;
    });
  }

/* =========================
   SEARCH
========================= */

function searchUsers(value) {

  searchKeyword = value;

  renderUsers();
}


async function activeUser(userId) {

  try {

    const res = await fetch(
      `${API_BASE}/v1/api/admin/users/${userId}/active`,
      {
        method: "PUT",

        headers: {
          "Authorization": `Bearer ${token}`
        }
      }
    );

    const result = await res.json();

    if (!res.ok) {

      throw new Error(
        result.message ||
        "Thao tác thất bại"
      );
    }

    closeConfirm();

    // UPDATE LOCAL

    allUsers = allUsers.map(user => {

      if (user.id === userId) {

        return {
          ...user,
          active: !user.active
        };
      }

      return user;
    });

    renderUsers();

  } catch (err) {

    console.log(err);

    alert(err.message);
  }
}

/* =========================
   FILTER + SORT
========================= */

function toggleIdSort() {

  isIdDesc = !isIdDesc;

  renderUsers();
}

function filterRole(value) {

  currentRole = value;

  renderUsers();
}

function filterGender(value) {

  currentGender = value;

  renderUsers();
}

function filterStatus(value) {

  currentStatus = value;

  renderUsers();
}

function resetFilters() {

  currentRole = "";
  currentGender = "";
  currentStatus = "";
  searchKeyword = "";

  renderUsers();
}

/* =========================
   NAVIGATION
========================= */

function goHome() {
  window.location.href = "../index.html";
}

function goUsers() {
  location.reload();
}

function goRoles() {
  window.location.href = "roles.html";
}

function goPermissions() {
  window.location.href = "permissions.html";
}

/* =========================
   CONFIRM MODAL
========================= */

let selectedUserId = null;

function openConfirm(userId) {

  selectedUserId = userId;

  document.getElementById("confirmModal").style.display =
    "flex";
}

function closeConfirm() {

  selectedUserId = null;

  document.getElementById("confirmModal").style.display =
    "none";
}

const confirmBtn =
  document.getElementById("confirmOkBtn");

if (confirmBtn) {

  confirmBtn.addEventListener("click", () => {

    if (selectedUserId) {
      activeUser(selectedUserId);
    }

  });
}

/* =========================
   CREATE MODAL
========================= */

function openCreateModal() {

  resetCreateUserForm();

  loadRoles();

  document.getElementById("createUserModal").style.display =
    "flex";
}

function closeCreateUserModal() {

  document.getElementById("createUserModal").style.display =
    "none";

  resetCreateUserForm();
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

/* =========================
   INIT
========================= */

window.addEventListener("load", async () => {

  const allowed =
    await checkUsersPage();

  if (!allowed) {
    return;
  };

  await loadUsers();
});