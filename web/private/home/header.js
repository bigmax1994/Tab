if (isAdmin == true) {
    //enable all elements with the class admin-nav
    var adminNav = document.getElementsByClassName("admin-nav");
    for (var i = 0; i < adminNav.length; i++) {
        adminNav[i].style.display = "block";
    }
}