async function checkPostNews() {
  return await checkPagePermission("post-news");
}
async function checksubmitPostNews(){
      const hasPostNewsPermission =
    await checkPermission(
      "articles",
      "CREATE"
    );
  const submitPostNewsBtn =
    document.getElementById("submitPostNewsBtn");
  if (!hasPostNewsPermission) {
    if (submitPostNewsBtn) {
      submitPostNewsBtn.style.display = "none";
    }
    return;
  }
  if (submitPostNewsBtn) {
    submitPostNewsBtn.style.display = "block";
  }
}
function submitPostNews() {
  const newsTitle = document.getElementById("newsTitle").value;
  const newsContent = document.getElementById("newsContent").value;
  if (!newsTitle || !newsContent) {
    showToast("Vui lòng nhập đủ cả tiêu đề và nội dung bài viết", "error");
    return;
  }
  showToast("Bài viết đã được gửi đi để duyệt", "success");
  document.getElementById("newsTitle").value ="" ;
  document.getElementById("newsContent").value="" ;
}
function goDashboard() {
      window.location.href = "/Fe/account-center/";
    }
window.addEventListener("load", async () => {
  const allowed =
    await checkPostNews();
    if (!allowed) {
      return;
    }
  document.body.style.display = "block";
  await checksubmitPostNews();
});