
  function saveUser(user) {

    localStorage.setItem(
      "user",
      JSON.stringify(user)
    );
  }

  function getUser() {

    const user =
      localStorage.getItem("user");

    return user
      ? JSON.parse(user)
      : null;
  }

  function clearUser() {

    localStorage.removeItem("user");
  }

  function saveToken(token) {

    localStorage.setItem("access_token", token);
    
  }

  function getToken() {
    return localStorage.getItem("access_token");
  }

  function logout() {

    localStorage.removeItem(
      "access_token"
    );

    localStorage.removeItem(
      "user"
    );

    localStorage.removeItem(
      "permissions"
    );

    window.location.href =
      "/Fe/login/";
  }

  function adminlogout() {

    localStorage.removeItem(
      "access_token"
    );

    localStorage.removeItem(
      "user"
    );

    localStorage.removeItem(
      "permissions"
    );

    window.location.href =
      "/Fe/login/";
  }