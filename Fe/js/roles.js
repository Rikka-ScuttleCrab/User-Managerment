 const {
    selectedRoleId,
    roleUsers,
  } = AdminState;

    let currentUser = null;

    let editingRoleId = null;

    let searchedUsers = [];
    
    let pendingRoleUsers = [];

    let searchTimeout;

    async function checkRolesPage() {
      return await checkPagePermission("roles");
    }

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

    allRoles = await loadRolesData();

    const table =
      document.getElementById("roleTable");

    table.innerHTML = "";

    allRoles.forEach((role) => {

      const isSystemRole =

        role.roleName?.toUpperCase() === "ADMIN"

          ||

        role.roleName?.toUpperCase() === "READER";

        table.innerHTML += `
          <tr>

            <td>
              ${role.roleId}
            </td>

            <td>
              ${role.roleName}
            </td>

            <td>

              <button
                data-permission="UPDATE"

                class="action-btn edit-btn"

                onclick="openEditRoleModal(${role.roleId})"

                  ${isSystemRole ? "disabled" : ""}

                  style="
                    ${isSystemRole ? "display:none;" : ""}">
                  Sửa
              </button>

              <button
                data-permission="DELETE"

                class="action-btn delete-btn"

                onclick="openDeleteRoleModal(${role.roleId})"

                ${isSystemRole ? "disabled" : ""}

                style="
                  ${isSystemRole ? "display:none;" : ""}">
                Xoá
              </button>

              <button
                data-permission="UPDATE"

                class="action-btn member-btn"

                onclick="openRoleUsersModal(${role.roleId})">
                Thành viên
              </button>

                  </td>

              </tr>
              `;
    });

    } catch (err) {

      console.log(err);

    }
    await checkButtonPermissions("roles", "UPDATE");
    await checkButtonPermissions("roles", "DELETE");
  }

  async function createRole() {

    const roleName =
        document.getElementById("roleName").value.trim();

    const description =
        document.getElementById("roleDescription").value.trim();

    if (!roleName) {

      showToast("Vui lòng nhập tên nhóm", "warning");

      return;
    }

    try {

      const res = await fetch(
        `${API_BASE}/v1/api/admin/roles/create`,
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },

          body: JSON.stringify({
            roleName,
            description
          })
        }
      );

      const result = await res.json();

        if (!res.ok) {

          throw new Error(
            result.message || "Tạo nhóm thất bại"
          );
        }

        showToast("Tạo nhóm thành công", "success");

        closeRoleModal();

        await loadRoles();

      } catch (err) {

        console.log(err);

        showToast(err.message, "error");
      }
  }

    async function loadAllUsers() {

      try {

        allUsers = await loadUsersData();

      } catch (err) {

        console.log(err);
      }
    }

    async function loadRoleUsers(roleId) {

        const role =
            allRoles.find(
            role => role.roleId === roleId
            );

        if (!role) {
            return;
        }

        AdminState.roleUsers = allUsers.filter(user => {

            return user.roles?.includes(
            role.roleName
            );
        });

        // clone ra danh sách tạm
        pendingRoleUsers = [...AdminState.roleUsers];

        renderRoleUsers();
    }

/* MODAL */

function openAddRoleModal() {

  editingRoleId = null;

  document.getElementById(
    "roleModalTitle"
  ).innerText = "Thêm nhóm";

  document.getElementById(
    "roleName"
  ).value = "";

  document.getElementById(
    "roleDescription"
  ).value = "";

  document.getElementById(
    "saveRoleBtn"
  ).onclick = createRole;

  document.getElementById(
    "roleModal"
  ).style.display = "flex";
}

function closeRoleModal() {

  editingRoleId = null;

  document.getElementById(
    "roleModal"
  ).style.display = "none";
}

async function openRoleUsersModal(roleId) {

  const role =
    allRoles.find(
      role => role.roleId === roleId
    );

  if (!role) {
    return;
  }

  AdminState.selectedRoleId = roleId;

  document.getElementById(
    "roleUsersTitle"
  ).innerText =
    `Thành viên role: ${role.roleName}`;

  document.getElementById(
    "roleUsersModal"
  ).style.display = "flex";

  loadRoleUsers(roleId);
}

