if (!token) {
  window.location.href = "/Fe/login/";
}

let allRoles = [];

let myPermissions = [];

let allPermissions = [];

let selectedRole = null;

let pendingPermissions = [];
let groupedPermissions = {};

let selectedModule = null;

function isAdminRole() {

  return (

    selectedRole?.roleName?.toUpperCase()
    === "ADMIN"

  );
}

async function checkPermissionsPage() {
  return await checkPagePermission("permissions");
}

function groupPermissionsByModule() {

  groupedPermissions = {};

  allPermissions.forEach(permission => {

    const module =
      permission.permissionName;

    if (!groupedPermissions[module]) {

      groupedPermissions[module] = [];
    }

    groupedPermissions[module].push(permission);
  });
}

async function loadPermissionsPage() {

  try {

    await loadMyPermissions();

    // LOAD ALL ROLES

    const rolesRes = await fetch(
      `${API_BASE}/v1/api/admin/roles`,
      {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      }
    );

    const rolesResult =
      await rolesRes.json();

    const pureRoles =
      rolesResult.data || [];

    // LOAD ROLE PERMISSIONS

    const permissionsRes = await fetch(
      `${API_BASE}/v1/api/admin/permissions/roles`,
      {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      }
    );

    const permissionsResult =
      await permissionsRes.json();

    const permissionRoles =
      permissionsResult.data || [];

    // MERGE

    allRoles = pureRoles.map(role => {

      const matched =
        permissionRoles.find(
          item =>
            item.roleName?.toUpperCase()
            ===
            role.roleName?.toUpperCase()
        );

      return {

        ...role,

        permissions:
          matched?.permissions || []
      };
    });

    allPermissions = extractPermissionsFromRoles();

    groupPermissionsByModule();

    renderRoles();

    if (allRoles.length > 0) {

      selectRole(
        allRoles[0].roleName
      );
    }

  } catch (err) {

    console.log(err);

    adminlogout();
  }
}

function extractPermissionsFromRoles() {

  const permissionMap = new Map();

  allRoles.forEach(role => {

    role.permissions?.forEach(permission => {

      const action =

        permission.permissionAction
        ||
        permission.actionName;

      const key =
        `${permission.permissionName}-${action}`;

      if (!permissionMap.has(key)) {

        permissionMap.set(key, {

          permissionName:
            permission.permissionName,

          permissionAction:
            action,

          // THÊM DÒNG NÀY
          permissionDescription:
            permission.permissionDescription,

          description:
            permission.description
        });
      }
    });
  });

  return Array.from(
    permissionMap.values()
  );
}

function hasMyPermission(
  permissionName,
  actionName
) {

  return myPermissions.some(p => {

    return (
      p.permissionName === permissionName &&
      p.actionName === actionName
    );

  });
}

function renderPermissionList() {

  const container =
    document.getElementById(
      "permissionList"
    );

  container.innerHTML = "";

  const isAdmin =

    selectedRole?.roleName === "Admin"

    ||

    selectedRole?.roleName === "ADMIN";

  allPermissions.forEach(permission => {

    const key =
  `${permission.permissionName}-${permission.permissionAction}`;

    const checked =
      pendingPermissions.includes(key);

    container.innerHTML += `

      <label class="permission-card">

        <input
          type="checkbox"

          class="permission-checkbox"

          ${checked ? "checked" : ""}

          ${isAdmin ? "disabled" : ""}

          onchange="
            togglePendingPermission(
              '${key}'
            )
          "
        >

        <div class="permission-info">

          <div class="permission-name">

            ${permission.permissionName}

            -

            ${permission.permissionAction}

          </div>

          <div class="permission-description">

            ${permission.description || ""}

          </div>

        </div>

      </label>
    `;
  });

  document.getElementById(
    "savePermissionBtn"
  ).style.display =

    isAdmin
      ? "none"
      : "block";
}

function renderRoles() {

  const container =
    document.getElementById("roleList");

  container.innerHTML = "";

  allRoles.forEach(role => {

    const active =
      selectedRole?.roleName === role.roleName;

    container.innerHTML += `

      <div
        class="
          role-item
          ${active ? "active" : ""}
        "

        onclick="
          selectRole('${role.roleName}')
        "
      >

        ${role.roleName}

      </div>
    `;
  });
}

function selectRole(roleName) {

  selectedRole =
    allRoles.find(
      role => role.roleName === roleName
    );

  if (!selectedRole) {
    return;
  }

  pendingPermissions =
    selectedRole.permissions?.map(p =>

      `${p.permissionName}-${p.actionName}`

    ) || [];

  document.getElementById(
    "selectedRoleTitle"
  ).innerText =
    `Nhóm: ${roleName}`;

  renderRoles();

  renderModuleTable();
}

