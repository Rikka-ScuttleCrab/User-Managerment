async function loadTopbar(
  breadcrumbText
) {
  const res = await fetch(
    "/Fe/components/topbar.html"
  );
  const html =
    await res.text();
  document.getElementById(
    "topbar-container"
  ).innerHTML = html;
  // breadcrumb
  const breadcrumb =
    document.getElementById(
      "breadcrumbText"
    );
  if (breadcrumb) {
    breadcrumb.textContent =
      breadcrumbText;
  }
  // LOAD ADMIN HERE
  loadAdminProfile();
  restoreSidebarState();
}
function loadAdminProfile() {
  const user = getUser();
  if (!user) {
    adminlogout();
    return null;
  }
  const adminName =
    document.getElementById("adminName");
  if (adminName) {
    adminName.innerText =
      user.nickname || user.username;
  }
  return user;
}
function goDashboard() {
  window.location.href = "/Fe/account-center/";
}