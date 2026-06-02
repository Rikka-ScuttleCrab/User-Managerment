// js/common/admin-state.js

const AdminState = {

  // AUTH
  currentUser: null,

  // USERS
  allUsers: [],
  currentPage: 1,
  usersPerPage: 50,

  // ROLES
  allRoles: [],

  // SELECTED
  selectedUserId: null,
  selectedRoleUserId: null,
  selectedEditUserId: null,
  selectedResetUserId: null,
  selectedRoleId: null,
  editingRoleId: null,

  // FILTER
  currentRole: "",
  currentGender: "",
  currentStatus: "",
  searchKeyword: "",

  // SORT
  isIdDesc: false,

  // ROLE USERS
  searchedUsers: [],
  roleUsers: [],
  pendingRoleUsers: [],

  // SEARCH
  searchTimeout: null
};