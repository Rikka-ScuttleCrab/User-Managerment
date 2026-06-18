const news_name =
  "User Permission<br>Management";
function updateLogoName() {
  const logos =
    document.querySelectorAll(
      ".logo, .sidebar-logo"
    );
  logos.forEach((logo) => {
    logo.innerHTML = news_name;
  });
}
function startHeartbeat() {
  setInterval(async () => {
    const token = getToken();
    if(!token) return;
    try {
      const response = await fetch(
        `${API_BASE}/v1/api/me`,
        {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );
      if (response.status === 423) {
        localStorage.clear();
        sessionStorage.clear();
       showToast(
          "Tài khoản đã bị vô hiệu hóa", "error", 5000
        );
        setTimeout(() => {
          window.location.href = "/Fe/";
        },5000);
        return;
      }
      if (response.status === 401) {
        localStorage.clear();
        sessionStorage.clear();
       showToast(
          "Gặp lỗi vui lòng đăng nhập lại", "error", 5000
        );
        setTimeout(() => {
          window.location.href = "/Fe/";
        },5000);
        return;
      }
    } catch (error) {
      console.error(
        "Heartbeat error",
        error
      );
    }
  }, 15000);
}
document.addEventListener(
  "DOMContentLoaded",
  () => {
    updateLogoName();
    startHeartbeat();
  }
);
function showToast(message, type = "success", timeout = 1500) {
  const container =
    document.getElementById("toastContainer");
  if (!container) {
    alert(message);
    return;
  }
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <div class="toast-icon">
      ${type === "success" ? "✓" : "⚠"}
    </div>
    <div class="toast-message">
      ${message}
    </div>
  `;
  container.appendChild(toast);
  setTimeout(() => {
    toast.remove();
  }, timeout);
}
function goHome() {
  window.location.href = "/Fe/";
}