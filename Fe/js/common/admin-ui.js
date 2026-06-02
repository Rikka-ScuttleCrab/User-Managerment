// js/common/admin-ui.js

function showToast(message, type = "success") {

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
  }, 2000);
}

function openModal(id) {

  const modal = document.getElementById(id);

  if (modal) {
    modal.style.display = "flex";
  }
}

function closeModal(id) {

  const modal = document.getElementById(id);

  if (modal) {
    modal.style.display = "none";
  }
}

function clearInput(id, value = "") {

  const input = document.getElementById(id);

  if (input) {
    input.value = value;
  }
}

function setText(id, value = "") {

  const el = document.getElementById(id);

  if (el) {
    el.innerText = value;
  }
}