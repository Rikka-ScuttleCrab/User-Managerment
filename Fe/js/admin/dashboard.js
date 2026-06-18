async function checkDashboardPage() {
  return await checkPagePermission("admin");
}
async function loadDashboard() {
  try {
    // USERS
    const usersRes =
      await fetch(
        `${API_BASE}/v1/api/admin/users?page=1`,
        {
          headers: {
            Authorization:
              `Bearer ${token}`
          }
        }
      );
    const usersResult =
      await usersRes.json();
    document.getElementById(
      "totalUsers"
    ).innerText =
      usersResult.data?.totalItems || 0;
    // ROLES
    const rolesRes =
      await fetch(
        `${API_BASE}/v1/api/admin/roles`,
        {
          headers: {
            Authorization:
              `Bearer ${token}`
          }
        }
      );
    const rolesResult =
      await rolesRes.json();
    const roles =
      rolesResult.data || [];
    document.getElementById(
      "totalRoles"
    ).innerText =
      roles.length;
    // MOCK DATA
    document.getElementById(
      "approvedNews"
    ).innerText =
      Math.floor(
        Math.random() * 30
      ) + 5;
    document.getElementById(
      "todayVisits"
    ).innerText =
      Math.floor(
        Math.random() * 5000
      ) + 1000;
  } catch (err) {
    console.error(
      "Load dashboard error:",
      err
    );
    showToast(
      "Không thể tải dữ liệu dashboard",
      "error"
    );
  }
}
window.addEventListener("load", async () => {
  const allowed =
    await checkDashboardPage();
    if (!allowed) {
      return;
    }
  await loadDashboard();
});