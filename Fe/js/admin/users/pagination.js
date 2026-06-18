  function renderPagination(totalPages) {
    const pagination =
      document.getElementById(
        "pagination"
      );
    if (!pagination) {
      return;
    }
    pagination.innerHTML = "";
    // PREV
    pagination.innerHTML += `
      <button
        class="page-btn"
        onclick="changePage(${AdminState.currentPage - 1})"
        ${AdminState.currentPage === 1 ? "disabled" : ""}
      >
        ←
      </button>
    `;
    const visiblePages = [];
    // ALWAYS SHOW FIRST 3
    for (
      let i = 1;
      i <= Math.min(3, totalPages);
      i++
    ) {
      visiblePages.push(i);
    }
    // ALWAYS SHOW LAST 2
    for (
      let i = Math.max(totalPages - 1, 4);
      i <= totalPages;
      i++
    ) {
      visiblePages.push(i);
    }
    // CURRENT PAGE
    if (
      AdminState.currentPage > 3 &&
      AdminState.currentPage < totalPages - 1
    ) {
      visiblePages.push(AdminState.currentPage);
    }
    // REMOVE DUPLICATE
    const uniquePages =
      [...new Set(visiblePages)]
      .sort((a, b) => a - b);
    let lastPage = 0;
    uniquePages.forEach(page => {
      // DOTS
      if (page - lastPage > 1) {
        pagination.innerHTML += `
          <span class="page-dots">
            ...
          </span>
        `;
      }
      pagination.innerHTML += `
        <button
          class="page-btn ${page === AdminState.currentPage ? 'active' : ''}"
          onclick="changePage(${page})"
        >
          ${page}
        </button>
      `;
      lastPage = page;
    });
    // NEXT
    pagination.innerHTML += `
      <button
        class="page-btn"
        onclick="changePage(${AdminState.currentPage + 1})"
        ${AdminState.currentPage === totalPages ? "disabled" : ""}
      >
        →
      </button>
    `;
    // INPUT PAGE
    pagination.innerHTML += `
      <div class="page-input-box">
        <input
          type="number"
          id="pageInput"
          min="1"
          max="${totalPages}"
          placeholder="${AdminState.currentPage}"
        >
        <button
          class="goto-btn"
          onclick="goToPage()"
        >
          Go
        </button>
      </div>
    `;
  }
  async function goToPage() {
    const page =
      Number(
        document.getElementById("pageInput").value
      );
    if (
      !page ||
      page < 1 ||
      page > AdminState.totalPages
    ) {
      return;
    }
    AdminState.currentPage = page;
    if (isSearching) {
      renderUsers();
    } else {
      await loadUsers(page);
    }
  }
  async function changePage(page) {
    const totalPages =
      AdminState.totalPages;
    if (
      page < 1 ||
      page > totalPages
    ) {
      return;
    }
    AdminState.currentPage = page;
    if (isSearching) {
      renderUsers();
    } else {
      await loadUsers(page);
    }
    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });
  }