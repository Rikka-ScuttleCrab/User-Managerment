
async function checkPostNews() {

  const hasPostNewsPermission =
    await checkPermission(
      "post-news",
      "VIEW"
    );

  if (!hasPostNewsPermission) {
    alert("Bạn không có quyền truy cập");

    window.location.href =
      "/Fe/";

    return;
  }
  document.body.style.display = "block";
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
  checksubmitPostNews();
}

function goDashboard() {
      window.location.href = "/Fe/account-center/";
    }

window.addEventListener("load", () => {
  checkPostNews();
  checksubmitPostNews();
});