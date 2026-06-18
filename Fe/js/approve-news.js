  // js/approve-news.js
  async function checkApprove() {
    return await checkPagePermission("approve");
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
    showToast("Đã duyệt bài viết thành công", "success");
  }
  function rejectNews() {
    showToast("Đã từ chối bài viết", "error");
  }
  function goDashboard() {
      window.location.href = "/Fe/account-center/";
  }
  window.addEventListener("load", async () => {
    const allowed =
      await checkApprove();
    if (!allowed) {
      return;
    }
    document.body.style.display = "block";
    await checksubmitApproveNews();
  });