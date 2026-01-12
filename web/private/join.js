var url_string = window.location.href;
var url = new URL(url_string);
var paramValue = url.searchParams.get("code");
var codeElement = document.getElementById("code");
if (codeElement) {
    codeElement.value = paramValue;
}

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

//add clic event to checkboxes
var checkboxes = document.querySelectorAll(".check-label");
checkboxes.forEach(function(checkbox) {
    checkbox.addEventListener("click", function(event) {
        //only allwo to uncheck the checkbox if another checkbox in the same input group is checked
        var inputGroup = event.target.parentElement;
        var checkBox = inputGroup.querySelector("#" + event.target.getAttribute("for"));
        if (!checkBox.checked) {
            return;
        }
        //get all checkboxes in the same input group
        var checkboxesInGroup = inputGroup.querySelectorAll(".check-input");
        var checked = false;
        checkboxesInGroup.forEach(function(checkboxInGroup) {
            if (checkboxInGroup.checked && checkboxInGroup !== checkBox) {
                checked = true;
            }
        });
        //if no checkbox is checked, prevent the checkbox from being unchecked
        if (!checked) {
            event.preventDefault();
            event.target.checked = true;
        }
    });
});

// helper: read a cookie by name
function readCookie(name) {
    var nameEq = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(nameEq) === 0) return decodeURIComponent(c.substring(nameEq.length));
    }
    return null;
}

// create overlay element and append to body (hidden by default)
function createOverlay() {
    var overlay = document.createElement('div');
    overlay.id = 'player-overlay';

    var box = document.createElement('div');
    box.className = 'overlay-box';

    var message = document.createElement('div');
    message.id = 'player-overlay-message';
    box.appendChild(message);

    var buttons = document.createElement('div');
    buttons.className = 'overlay-buttons';

    var cancelBtn = document.createElement('button');
    cancelBtn.type = 'button';
    cancelBtn.textContent = 'Cancel';
    cancelBtn.className = 'button cancel submit-button';
    cancelBtn.addEventListener('click', function() {
        overlay.style.display = 'none';
    });

    var submitBtn = document.createElement('button');
    submitBtn.type = 'button';
    submitBtn.textContent = 'Submit anyway';
    submitBtn.className = 'button primary submit-button';
    submitBtn.addEventListener('click', function() {
        overlay.style.display = 'none';
        var form = document.getElementById('join-form');
        if (form) form.submit();
    });

    buttons.appendChild(cancelBtn);
    buttons.appendChild(submitBtn);
    box.appendChild(buttons);
    overlay.appendChild(box);
    document.body.appendChild(overlay);
    overlay.style.display = 'none';
    return overlay;
}

var overlay = createOverlay();

// hook up submit button to check cookie
var submitButton = document.getElementById('submit-button');
if (submitButton) {
    submitButton.addEventListener('click', function(ev) {
        var nameInput = document.getElementById('name');

        // trim to avoid spaces-only names
        if (!nameInput || nameInput.value.trim() === "") {
            ev.preventDefault();

            // highlight input
            nameInput.classList.add('error-border');

            // ensure label floats correctly
            var nameLabel = document.querySelector("label[for='name']");
            if (nameLabel) {
                nameLabel.dataset.focus = "true";
            }

            nameInput.scrollIntoView({
                behavior: 'smooth',
                block: 'center'
            });
            nameInput.focus();


            // remove error when user starts typing
            nameInput.addEventListener('input', function removeError() {
                if (nameInput.value.trim() !== "") {
                    nameInput.classList.remove('error');
                    nameInput.removeEventListener('input', removeError);
                }
            });

            return;
        }

        var player = readCookie('player');
        if (!player) {
            var form = document.getElementById('join-form');
            if (form) form.submit();
            return;
        }

        var messageEl = document.getElementById('player-overlay-message');
        if (messageEl) {
            messageEl.textContent =
                "Someone called " + player +
                " has already put themselves on the list from this device. Do you want to submit again?";
        }
        overlay.style.display = 'flex';
    });
}