function renderRoleUsers() {

  const container =
    document.getElementById(
      "roleUsersList"
    );

  container.innerHTML = "";

  const role =
    allRoles.find(
      role => role.roleId === AdminState.selectedRoleId
    );

  if (!role) {
    return;
  }

  const isReaderRole =
    role.roleName?.toUpperCase() === "READER";

  const isAdminRole =
    role.roleName?.toUpperCase() === "ADMIN";
  pendingRoleUsers.forEach(user => {

    const isCurrentAdminSelf =

      currentUser?.username === user.username &&

      role.roleName?.toUpperCase() === "ADMIN";


    const shouldHideRemove =

      isReaderRole ||

      isAdminRole ||
      
      isCurrentAdminSelf;

    container.innerHTML += `

      <tr>

        <td>
          ${user.id}
        </td>

        <td>
          ${user.username}
        </td>

        <td>
          ${user.email}
        </td>

        <td>

          <button
            class="action-btn delete-btn
              ${shouldHideRemove ? "hidden" : ""}
            "

            ${shouldHideRemove ? "disabled" : ""}

            onclick="removeUserFromPending(${user.id})"
          >
            Loại bỏ
          </button>

        </td>

      </tr>
    `;
  });
}

function closeRoleUsersModal() {

  AdminState.selectedRoleId = null;

  document.getElementById(
    "roleUsersModal"
  ).style.display = "none";
}

function openDeleteRoleModal(roleId) {

    const role =
        allRoles.find(
            role => role.roleId === roleId
        );

    if (!role) {
        return;
    }

    const confirmDelete = confirm(
        `Chắc chắn muốn xoá role "${role.roleName}" chứ?`
    );

    if (!confirmDelete) {
        return;
    }

    deleteRole(roleId);
}

function openEditRoleModal(roleId) {

    const role =
        allRoles.find(
            role => role.roleId === roleId
        );

    if (!role) {
        return;
    }

    editingRoleId = roleId;

    document.getElementById(
        "roleModalTitle"
    ).innerText = "Chỉnh sửa nhóm";

    document.getElementById(
        "roleName"
    ).value = role.roleName || "";

    document.getElementById(
        "roleDescription"
    ).value = role.description || "";

    document.getElementById(
        "saveRoleBtn"
    ).onclick = updateRole;

    document.getElementById(
        "roleModal"
    ).style.display = "flex";
}

function searchUsersToAdd(keyword) {

  clearTimeout(searchTimeout);

  searchTimeout = setTimeout(() => {

    const container =
      document.getElementById("searchUsersResult");

    const value = keyword.trim();

    container.innerHTML = "";

    if (!value) {
      return;
    }

    const isOnlyNumber =
      /^\d+$/.test(value);

    if (
      !isOnlyNumber &&
      value.length < 3
    ) {

      container.innerHTML = `
        <div class="search-empty">
          Nhập ít nhất 3 ký tự để tìm kiếm
        </div>
      `;

      return;
    }

    const role =
      allRoles.find(
        role => role.roleId === AdminState.selectedRoleId
      );

    const lowerKeyword =
      value.toLowerCase();

    const users = allUsers.filter(user => {

      const alreadyInRole =
        user.roles?.includes(role.roleName);

      if (alreadyInRole) {
        return false;
      }

      if (isOnlyNumber) {

        return String(user.id)
          .includes(value);
      }

      return (

        user.username
          ?.toLowerCase()
          .includes(lowerKeyword)

        ||

        user.email
          ?.toLowerCase()
          .includes(lowerKeyword)

        ||

        user.nickname
          ?.toLowerCase()
          .includes(lowerKeyword)

      );

    });

    users.forEach(user => {

      container.innerHTML += `
        <div
          class="search-user-item"
          onclick="selectUserToRole(${user.id})">

          <div class="search-user-id">
            #${user.id}
          </div>

          <div class="search-user-username">
            ${user.username}
          </div>

          <div class="search-user-email">
            ${user.email}
          </div>

        </div>
      `;
    });

  }, 300);
}

function selectUserToRole(userId) {

  const user =
    allUsers.find(
      user => user.id === userId
    );

  if (!user) {
    return;
  }

  const exists =
    pendingRoleUsers.some(
      item => item.id === userId
    );

  if (exists) {
    return;
  }

  pendingRoleUsers.push(user);

  renderRoleUsers();

  document.getElementById(
    "searchUsersResult"
  ).innerHTML = "";

  document.querySelector(
    ".role-search-box input"
  ).value = "";
}

