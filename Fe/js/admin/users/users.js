  /* PAGE PERMISSION */
  async function checkUsersPage() {
    return await checkPagePermission("users");
  }
  /* LOAD USERS */
  async function loadUsers() {
    try {
      const data =
        await loadUsersData(
          AdminState.currentPage
        );
      AdminState.allUsers =
        data.users || [];
      AdminState.totalPages =
        data.totalPages || 1;
      AdminState.totalItems =
        data.totalItems || 0;
      renderUsers();
    } catch (err) {
      console.error(
        "Load users error",
        err
      );
      showToast(
        err.message,
        "error"
      );
    }
  }
  /* LOAD ROLES */
  async function loadAllRoles() {
    try {
      allRoles = await loadRolesData();
    } catch (err) {
      console.log(err);
    }
  }
  async function loadRoleFilter() {
      const roles =
        allRoles || [];
      const roleFilter =
        document.getElementById(
          "roleFilter"
        );
      if (!roleFilter) {
        return;
      }
      roleFilter.innerHTML = `
        <option value="">
          Tất cả Role
        </option>
      `;
      roles.forEach(role => {
        roleFilter.innerHTML += `
          <option value="${role.roleName}">
            ${role.roleName}
          </option>
        `;
      });
  }
  /* FILTER + SORT */
  function toggleDropdown(event, userId) {

    event.stopPropagation();

    const menu =
      document.getElementById(
        `dropdown-${userId}`
      );

    document
      .querySelectorAll(".dropdown-menu")
      .forEach(item => {

        if (item !== menu) {

          item.classList.remove("show");
        }
      });

    menu.classList.toggle("show");
  }
  /* CLICK DROPDOWN */
  window.addEventListener("click", () => {
    document
      .querySelectorAll(".dropdown-menu")
      .forEach(menu => {
        menu.classList.remove("show");
      });
  });
  /* INIT */
  window.addEventListener("load", async () => {
    AdminState.currentUser = getUser();
    const allowed =
      await checkUsersPage();
    if (!allowed) {
      return;
    }
    else {
      await checkButtonPermissions("users", "CREATE");
      await loadUsers();
      await loadAllRoles();
      loadRoleFilter();
    }
  });