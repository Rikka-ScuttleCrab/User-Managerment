let canViewUsers;
let canViewRoles;
let canViewPermissions;
async function loadSidebar(activePage = "") {
  const res = await fetch(
    "/Fe/components/sidebar.html"
  );
  const html = await res.text();
  document.getElementById(
    "sidebar-container"
  ).innerHTML = html;
  updateLogoName();
  // APPLY PERMISSION
  // SAU KHI SIDEBAR ĐÃ RENDER
  await applySidebarPermissions();
  // ACTIVE MENU
  const items =
    document.querySelectorAll(
      ".menu-item"
    );
  items.forEach(item => {
    item.classList.remove("active");
  });
  switch (activePage) {
    case "dashboard":
      items[0]?.classList.add("active");
      break;
    case "users":
      items[1]?.classList.add("active");
      break;
    case "roles":
      items[2]?.classList.add("active");
      break;
    case "permissions":
      items[3]?.classList.add("active");
      break;
  }
}
async function applySidebarPermissions() {
  const menuUsers =
    document.getElementById(
      "menuUsers"
    );
  const menuRoles =
    document.getElementById(
      "menuRoles"
    );
  const menuPermissions =
    document.getElementById(
      "menuPermissions"
    );
  // USERS
  canViewUsers =
    await checkPermission(
      "users",
      "VIEW"
    );
  menuUsers?.classList.toggle(
    "hidden",
    !canViewUsers
  );
  // ROLES
  canViewRoles =
    await checkPermission(
      "roles",
      "VIEW"
    );
  menuRoles?.classList.toggle(
    "hidden",
    !canViewRoles
  );
  // PERMISSIONS
  canViewPermissions =
    await checkPermission(
      "permissions",
      "VIEW"
    );
  menuPermissions?.classList.toggle(
    "hidden",
    !canViewPermissions
  );
  // TOGGLE BUTTON
  const toggleBtn =
    document.getElementById(
      "toggleSidebarBtn"
    );
  const noMenuVisible =
    !canViewUsers &&
    !canViewRoles &&
    !canViewPermissions;
  toggleBtn?.classList.toggle(
    "hidden",
    noMenuVisible
  );
}
function toggleSidebar() {
  const sidebar =
    document.querySelector(
      ".sidebar"
    );
  const mainContent =
    document.querySelector(
      ".main-content"
    );
  const topbar =
    document.querySelector(
      ".topbar"
    );
  sidebar.classList.toggle(
    "collapsed"
  );
  mainContent?.classList.toggle(
    "expanded"
  );
  topbar?.classList.toggle(
    "expanded"
  );
  // SAVE STATE
  const isCollapsed =
    sidebar.classList.contains(
      "collapsed"
    );
  localStorage.setItem(
    "sidebarCollapsed",
    isCollapsed
  );
}
function restoreSidebarState() {
  const isCollapsed =
    localStorage.getItem(
      "sidebarCollapsed"
    ) === "true";
  if (!isCollapsed) {
    return;
  }
  requestAnimationFrame(() => {
    const sidebar =
      document.querySelector(
        ".sidebar"
      );
    const mainContent =
      document.querySelector(
        ".main-content"
      );
    const topbar =
      document.querySelector(
        ".topbar"
      );
    sidebar?.classList.add(
      "collapsed"
    );
    mainContent?.classList.add(
      "expanded"
    );
    topbar?.classList.add(
      "expanded"
    );
  });
}
const toggleBtn =
  document.getElementById(
    "toggleSidebarBtn"
  );
const hasAnyMenu =
  canViewUsers
  ||
  canViewRoles
  ||
  canViewPermissions;
if (hasAnyMenu) {
  toggleBtn?.classList.remove(
    "hidden"
  );
} else {
  toggleBtn?.classList.add(
    "hidden"
  );
}
const sidebarTitle =
  document.querySelector(
    ".sidebar-title"
  );
if (!hasAnyMenu) {
  sidebarTitle?.classList.add(
    "hidden"
  );
} else {
  sidebarTitle?.classList.remove(
    "hidden"
  );
}
/* =========================
   NAVIGATION
========================= */
function goAdminDashboard() {
  window.location.href =
    "/Fe/admin/";
}
function goUsers() {
  window.location.href =
    "/Fe/admin/users/";
}
function goRoles() {
  window.location.href =
    "/Fe/admin/roles/";
}
function goPermissions() {
  window.location.href =
    "/Fe/admin/permissions/";
}