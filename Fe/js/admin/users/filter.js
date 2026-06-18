  function toggleIdSort() {
    isIdDesc = !isIdDesc;
    renderUsers();
  }
  
  async function filterRole(value) {
    currentRole = value;
    await searchUsers();
  }

  async function filterGender(value) {
    currentGender = value;
    await searchUsers();
  }

  async function filterStatus(value) {
    currentStatus = value;
    await searchUsers();
  }

  function resetFilters() {
    currentRole = "";
    currentGender = "";
    currentStatus = "";
    searchKeyword = "";
    renderUsers();
  }

  async function searchUsers() {
    const keyword =
      document
        .getElementById("searchInput")
        .value
        .trim();
    const params =
      new URLSearchParams();
    if (keyword) {
      params.append(
        "keyword",
        keyword
      );
    }
    if (currentRole) {
      params.append(
        "role",
        currentRole
      );
    }
    if (currentGender) {
      params.append(
        "gender",
        currentGender
      );
    }
    if (currentStatus) {
      params.append(
        "active",
        currentStatus
      );
    }
    const response =
      await apiFetch(
        `${API_BASE}/v1/api/admin/users/search?${params}`
      );
    searchResultUsers =
      response.data || [];
    isSearching = true;
    AdminState.currentPage = 1;
    renderUsers();
  }

  function handleSearchEnter(event) {
    if (event.key === "Enter") {
      searchUsers();
    }
  }
