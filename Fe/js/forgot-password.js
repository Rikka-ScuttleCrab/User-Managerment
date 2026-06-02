  async function sendOtp() {

    const email =
      document.getElementById("forgotEmail").value;

    const btn =
      document.getElementById("sendOtpBtn");

    try {

      // loading state

      btn.disabled = true;

      btn.innerText = "Đang gửi OTP...";

      const res = await fetch(
        `${API_BASE}/v1/api/auth/forgot-password/send-otp`,
        {

          method: "POST",

          headers: {
            "Content-Type": "application/json"
          },

          body: JSON.stringify({
            email
          })

        }
      );

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.message || "Gửi OTP thất bại");
      }

      btn.innerText = "OTP đã gửi ✓";

      document.getElementById("step-email").style.display =
        "none";

      document.getElementById("step-otp").style.display =
        "block";

      document.getElementById("forgot-step-title").innerText =
        "Nhập mã OTP";

    } catch (err) {

      btn.disabled = false;

      btn.innerText = "Gửi OTP";

      document.getElementById("forgot-error").innerText =
        err.message;

    }

  }

  async function verifyOtp() {

    const email =
      document.getElementById("forgotEmail").value;

    const otp =
      document.getElementById("otp").value;

    try {

      const res = await fetch(
        `${API_BASE}/v1/api/auth/forgot-password/verify-otp`,
        {

          method: "POST",

          headers: {
            "Content-Type": "application/json"
          },

          body: JSON.stringify({
            email,
            otp
          })

        }
      );

      if (!res.ok) {
        throw new Error("OTP không hợp lệ");
      }

      document.getElementById("step-otp").style.display =
        "none";

      document.getElementById("step-password").style.display =
        "block";

      document.getElementById("forgot-step-title").innerText =
        "Nhập mật khẩu mới";

    } catch (err) {

      document.getElementById("forgot-error").innerText =
        err.message;

    }

  }

  async function resetPassword() {

    const email =
      document.getElementById("forgotEmail").value;

    const newPassword =
      document.getElementById("newPassword").value;

    const confirmPassword =
      document.getElementById("confirmNewPassword").value;

    if (newPassword !== confirmPassword) {

      document.getElementById("forgot-error").innerText =
        "Mật khẩu xác nhận không khớp";

      return;
    }

    try {

      const res = await fetch(
        `${API_BASE}/v1/api/auth/reset-password`,
        {

          method: "POST",

          headers: {
            "Content-Type": "application/json"
          },

          body: JSON.stringify({
            email,
            newPassword
          })

        }
      );

      const text = await res.text();

      if (!res.ok) {
        throw new Error(text || "Đổi mật khẩu thất bại");
      }

      alert("Đổi mật khẩu thành công");

      showLogin();

    } catch (err) {

      document.getElementById("forgot-error").innerText =
        err.message;

    }

  }