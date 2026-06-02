
function goLogin() {
    window.location.href = "/Fe/login/";
}

function goRegister() {
    window.location.href = "/Fe/login/?register=true";
}

function goAccountCenter() {
  window.location.href = "/Fe/account-center/";
}

function goHome() {
  window.location.href = "/Fe/";
}

function toggleProfileMenu() {

    const menu = document.getElementById("profile-menu");

    menu.style.display =
        menu.style.display === "block"
        ? "none"
        : "block";
}

document.addEventListener(
  "DOMContentLoaded",
  () => {

    const error =
      sessionStorage.getItem(
        "permission_error"
      );

    if (error) {

      showToast(
        error,
        "error"
      );

      sessionStorage.removeItem(
        "permission_error"
      );
    }
  }
);