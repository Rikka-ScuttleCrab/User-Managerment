  /* DATA */
  const {
    allUsers,
    currentPage,
    currentUser,
  } = AdminState;
  let isSearching = false;
  let searchResultUsers = [];
  let allRoles = [];
  let selectedUserId = null;
  let selectedRoleUserId = null;
  let selectedEditUserId = null;
  let selectedResetUserId = null;
  const usersPerPage = 7;
  /* FILTER */
  let currentRole = "";
  let currentGender = "";
  let currentStatus = "";
  let searchKeyword = "";
  /* SORT */
  let isIdDesc = false;