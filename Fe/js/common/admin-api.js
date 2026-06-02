// js/common/admin-api.js

async function apiFetch(url, options = {}) {

  const config = {
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,
      ...(options.headers || {})
    },
    ...options
  };

  const res = await fetch(url, config);

  const result = await res.json();

  if (!res.ok) {
    throw new Error(result.message || "Có lỗi xảy ra");
  }

  return result;
}

async function loadUsersData(page = 1) {

  const result = await apiFetch(
    `${API_BASE}/v1/api/admin/users?page=${page}`
  );

  return result.data;
}

async function loadRolesData() {

  const result = await apiFetch(
    `${API_BASE}/v1/api/admin/roles`
  );

  return result.data || [];
}

async function checkPagePermission(pageName) {

  const hasPermission =
    await checkPermission(
      pageName,
      "VIEW"
    );

  if (!hasPermission) {

    sessionStorage.setItem(
      "permission_error",
      "Bạn không có quyền truy cập trang này"
    );

    window.location.href = "/Fe/";

    return false;
  }

  document.body.style.display = "flex";

  return true;
}