if (currentDebate) {
    var card = document.getElementById("debate-assign-wrapper");
    //create a div element
    var wrapper = document.createElement("div");
    wrapper.id = "debate-assign";
    //set the innerHTML of the wrapper to the currentDebate
    wrapper.innerHTML = currentDebate;
    card.appendChild(wrapper);
    var inputs = wrapper.getElementsByTagName("input");
    // make the input uneditable
    for (var i = 0; i < inputs.length; i++) {
        var input = inputs[i];
        //create a span element
        input.value = input.getAttribute("data");
        //make input unselectable
        input.setAttribute("readonly", "true");
        input.style.pointerEvents = "none";
    }
    // get the delete buttons
    var deleteButtons = wrapper.getElementsByClassName("delete-debate");
    // remove them
    for (var i = deleteButtons.length - 1; i >= 0; i--) {
        var deleteButton = deleteButtons[i];
        deleteButton.remove();
    }
}