function renderModuleTable() {

  const table =
    document.getElementById(
      "moduleTable"
    );

  table.innerHTML = "";

  const t = getTranslations();

  const isAdmin =

    selectedRole?.roleName === "Admin"

    ||

    selectedRole?.roleName === "ADMIN";

  // =========================
  // LẤY MODULE UNIQUE
  // =========================

  const modules =
    extractModules();

  // =========================
  // RENDER
  // =========================

  modules.forEach(module => {

    const permissions =

      selectedRole.permissions?.filter(p =>

        p.permissionName ===
        module.permissionName
      )

      ||

      [];

    const permissionBadges =

      permissions.length > 0

      ?

      permissions.map(permission => {

        return `

          <span class="
            permission-badge
            ${permission.actionName.toLowerCase()}
          ">
            ${permission.actionName}
          </span>

        `;

      }).join("")

      :

      `
        <span class="no-permission">
          ${t.noPermission}
        </span>
      `;

    table.innerHTML += `

      <tr>

        <td>

          <div class="module-description">

            ${module.permissionDescription}

          </div>

        </td>

        <td>

          <div class="permission-badge-list">

            ${permissionBadges}

          </div>

        </td>

        <td data-permission="UPDATE">

          <button
            data-permission="UPDATE"

            class="
              action-btn
              ${isAdmin
                ? "view-btn"
                : "edit-btn"
              }
            "

            onclick="
              openPermissionModal(
                '${module.permissionName}'
              )
            ">

            ${
              isAdmin
                ? t.viewPermission
                : t.edit
            }

          </button>

        </td>

      </tr>
    `;
  });
  checkButtonPermissions("permissions", "UPDATE");
}

async function savePermissions() {

  try {

    const permissionActions = pendingPermissions
      .filter(item => item.startsWith(`${selectedModule}-`))
      .map(item => item.split("-").pop());

    const body = {

      roleId:
        selectedRole.roleId,

      permissionName:
        selectedModule,

      permissionActions
    };

    const res = await fetch(

      `${API_BASE}/v1/api/admin/permissions/update`,

      {
        method: "PUT",

        headers: {
          "Content-Type":
            "application/json",

          "Authorization":
            `Bearer ${token}`
        },

        body: JSON.stringify(body)
      }
    );

    if (!res.ok) {

      throw new Error(
        "Cập nhật quyền thất bại"
      );
    }

    showToast(
      "Cập nhật quyền thành công", "success"
    );

    closePermissionModal();

    await loadPermissionsPage();

  } catch (err) {

    console.log(err);

    showToast(err.message, "error");
  }
}

function extractModules() {

  const modulesMap = new Map();

  allPermissions.forEach(permission => {

    const moduleName =
      permission.permissionName;

    // nếu module chưa tồn tại
    // thì mới lưu

    if (!modulesMap.has(moduleName)) {

      modulesMap.set(moduleName, {

        permissionName:
          moduleName,

        permissionDescription:
          permission.permissionDescription
      });
    }
  });

  return Array.from(
    modulesMap.values()
  );
}

function togglePendingPermission(key) {

  const exists =
    pendingPermissions.includes(key);

  if (exists) {

    pendingPermissions =
      pendingPermissions.filter(
        item => item !== key
      );

  } else {

    pendingPermissions.push(key);
  }
}

function togglePermission(permissionName, permissionAction) {

  const key =
    `${permissionName}-${permissionAction}`;

  const exists =
    pendingPermissions.includes(key);

  if (exists) {

    pendingPermissions =
      pendingPermissions.filter(
        item => item !== key
      );

  } else {

    pendingPermissions.push(key);
  }
}

function openPermissionModal(module) {

  selectedModule = module;
  const container =
    document.getElementById(
      "permissionCheckboxList"
    );

  container.innerHTML = "";

  document.getElementById(
    "permissionModalTitle"
  ).innerText =
    `Module: ${module}`;

  const permissions =
    groupedPermissions[module];

  const isAdmin =

    selectedRole.roleName
      .toUpperCase() === "ADMIN";

  permissions.forEach(permission => {

    const key =

      `${permission.permissionName}-${permission.permissionAction}`;

    const checked =
      pendingPermissions.includes(key);

    container.innerHTML += `

      <label class="permission-item">

        <input
          type="checkbox"

          ${checked ? "checked" : ""}

          ${isAdminRole() ? "disabled" : ""}

          onchange="
            togglePermission(
              '${permission.permissionName}',
              '${permission.permissionAction}'
            )
          "
        >

        <div>

          <div class="
            permission-badge
            ${permission.permissionAction.toLowerCase()}
          ">

            ${permission.permissionAction}

          </div>

          <div class="permission-item-description">

            ${permission.description || ""}

          </div>

        </div>

      </label>
    `;
  });

  const cancelBtn =
  document.querySelector(
    ".modal-actions .cancel-btn"
  );

const saveBtn =
  document.querySelector(
    ".modal-actions .save-btn"
  );

if (isAdminRole()) {

  cancelBtn.style.display = "none";

  saveBtn.style.display = "none";

} else {

  cancelBtn.style.display = "block";

  saveBtn.style.display = "block";
}

  document.getElementById(
    "permissionModal"
  ).style.display = "flex";
}

function closePermissionModal() {

  document.getElementById(
    "permissionModal"
  ).style.display = "none";
}

window.addEventListener("load", async () => {
  applyTranslations();

  const allowed = await checkPermissionsPage();

  if (!allowed) {
    return;
  }
  
  loadPermissionsPage();
});