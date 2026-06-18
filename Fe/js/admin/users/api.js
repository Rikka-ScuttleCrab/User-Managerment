  async function createUser() {
    const username =
      document.getElementById("createUsername").value.trim();
    const password =
      document.getElementById("createPassword").value.trim();
    const email =
      document.getElementById("createEmail").value.trim();
    const nickname =
      document.getElementById("createNickname").value.trim();
    const gender =
      document.getElementById("createGender").value;
    const errorEl =
      document.getElementById("createError");
    errorEl.innerText = "";
    // VALIDATE
    if (!username) {
      errorEl.innerText =
        "Vui lòng nhập username";
      return;
    }
    if (!password) {
      errorEl.innerText =
        "Vui lòng nhập mật khẩu";
      return;
    }
    if (password.length < 8) {
      errorEl.innerText =
        "Mật khẩu phải có ít nhất 8 ký tự";
      return;
    }
    if (!email) {
      errorEl.innerText =
        "Vui lòng nhập email";
      return;
    }
    if (!gender) {
      errorEl.innerText =
        "Vui lòng chọn giới tính";
      return;
    }
    try {
      const body = {
        username,
        password,
        email,
        gender
      };
      // nickname optional
      if (nickname) {
        body.nickname = nickname;
      }
      const res = await fetch(
        `${API_BASE}/v1/api/auth/sign-up`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(body)
        }
      );
      const result = await res.json();
      if (!res.ok) {
        if (result.message === "Email already exists") {
            errorEl.innerText = "Email đã được đăng ký";
        } else if (result.message === "Username already exists") {
            errorEl.innerText ="Tên đăng nhập đã tồn tại";
        } else if (result.data.email === "Invalid email format") {
            errorEl.innerText = "Định dạng email không hợp lệ";
        } else {
            errorEl.innerText =
              result.message ||
                "Tạo người dùng thất bại";
        }
        return;
      }
      closeCreateUserModal();
      await loadUsers();
      showToast("Tạo người dùng thành công", "success");
    } catch (err) {
      console.log(err);
      errorEl.innerText =
        "Không thể kết nối server";
    }
  }
  
  async function saveUserRoles() {
    try {
      const user =
        [...AdminState.allUsers, ...searchResultUsers]
          .find(u => u.id === selectedRoleUserId);
        const checkedRoles =
            document.querySelectorAll(
                ".role-checkbox:checked"
            );
        let roleIds =
            Array.from(checkedRoles)
            .map(cb => Number(cb.value));
        const hiddenRoles =
            allRoles.filter(role => {
                const roleName =
                    role.roleName?.toUpperCase();
                return (
                    roleName === "ADMIN"
                    ||
                    roleName === "READER"
                );
            });
        hiddenRoles.forEach(role => {
            const hasRole =
                user.roles?.some(
                    r =>
                        r.toUpperCase() ===
                        role.roleName.toUpperCase()
                );
            if (hasRole) {
                roleIds.push(role.roleId);
            }
        });
        roleIds = [...new Set(roleIds)];
      const res = await fetch(
        `${API_BASE}/v1/api/admin/users/add-roles/${selectedRoleUserId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          },
          body: JSON.stringify({
            roleIds
          })
        }
      );
      if (!res.ok) {
        throw new Error(
          "Cập nhật role thất bại"
        );
      }

      const newRoles =
        allRoles
          .filter(role =>
            roleIds.includes(role.id)
          )
          .map(role =>
            role.roleName
          );
      AdminState.allUsers = AdminState.allUsers.map(user => {
        if (
          user.id === selectedRoleUserId
        ) {
          return {
            ...user,
            roles: newRoles
          };
        }
        return user;
      });
      closeRoleModal();
      searchUsers();
      showToast("Cập nhật nhóm thành công", "success");
    } catch (err) {
      console.log(err);
      showToast(err.message, "error");
    }
  }

  async function saveEditUser() {
    try {
      const email =
        document.getElementById(
          "editEmail"
        ).value.trim();
      const nickname =
        document.getElementById(
          "editNickname"
        ).value.trim();
      const gender =
        document.getElementById(
          "editGender"
        ).value;
      const errorEl =
        document.getElementById(
          "editUserError"
        );
      errorEl.innerText = "";
      if (!email) {
        errorEl.innerText =
          "Vui lòng nhập email";
        return;
      }
      const body = {
        email,
        gender,
        nickname
      };
      const res = await fetch(
        `${API_BASE}/v1/api/admin/users/${selectedEditUserId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type":
              "application/json",
            "Authorization":
              `Bearer ${token}`
          },
          body: JSON.stringify(body)
        }
      );
      const result =
        await res.json();
      if (!res.ok) {
        if (result.message === "Email already exists") {
            errorEl.innerText = "Email đã được đăng ký";
        } else if (result.message === "Username already exists") {
            errorEl.innerText ="Tên đăng nhập đã tồn tại";
        } else if (result.message === "Invalid email format") {
            errorEl.innerText = "Định dạng email không hợp lệ";
        } else {
            errorEl.innerText =
              result.message ||
                "Cập nhật người dùng thất bại";
        }
        return;
      }
      // UPDATE LOCAL
      AdminState.allUsers = AdminState.allUsers.map(user => {
        if (
          user.id === selectedEditUserId
        ) {
          return {
            ...user,
            email,
            nickname,
            gender
          };
        }
        return user;
      });
      renderUsers();
      closeEditModal();
      showToast(
        "Cập nhật thành công", "success"
      );
    } catch (err) {
      console.log(err);
      showToast(err.message, "error");
    }
  }

  async function submitResetPassword() {
    let newMessage ="";
    if (!selectedResetUserId) {
      return;
    }
    try {
      const res = await fetch(
        `${API_BASE}/v1/api/admin/users/reset-password/${selectedResetUserId}`,
        {
          method: "PUT",
          headers: {
            "Authorization":
              `Bearer ${token}`
          }
        }
      );
      const result =
        await res.json();
      console.log(result);
      if (!res.ok) {
        throw new Error(
          result.message ||
          "Reset password thất bại"
        );
      }
      closeResetPasswordConfirmModal();
      closeResetPasswordModal();
      if(result.data?.message == "Password reset successfully") {
        newMessage = "Reset password thành công";
      }
      showToast(
        newMessage ||
        result?.data?.message, "success"
      );
    } catch (err) {
      console.log(err);
      showToast(err.message, "error");
    }
  }

  async function activeUser(userId) {
    try {
      const res = await fetch(
        `${API_BASE}/v1/api/admin/users/${userId}/active`,
        {
          method: "PUT",
          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );
      const result = await res.json();
      if (!res.ok) {
        throw new Error(
          result.message ||
          "Thao tác thất bại"
        );
      }
      closeConfirm();
      // UPDATE LOCAL
      AdminState.allUsers = AdminState.allUsers.map(user => {
        if (user.id === userId) {
          return {
            ...user,
            active: !user.active
          };
        }
        return user;
      });
      showToast(
        "Cập nhật trạng thái thành công",
        "success"
      );
      searchUsers();
    } catch (err) {
      console.log(err);
      showToast(err.message, "error");
    }
  }

  function renderUsers() {
    const table =
      document.getElementById("userTable");
    let users =
      isSearching
        ? [...searchResultUsers]
        : [...AdminState.allUsers];
    const totalPages =
      isSearching
        ? Math.ceil(
            users.length / usersPerPage
          )
        : AdminState.totalPages;
    AdminState.totalPages =
      totalPages;
      const startIndex =
        (AdminState.currentPage - 1)
        * usersPerPage;
      const endIndex =
        startIndex + usersPerPage;
      const paginatedUsers =
        isSearching
          ? users.slice(
              startIndex,
              endIndex
            )
          : users;
    if (currentRole) {
      users = users.filter(user =>
        user.roles?.some(role =>
          role.toUpperCase() === currentRole.toUpperCase()
        )
      );
    }
    // FILTER GENDER
    if (currentGender) {
      users = users.filter(
        user =>
          user.gender?.toUpperCase() ===
          currentGender.toUpperCase()
      );
    }
    // FILTER STATUS
    if (currentStatus) {
      users = users.filter(user => {
        if (currentStatus === "ACTIVE") {
          return user.active === true;
        }
        if (currentStatus === "INACTIVE") {
          return user.active === false;
        }
        return true;
      });
    }
    // SORT ID
    users.sort((a, b) => {
      return isIdDesc
        ? b.id - a.id
        : a.id - b.id;
    });
    // SEARCH
    if (searchKeyword) {
      const keyword =
        searchKeyword.toLowerCase();
      users = users.filter(user => {
        return (
          String(user.id)
            .toLowerCase()
            .includes(keyword)
          ||
          user.username
            ?.toLowerCase()
            .includes(keyword)
          ||
          user.nickname
            ?.toLowerCase()
            .includes(keyword)
          ||
          user.email
            ?.toLowerCase()
            .includes(keyword)
        );
      });
    }
    table.innerHTML = "";
    paginatedUsers.forEach((user) => {
      const rowIndex =
        paginatedUsers.indexOf(user);

      const shouldOpenUp =
        paginatedUsers.length > 5 &&
        rowIndex >= paginatedUsers.length - 2;

      const isAdminUser =
        user.roles?.some(role =>
          role.toUpperCase() === "ADMIN"
      );
      
      const isHiddenActive =  AdminState.currentUser?.username === user.username;
      table.innerHTML += `
        <tr>
          <td>${user.id}</td>
          <td>${user.username}</td>
          <td>${user.email}</td>
          <td>${user.nickname || ""}</td>
          <td>
            <div class="roles-wrap">
              ${
                user.roles?.map(role => `
                  <span class="role-badge">
                    ${role}
                  </span>
                `).join("")
              }
            </div>
          </td>
          <td>${user.gender}</td>
          <td>
            <span class="status ${user.active ? 'active' : 'inactive'}">
              ${user.active ? 'Hoạt động' : 'Bị khóa'}
            </span>
          </td>
          <td class="action-cell">
            <button 
              data-permission="UPDATE"
              class="action-btn edit-btn hidden"
              onclick="openEditModal(${user.id})">
              Sửa
            </button>
            <div class="dropdown">
              <button
                data-permission="UPDATE"
                class="more-btn hidden"
                onclick="toggleDropdown(event, ${user.id})">
                ⋮
              </button>
              <div
                class="dropdown-menu ${shouldOpenUp ? 'drop-up' : ''}"
                id="dropdown-${user.id}">
                  <button
                    data-permission="ROLE"
                    class="action-btn role-btn hidden"
                    onclick="openRoleModal(${user.id})">
                    Phân nhóm
                  </button>
                  <button
                    data-permission="UPDATE"
                    class="action-btn reset-btn hidden"
                    onclick="openResetPasswordModal(${user.id})">
                    Reset Password
                  </button>
                  <button
                    data-permission="ACTIVE"
                    class="
                      action-btn
                      ${user.active ? "disable-btn" : "enable-btn"}
                      ${isAdminUser ? "admin-protected hidden" : ""}
                      ${isHiddenActive ? "hidden" : ""}
                    "
                    onclick="openConfirm(${user.id})"
                    ${isHiddenActive || isAdminUser ? "disabled" : ""}>
                    ${user.active ? 'Vô hiệu hoá' : 'Kích hoạt'}
                  </button>
              </div>
            </div>
          </td>
        </tr>
      `;
    });
    document.getElementById(
      "totalUsersCount"
    ).innerText =
      `${AdminState.totalItems}`;
    checkButtonPermissions("users", "UPDATE");
    checkButtonPermissions("users", "ROLE");
    checkButtonPermissions("users", "ACTIVE");
    renderPagination(AdminState.totalPages);
  }