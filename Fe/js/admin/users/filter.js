
  function toggleIdSort() {

    AdminState.currentPage = 1;

    isIdDesc = !isIdDesc;

    renderUsers();
  }

  function filterRole(value) {

    AdminState.currentPage = 1;

    currentRole = value;

    renderUsers();
  }

  function filterGender(value) {

    AdminState.currentPage = 1;

    currentGender = value;

    renderUsers();
  }

  function filterStatus(value) {

    AdminState.currentPage = 1;

    currentStatus = value;

    renderUsers();
  }

  function resetFilters() {

    currentRole = "";
    currentGender = "";
    currentStatus = "";
    searchKeyword = "";

    renderUsers();
  }
  
  async function searchUsers() {

    try {

      const keyword =
        document
          .getElementById("searchInput")
          .value
          .trim();

      const params =
        new URLSearchParams();

      if (keyword)
        params.append(
          "keyword",
          keyword
        );

      if (currentRole)
        params.append(
          "role",
          currentRole
        );

      if (currentGender)
        params.append(
          "gender",
          currentGender
        );

      if (currentStatus)
        params.append(
          "active",
          currentStatus === "ACTIVE"
        );

      const response =
        await apiFetch(
          `${API_BASE}/v1/api/admin/users/search?${params}`
        );

      searchResultUsers =
        response.data || [];

      isSearching = true;

      AdminState.currentPage = 1;

      renderUsers();

    } catch (err) {

      console.error(err);

      showToast(
        err.message,
        "error"
      );
    }
  }

  function handleSearchEnter(event) {

    if (event.key === "Enter") {

      searchUsers();
    }
  }