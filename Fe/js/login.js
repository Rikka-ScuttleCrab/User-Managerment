  async function login() {
    const login =
      document.getElementById("loginUsername").value;
    const password =
      document.getElementById("loginPassword").value;
    if(!login || !password) {
      document.getElementById("login-error").innerText = "Vui lòng nhập đầy đủ thông tin";
      return;
    }
    try {
      const res = await fetch(`${API_BASE}/v1/api/auth/sign-in`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          login,
          password
        })
      });
      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.message || "Login failed");
      }
      saveToken(data.data?.access_token);
      const profileRes = await fetch(
        `${API_BASE}/v1/api/me`,
        {
          headers: {
            "Authorization":
              `Bearer ${data.data?.access_token}`
          }
        }
      );
      const profileResult =
        await profileRes.json();
      const user = profileResult.data;
      // convert role cũ
      if (user.roles?.length > 0) {
        user.role =
          user.roles[0].roleName;
      }
      saveUser(user);
      await loadMyPermissions();
      // SAVE PERMISSIONS
      const permissions = [];
      user.roles?.forEach(role => {
        role.permissions?.forEach(permission => {
          permissions.push({
            permissionName:
              permission.permissionName,
            actionName:
              permission.actionName
          });
        });
      });
      localStorage.setItem(
        "myPermissions",
        JSON.stringify(permissions)
      );
      console.log(getPermissions());
      window.location.href =
        "/Fe/";
    } catch (err) {
      if(err.message === "Failed to fetch") {
        document.getElementById("login-error").innerText = "Không thể kết nối đến server";
        return;
      }
      else if(err.message === "User not found") {
        document.getElementById("login-error").innerText = "Tài khoản không tồn tại";
      }
      else if(err.message === "Invalid password") {
        document.getElementById("login-error").innerText = "Mật khẩu không đúng";
        document.getElementById("loginPassword").value = "";
      } else if(err.message === "Your account has been locked. Please contact the administrator for more information.") {
        document.getElementById("login-error").innerText = "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ với quản trị viên để biết thêm thông tin.";
      } else {
        document.getElementById("login-error").innerText = err.message;
      }
      setTimeout(() => {
        document.getElementById("login-error").innerText = "";
      }, 3000);
    }
  }
  function showRegister() {
      document.getElementById("loginForm").style.display = 
        "none";
      document.getElementById("registerForm").style.display = 
        "block";
      document.getElementById("forgotForm").style.display =
        "none";
  }
  function showLogin() {
    document.getElementById("registerForm").style.display =
      "none";
    document.getElementById("forgotForm").style.display =
      "none";
    document.getElementById("loginForm").style.display =
      "block";
    window.location.href = "/Fe/login/";
  }
  function showForgotPassword() {
    document.getElementById("loginForm").style.display =
      "none";
    document.getElementById("registerForm").style.display =
      "none";
    document.getElementById("forgotForm").style.display =
      "block";
    document.getElementById("step-email").style.display =
      "block";
    document.getElementById("step-otp").style.display =
      "none";
    document.getElementById("step-password").style.display =
      "none";
    document.getElementById("forgot-step-title").innerText =
      "Nhập email để nhận OTP";
  }
  document.addEventListener("DOMContentLoaded", function () {
      const params = new URLSearchParams(window.location.search);
      if (params.get("register") === "true") {
          showRegister();
      }
  });
