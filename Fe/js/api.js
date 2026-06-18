  // js/api.js
  async function checkPermission(permissionName, actionName) {
    const permissions =
      getPermissions();
    return permissions.some(permission => {
      return (
        permission.permissionName === permissionName &&
        permission.actionName === actionName
      );
    });
  }
  function savePermissionsToStorage(permissions) {
    localStorage.setItem(
      "permissions",
      JSON.stringify(permissions)
    );
  }
  function getPermissions() {
    const permissions =
      localStorage.getItem("permissions");
    return permissions
      ? JSON.parse(permissions)
      : [];
  }
  async function loadMyPermissions() {
    const token = getToken();
    if (!token) {
      savePermissionsToStorage([]);
      return [];
    }
    try {
      const res = await fetch(
        `${API_BASE}/v1/api/admin/permissions/me`,
        {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );
      const result = await res.json();
      const permissions =
        result.data || [];
      savePermissionsToStorage(permissions);
      return permissions;
    } catch (err) {
      console.log(err);
      savePermissionsToStorage([]);
      return [];
    }
  }
  async function checkButtonPermissions(permissionName, actionName) {
    const buttons =
      document.querySelectorAll(
        `[data-permission="${actionName}"]`
      );
    const hasPermission =
      await checkPermission(
        permissionName,
        actionName
      );
    buttons.forEach(button => {
      // ADMIN PROTECTED
      if (
        button.classList.contains(
          "admin-protected"
        )
      ) {
        button.classList.add("hidden");
        return;
      }
      if (hasPermission) {
        button.classList.remove(
          "hidden"
        );
      } else {
        button.classList.add(
          "hidden"
        );
      }
    });
  }