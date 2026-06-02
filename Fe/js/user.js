

  async function signUp() {

    const username =
      document.getElementById("username").value;

    const email =
      document.getElementById("email").value;

    const gender =
      document.getElementById("gender").value;

    const password =
      document.getElementById("password").value;

    const confirmPassword =
      document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {

      document.getElementById("register-error").innerText =
        "Mật khẩu xác nhận không khớp";

      return;
    }

    try {

      const res = await fetch(`${API_BASE}/v1/api/auth/sign-up`, {

        method: "POST",

        headers: {
          "Content-Type": "application/json"
        },

        body: JSON.stringify({
          username,
          email,
          gender,
          password
        })

      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.detail || "Đăng ký thất bại");
      }

      alert("Đăng ký thành công");

      showLogin();

    } catch (err) {

      document.getElementById("register-error").innerText =
        err.message;
    }

  }

  async function loadProfile() {

    const user = getUser();

    if (!user) {
      return;
    }

    document.getElementById("guest-menu").style.display =
      "none";

    document.getElementById("user-menu").style.display =
      "block";

    const nicknameEl =
      document.getElementById("nickname");

    if (nicknameEl) {

      nicknameEl.innerText =
        user.nickname || user.username;
    }

    const usernameText =
      document.getElementById("usernameText");

    if (usernameText) {

      usernameText.innerText =
        user.username;
    }
  }

  async function updateProfile() {

      const email =
          document.getElementById("profileEmail").value;

      const nickname =
          document.getElementById("profileNickname").value;

      const gender =
          document.getElementById("profileGender").value;

      if (!token) {
          return;
      }

      try {

          const res = await fetch(`${API_BASE}/v1/api/me`, {

          method: "PUT",

          headers: {
              "Content-Type": "application/json",
              "Authorization": `Bearer ${token}`
          },

          body: JSON.stringify({
              email,
              nickname,
              gender
          })

          });

          const data = await res.json();

          if (!res.ok) {
          throw new Error(data.detail || "Edit failed");
          }

          alert("Cập nhật thành công");
          const user = getUser();

          user.email = email;
          user.nickname = nickname;
          user.gender = gender;

          saveUser(user);
          
          loadProfile();

          closeProfile();

      } catch (err) {

          alert(err.message);

      }

  }

  async function changedPassword() {

      const oldPassword =
          document.getElementById("oldPassword").value;

      const newPassword =
          document.getElementById("newPassword").value;

      const confirmPassword =
          document.getElementById("confirmNewPassword").value;

      const messageBox =
          document.getElementById("password-message");

      messageBox.innerText = "";
      messageBox.className = "password-message";

      if (!token) {
          return;
      }

      if (newPassword !== confirmPassword) {

          messageBox.innerText =
              "Xác nhận mật khẩu không khớp";

          messageBox.classList.add("error");

          return;
      }

      try {

          const res = await fetch(`${API_BASE}/v1/api/me/password`, {

              method: "PUT",

              headers: {
                  "Content-Type": "application/json",
                  "Authorization": `Bearer ${token}`
              },

              body: JSON.stringify({
                  oldPassword,
                  newPassword,
                  confirmPassword
              })

          });

          const result = await res.json();

          if (!res.ok) {
              throw new Error(result?.error || "Đổi mật khẩu thất bại");
          }

          // 👇 lấy message đúng từ API
          const successMessage = result?.data?.message || "Success";

          messageBox.innerText = successMessage;
          messageBox.classList.add("success");

          document.getElementById("oldPassword").value = "";
          document.getElementById("newPassword").value = "";
          document.getElementById("confirmNewPassword").value = "";

      } catch (err) {

          messageBox.innerText = err.message;

          messageBox.classList.add("error");

      }

  }

  async function loadProfilePage() {

    const data = getUser();

    if (!data) {
      return;
    }

    const nicknameInput =
      document.getElementById(
        "profileNickname"
      );

    if (nicknameInput) {
      nicknameInput.value =
        data.nickname || "";
    }

    const usernameInput =
      document.getElementById(
        "profileUsername"
      );

    if (usernameInput) {
      usernameInput.value =
        data.username || "";
    }

    const emailInput =
      document.getElementById(
        "profileEmail"
      );

    if (emailInput) {
      emailInput.value =
        data.email || "";
    }

    const genderInput =
      document.getElementById(
        "profileGender"
      );

    if (genderInput) {
      genderInput.value =
        data.gender || "OTHER";
    }
  }

  window.addEventListener("load", () => {

    loadProfile();

    loadProfilePage();

  });