async function updateRole() {

    if (!editingRoleId) {
        return;
    }

    const roleName =
        document.getElementById("roleName").value.trim();

    const description =
        document.getElementById("roleDescription").value.trim();

    if (!roleName) {

        showToast("Vui lòng nhập tên nhóm", "warning");

        return;
    }

    try {

        const res = await fetch(
            `${API_BASE}/v1/api/admin/roles/${editingRoleId}`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },

                body: JSON.stringify({
                    roleName,
                    description
                })
            }
        );

        const result = await res.json();

        if (!res.ok) {

            throw new Error(
                result.message || "Cập nhật thất bại"
            );
        }

        showToast("Cập nhật nhóm thành công", "success");

        closeRoleModal();

        await loadRoles();

    } catch (err) {

        console.log(err);

        showToast(err.message, "error");
    }
}

async function saveRoleUsers() {

  try {

    const userIds =
      pendingRoleUsers.map(
        user => user.id
      );

    const role =
      allRoles.find(
        role => role.roleId === AdminState.selectedRoleId
      );

    // chống tự remove ADMIN

    if (
      role.roleName?.toUpperCase() === "ADMIN"
    ) {

      const myUser =
        allUsers.find(
          user =>
            user.username ===
            currentUser.username
        );

      if (
        myUser &&
        !userIds.includes(myUser.id)
      ) {

        showToast(
          "Bạn không thể tự xoá khỏi ADMIN", "warning"
        );

        return;
      }
    }

    const res = await fetch(
      `${API_BASE}/v1/api/admin/roles/add-users/${AdminState.selectedRoleId}`,
      {
        method: "PUT",

        headers: {
          "Content-Type":
            "application/json",

          "Authorization":
            `Bearer ${token}`
        },

        body: JSON.stringify({
          userIds
        })
      }
    );

    if (!res.ok) {

      throw new Error(
        "Cập nhật thành viên thất bại"
      );
    }

    showToast(
      "Cập nhật thành viên thành công", "success"
    );

    closeRoleUsersModal();

    await loadAllUsers();

    await loadRoles();

  } catch (err) {

    console.log(err);

    showToast(err.message, "error");
  }
}

async function deleteRole(roleId) {

    try {

        const res = await fetch(
            `${API_BASE}/v1/api/admin/roles/${roleId}`,
            {
                method: "DELETE",

                headers: {
                    "Authorization": `Bearer ${token}`
                }
            }
        );

        const result = await res.json();

        if (!res.ok) {

            throw new Error(
                result.message || "Xoá role thất bại"
            );
        }

        showToast("Xoá role thành công", "success");

        await loadRoles();

    } catch (err) {

        console.log(err);

        showToast(err.message, "error");
    }
}

function removeUserFromRole(userId) {

  AdminState.roleUsers =
    AdminState.roleUsers.filter(
      user => user.id !== userId
    );

  renderRoleUsers();
}

function removeUserFromPending(userId) {

  const role =
    allRoles.find(
      role => role.roleId === AdminState.selectedRoleId
    );

  const user =
    pendingRoleUsers.find(
      user => user.id === userId
    );

  if (!role || !user) {
    return;
  }

  // chặn tự remove ADMIN

  if (

    role.roleName?.toUpperCase() === "ADMIN"

    &&

    currentUser?.username === user.username

  ) {

    showToast(
      "Bạn không thể tự xoá khỏi ADMIN", "warning"
    );

    return;
  }

  pendingRoleUsers =
    pendingRoleUsers.filter(
      user => user.id !== userId
    );

  renderRoleUsers();
}

function addUserToRole(userId) {

  const user =
    allUsers.find(
      user => user.id === userId
    );

  if (!user) {
    return;
  }

  const exists =
    AdminState.roleUsers.some(
      roleUser =>
        roleUser.id === user.id
    );

  if (exists) {
    return;
  }

  AdminState.roleUsers.push(user);

  renderRoleUsers();

  document.getElementById(
    "searchUsersResult"
  ).innerHTML = "";
}

window.addEventListener("load", async () => {
    currentUser = getUser();
    const allowed = await checkRolesPage();

    if (!allowed) {
      return;
    }
    await checkButtonPermissions("roles", "CREATE");
    await loadAllUsers();
    await loadRoles();
    
});