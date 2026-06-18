  function goHome() {
    window.location.href = "/Fe/";
  }
  function goProfile() {
    window.location.href = "./profile/";
  }
  function goPostNews() {
    window.location.href = "admin/post-news.html";
  }
  async function loadDashboard() {
    try {
      const res = await fetch(
        `${API_BASE}/v1/api/me`,
        {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );
      if (!res.ok) {
        logout();
        return;
      }
      const result = await res.json();
      const user = result.data;
      const nicknameEl =
        document.getElementById("nickname");
      if (nicknameEl) {
        nicknameEl.innerText =
          user.nickname || user.username;
      }
      const hasDashboardPermission =
        await checkPermission(
          "admin",
          "VIEW"
        );
      const adminCard =
        document.getElementById("adminCard");
      if (adminCard) {
        adminCard.style.display =
          hasDashboardPermission
            ? "block"
            : "none";
      }
    } catch (err) {
      console.log(err);
      logout();
    }
  }
  async function loadApprove() {
    try {
      const res = await fetch(
        `${API_BASE}/v1/api/me`,
        {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );
      if (!res.ok) {
        logout();
        return;
      }
      const result = await res.json();
      const user = result.data;
      const nicknameEl =
        document.getElementById("nickname");
      if (nicknameEl) {
        nicknameEl.innerText =
          user.nickname || user.username;
      }
      const hasDashboardPermission =
        await checkPermission(
          "approve",
          "VIEW"
        );
      const approveCard =
        document.getElementById("approveCard");
      if (approveCard) {
        approveCard.style.display =
          hasDashboardPermission
            ? "block"
            : "none";
      }
    } catch (err) {
      console.log(err);
      logout();
    }
  }
  async function loadPostNews() {
    try {
      const res = await fetch(
        `${API_BASE}/v1/api/me`,
        {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );
      if (!res.ok) {
        logout();
        return;
      }
      const result = await res.json();
      const user = result.data;
      const nicknameEl =
        document.getElementById("nickname");
      if (nicknameEl) {
        nicknameEl.innerText =
          user.nickname || user.username;
      }
      const hasPostNewsPermission =
        await checkPermission(
          "post-news",
          "VIEW"
        );
      const postNewsCard =
        document.getElementById("postNewsCard");
      if (postNewsCard) {
        postNewsCard.style.display =
          hasPostNewsPermission
            ? "block"
            : "none";
      }
    } catch (err) {
      console.log(err);
      logout();
    }
  }
  async function goAdmin() {
    const hasDashboardPermission =
      await checkPermission(
        "admin",
        "VIEW"
      );
    if (!hasDashboardPermission) {
      alert("Bạn không có quyền truy cập");
      return;
    }
    window.location.href =
      "/Fe/admin/";
  }
  async function goPostNews() {
    const hasPostNewsPermission =
      await checkPermission(
        "post-news",
        "VIEW"
      );
    if (!hasPostNewsPermission) {
      alert("Bạn không có quyền truy cập");
      return;
    }
    window.location.href = "/Fe/admin/post-news.html";
  }
  async function goApproveNews() {
    const hasApproveNewsPermission =
      await checkPermission(
        "approve",
        "VIEW"
      );
    if (!hasApproveNewsPermission) {
      alert("Bạn không có quyền truy cập");
      return;
    }
    window.location.href = "/Fe/admin/approve-news.html";
  }
  window.addEventListener("load", () => {
    loadDashboard();
    loadApprove();
    loadPostNews();
  });
