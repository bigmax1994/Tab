//add onKeyDown event to the text inputs
var textIntputs = document.getElementsByClassName("input");
for (var i = 0; i < textIntputs.length; i++) {
    textIntputs[i].addEventListener("keyup", function (event) {
        //check if the input has text in it
        dataFocus = "false";
        if (event.target.value != "") {
            dataFocus = "true";
        }
        // get the name of the input
        var inputName = event.target.getAttribute("name");
        //get label for the input
        var label = document.querySelector("label[for='" + inputName + "']");
        //check if the label is not null
        if (label != null) {
            //set the labels data-focus to true
            label.dataset.focus = dataFocus;
        }
    });
}

//on document load, check if the user has failed to login
if (failedLogin == true) {
    //set the login failed message to true
    document.getElementById("loginFailed").style.display = "block";
    document.getElementById("loginFailed").innerHTML = loginError;
    //set the class erro-border on the inputs
    var inputs = document.getElementsByClassName("input");
    for (var i = 0; i < inputs.length; i++) {
        inputs[i].classList.add("error-border");
    }
    failedLogin = false;
}