  // js/approve-news.js
  async function checkApprove() {

    const hasApproveNewsPermission =
      await checkPermission(
        "approve",
        "VIEW"
      );

    if (!hasApproveNewsPermission) {

      alert("Bạn không có quyền truy cập");

      window.location.replace("/Fe/");

      return;
    }
    document.body.style.display = "block";
  }

  async function checksubmitApproveNews() {

    const hasApproveNewsPermission =
      await checkPermission(
        "articles",
        "APPROVE"
      );

    const approveBtn =
      document.getElementById("approveBtn");

    const rejectBtn =
      document.getElementById("rejectBtn");

    if (!hasApproveNewsPermission) {

      if (approveBtn) {
        approveBtn.style.display = "none";
      }

      if (rejectBtn) {
        rejectBtn.style.display = "none";
      }

      return;
    }

    if (approveBtn) {
      approveBtn.style.display = "block";
    }

    if (rejectBtn) {
      rejectBtn.style.display = "block";
    }
  }

  function approveNews() {
    checksubmitApproveNews();
  }

  function rejectNews() {
    checksubmitApproveNews();
  }

  function goDashboard() {
      window.location.href = "/Fe/account-center/";
  }

  window.addEventListener("load", async () => {

    await checkApprove();

    await checksubmitApproveNews();

  });