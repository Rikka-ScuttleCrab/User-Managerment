
async function checkDashboardPage() {

  const hasDashboardPermission =
    await checkPermission(
      "admin",
      "VIEW"
    );

  if (!hasDashboardPermission) {

    alert("Bạn không có quyền truy cập");

    window.location.href =
      "/Fe/index.html";

    return false;
  }

  return true;
}

async function loadDashboard() {

  try {

    // LOAD USERS

    const usersRes =
      await fetch(
        `${API_BASE}/v1/api/admin/users`,{
            headers: {
                    "Authorization": `Bearer ${token}`
                }
        }
      );

    const usersResult =
      await usersRes.json();

    const users =
      usersResult.data || [];

    document.getElementById(
      "totalUsers"
    ).innerText = users.length;

    // LOAD ROLES

    const rolesRes =
      await fetch(
        `${API_BASE}/v1/api/admin/roles`,{
            headers: {
                    "Authorization": `Bearer ${token}`
            }
        }
      );

    const rolesResult =
      await rolesRes.json();

    const roles =
      rolesResult.data || [];

    document.getElementById(
      "totalRoles"
    ).innerText = roles.length;

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

    console.log(err);

  }
}

function goUsers() {

  window.location.href =
    "/Fe/admin/users/";
}

function goRoles() {

  window.location.href =
    "/Fe/admin/roles/";
}

window.addEventListener(
  "load",
  checkDashboardPage(),
  loadDashboard(),
);