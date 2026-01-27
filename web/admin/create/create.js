var tableBody = document.getElementById("participants-table-body");
if (tableBody) {
    // fill the table with the participants
    participants.forEach(function(participant) {
        var row = document.createElement("tr");

        var tdName = document.createElement("td");
        tdName.textContent = participant.name;
        row.appendChild(tdName);

        var tdLanguage = document.createElement("td");
        tdLanguage.textContent = participant.language;
        row.appendChild(tdLanguage);

        var tdFormat = document.createElement("td");
        tdFormat.textContent = participant.format;
        row.appendChild(tdFormat);

        var tdRole = document.createElement("td");
        tdRole.textContent = participant.role;
        row.appendChild(tdRole);

        var tdExperience = document.createElement("td");
        tdExperience.textContent = participant.experience;
        row.appendChild(tdExperience);

        var tdNext = document.createElement("td");
        tdNext.textContent = participant.nextTournament;
        row.appendChild(tdNext);

        var tdCouldnt = document.createElement("td");
        tdCouldnt.textContent = participant.couldnt;
        row.appendChild(tdCouldnt);

        tableBody.appendChild(row);
    });
}

var shortTableBody = document.getElementById("participants-table-body-small");
if (shortTableBody) {
    participants.forEach(function(participant, i) {
        var row = document.createElement("tr");
        row.id = "participant-" + i;

        var tdName = document.createElement("td");
        tdName.textContent = participant.name;
        row.appendChild(tdName);

        var tdLanguage = document.createElement("td");
        tdLanguage.className = participant.language.replace("/", "-").toLowerCase();
        row.appendChild(tdLanguage);

        var tdFormat = document.createElement("td");
        tdFormat.className = participant.format.replace("/", "-").toLowerCase();
        row.appendChild(tdFormat);

        var tdRole = document.createElement("td");
        tdRole.className = participant.role.replace("/", "-").toLowerCase();
        row.appendChild(tdRole);

        var tdExperience = document.createElement("td");
        tdExperience.className = participant.experience === "<5" ? "exp-leq-5" :
                              participant.experience === "5-10" ? "exp-5-10" : "exp-geq-10";
        row.appendChild(tdExperience);

        var tdNext = document.createElement("td");
        tdNext.className = participant.nextTournament === "" ? "no-tournament" : "some-tournament";
        row.appendChild(tdNext);

        var tdCouldnt = document.createElement("td");
        tdCouldnt.className = participant.couldnt === "-" ? "couldTalk" : "notTalk";
        row.appendChild(tdCouldnt);

        shortTableBody.appendChild(row);
    });
}


var detailed = true;
function changeView() {
    var assignWrapper = document.getElementById("small-wrapper");
    if (assignWrapper) {
        assignWrapper.setAttribute("data-show", detailed ? "true" : "false");
    }
    var detailsWrapper = document.getElementById("detail-wrapper");
    if (detailsWrapper) {
        detailsWrapper.setAttribute("data-show", detailed ? "false" : "true");
    }
    var assigns = document.getElementById("debate-assign-wrapper");
    if (assigns) {
        assigns.setAttribute("data-show", detailed ? "true" : "false");    
    }
    var vertical = document.getElementById("verticalExtender");
    if (vertical) {
        vertical.classList.remove(detailed ? "left" : "right");
        vertical.classList.add(detailed ? "right" : "left");
    }
    detailed = !detailed;
}

//add onKeyDown event to the text inputs
var textIntputs = document.getElementsByClassName("input");
for (var i = 0; i < textIntputs.length; i++) {
    makeOnKeyDownEvent(textIntputs[i]);
}

function makeOnKeyDownEvent(input) {
    input.addEventListener("keyup", function (event) {
        //check if the input has text in it
        dataFocus = "false";
        if (event.target.value != "") {
            dataFocus = "true";
            //set the inputs data property to the text
            event.target.setAttribute("data", event.target.value);
        }
        // get the name of the input
        var inputName = event.target.getAttribute("name");
        //get label for the input
        var label = document.querySelector("label[for='" + inputName + "']");
        //check if the label is not null
        if (label != null) {
            //set the labels data-focus to true
            label.setAttribute("data-focus", dataFocus);
        }
    });
}

function elementContains(parent, child) {
    // Check if the parent element contains the child element
    if (child) {
        if (child.parentElement === parent) {
            return true;
        }
        // Recursively check parent elements
        if (child.parentElement === null) {
            return false; // Reached the top of the DOM tree without finding the parent
        }
        return elementContains(parent, child.parentElement);
    }
    return false;
}

var maxID = 1
function addBPRoom() {
    var id = maxID++;
    var emptyRoom = `<div class="input-group"><input class="input" type="text" id="room-name-${id}" name="room-name-${id}"><label for="room-name-${id}" data-focus="false">Room Name</label><div id="delete-${id}" class="delete-debate"></div></div><div class="teams"><div class="debate-half"><div class="team"><p>OG</p><div class="member static-member" id="pm-${id}" orig="PM">PM</div><div class="member static-member" id="dpm-${id}" orig="DPM">DPM</div></div><div class="team"><p>OO</p><div class="member static-member" id="lo-${id}" orig="LO">LO</div><div class="member static-member" id="dlo-${id}" orig="DLO">DLO</div></div></div><div class="debate-half"><div class="team"><p>CG</p><div class="member static-member" id="mg-${id}" orig="MG">MG</div><div class="member static-member" id="gw-${id}" orig="GW">GW</div></div><div class="team"><p>CO</p><div class="member static-member" id="mo-${id}" orig="MO">MO</div><div class="member static-member" id="ow-${id}" orig="OW">OW</div></div></div></div><div class="member static-member judge chair expandable" id="chair-${id}" orig="Chair">Chair</div>`;

    var debateAssign = document.getElementById("debate-assign");
    if (debateAssign) {
        var newRoom = document.createElement("div");
        newRoom.classList.add("debate-card");
        newRoom.setAttribute("id", "room-" + id);
        newRoom.innerHTML = emptyRoom;
        debateAssign.appendChild(newRoom);
        //add onKeyDown event to the text inputs in the new room
        var newInput = document.getElementById("room-name-" + id);
        if (newInput) {
            makeOnKeyDownEvent(newInput);
        }
        var deleteButton = document.getElementById("delete-" + id);
        if (deleteButton) {
            deleteButton.addEventListener("click", function() {
                //enable all small table rows
                var room = document.getElementById("room-" + id);
                var smallTabelRows = document.querySelectorAll("#participants-table-body-small tr");
                smallTabelRows.forEach(function(row) {
                    //check if the row was set in this room
                    var participantID = row.id.replace("participant-", "");
                    //check if the room element is parent of the row
                    var participantRow = document.querySelector(".static-member[participant-id='participant-" + participantID + "']");
                    //check if the room element is parent of the row
                    if (row.classList.contains("assigned") && elementContains(room, participantRow)) {
                        row.classList.remove("assigned");
                    }
                });
                if (room) {
                    room.remove();
                }
            });
        }
    }

    //add mouseDown event to the static members in the new room
    var staticMembers = document.querySelectorAll("#room-" + id + " .static-member");
    staticMembers.forEach(function(member) {
        member.addEventListener("mousedown", function(event) {
            if (!this.classList.contains("assigned-static")) {
                return; // Do not allow dragging if not assigned
            }
            downOnStatic = true;
            originalStatic = this;
            createDraggable(this.getAttribute("participant-id"), this.innerHTML, event);
        });
    });
}

function addOPDRoom() {
    var id = maxID++;
    var emptyRoom = `<div class="input-group"><input class="input" type="text" id="room-name-${id}" name="room-name-${id}"><label for="room-name-${id}" data-focus="false">Room Name</label><div id="delete-${id}" class="delete-debate"></div></div><div class="teams"><div class="debate-half"><div class="team"><p>Regierung</p><div class="member static-member" id="gov-1-${id}" orig="1. Redner">1. Redner</div><div class="member static-member" id="gov-2-${id}" orig="2. Redner">2. Redner</div><div class="member static-member" id="gov-3-${id}" orig="3. Redner">3. Redner</div></div><div class="team"><p>Opposition</p><div class="member static-member" id="opp-1-${id}" orig="1. Redner">1. Redner</div><div class="member static-member" id="opp-2-${id}" orig="2. Redner">2. Redner</div><div class="member static-member" id="opp-3-${id}" orig="3. Redner">3. Redner</div></div></div></div><div class="team big-team"><p>Freie Redner</p><div class="member static-member free-speaker first-free-speaker expandable" id="mg-${id}" orig="Freier Redner">Freier Redner</div></div></div><div class="member static-member judge chair expandable" id="chair-${id}" orig="Chair">Chair</div>`;
    var debateAssign = document.getElementById("debate-assign");
    if (debateAssign) {
        var newRoom = document.createElement("div");
        newRoom.classList.add("debate-card");
        newRoom.setAttribute("id", "room-" + id);
        newRoom.innerHTML = emptyRoom;
        debateAssign.appendChild(newRoom);
        //add onKeyDown event to the text inputs in the new room
        var newInput = document.getElementById("room-name-" + id);
        if (newInput) {
            makeOnKeyDownEvent(newInput);
        }
    }

    var deleteButton = document.getElementById("delete-" + id);
    if (deleteButton) {
        deleteButton.addEventListener("click", function() {
            //enable all small table rows
            var room = document.getElementById("room-" + id);
            var smallTabelRows = document.querySelectorAll("#participants-table-body-small tr");
            smallTabelRows.forEach(function(row) {
                //check if the row was set in this room
                var participantID = row.id.replace("participant-", "");
                //check if the room element is parent of the row
                var participantRow = document.querySelector(".static-member[participant-id='participant-" + participantID + "']");
                //check if the room element is parent of the row
                if (row.classList.contains("assigned") && elementContains(room, participantRow)) {
                    row.classList.remove("assigned");
                }
            });
            if (room) {
                room.remove();
            }
        });
    }

    //add mouseDown event to the static members in the new room
    var staticMembers = document.querySelectorAll("#room-" + id + " .static-member");
    staticMembers.forEach(function(member) {
        member.addEventListener("mousedown", function(event) {
            if (!this.classList.contains("assigned-static")) {
                return; // Do not allow dragging if not assigned
            }
            downOnStatic = true;
            originalStatic = this;
            createDraggable(this.getAttribute("participant-id"), this.innerHTML, event);
        });
    });
}

//create drag and drop functionality for the debate assign
var mouseIsDown = false;
var downOnStatic = false;
var originalStatic = null;
var draggedElement = null;
var draggedID = null;
var smallTabelRows = document.querySelectorAll("#participants-table-body-small tr");

function createDraggable(id, name, event) {
    //create new element to drag
    mouseIsDown = true;
    draggedID = id; // Store the ID of the dragged element
    draggedElement = document.createElement("div");
    draggedElement.classList.add("member");
    draggedElement.classList.add("dragged");

    draggedElement.innerHTML = name;
    //get the overlay
    var overlay = document.getElementById("drag-overlay");
    if (overlay && draggedElement) {
        overlay.style.display = "block";
        overlay.appendChild(draggedElement);
        draggedElement.classList.add("dragged");
        //set the position of the dragged element to the mouse position centered
        // Center the dragged element under the cursor
        var rect = draggedElement.getBoundingClientRect();
        draggedElement.style.left = (event.pageX - rect.width / 2) + "px";
        draggedElement.style.top = (event.pageY - rect.height / 2) + "px";
    }
}
smallTabelRows.forEach(function(row) {
    row.addEventListener("mousedown", function(event) {
        //check if the row has already been assigned
        if (this.classList.contains("assigned")) {
            return; // Do not allow dragging if already assigned
        }
        createDraggable(this.id, this.innerHTML, event);
    });
});

document.addEventListener("mouseup", function(event) {
    mouseIsDown = false;
    if (draggedElement) {
        //remove the dragged element from the overlay
        var overlay = document.getElementById("drag-overlay");
        if (overlay) {
            overlay.removeChild(draggedElement);
            overlay.style.display = "none";
        }
        //get closest member element
        var closestMember = document.elementsFromPoint(event.clientX, event.clientY)
            .find(el => el.classList && el.classList.contains("static-member"));
        var closestTable = document.elementsFromPoint(event.clientX, event.clientY)
            .find(el => el.id && el.id == "small-wrapper");
        if (!closestMember && !closestTable) {
            return;
        }
        if (closestMember) {
            //check that the closest member is not already assigned
            if (closestMember.classList.contains("assigned-static")) {
                return; // Do not allow assigning to already assigned members
            }
            //replace the innerhtml with the correct name
            closestMember.innerHTML = draggedElement.innerHTML;
            //add the id to the closest member
            closestMember.setAttribute("participant-id", draggedID);
            closestMember.classList.add("assigned-static");
            originalRow = document.getElementById(draggedID);
            if (originalRow) {
                //mark the row as assigned
                originalRow.classList.add("assigned");
            }
            //check if the closest member is a judge
            if (closestMember.classList.contains("expandable")) {
                //add a new empty judge row to the table
                var row = document.createElement("div");
                row.classList.add("member");
                row.classList.add("static-member");
                row.classList.add("expandable");
                if (closestMember.classList.contains("judge")) {
                    row.classList.add("judge");
                    row.setAttribute("orig", "Panelist");
                    row.setAttribute("id", "judge-" + draggedID);
                    row.innerHTML = `Panelist`;
                }else if (closestMember.classList.contains("free-speaker")) {
                    row.classList.add("free-speaker");
                    row.setAttribute("orig", "Freier Redner");
                    row.setAttribute("id", "free-" + draggedID);
                    row.innerHTML = `Freier Redner`;
                }
                row.setAttribute("participant-id", draggedID);
                var debate = closestMember.parentElement;
                debate.appendChild(row);
                //add mouseDown event to the new judge row
                row.addEventListener("mousedown", function(event) {
                    if (!this.classList.contains("assigned-static")) {
                        return; // Do not allow dragging if not assigned
                    }
                    downOnStatic = true;
                    originalStatic = this;
                    createDraggable(this.getAttribute("participant-id"), this.innerHTML, event);
                });
            }
        } else if (closestTable) {
            //find the row with the id of the dragged element
            var originalRow = document.getElementById(draggedID);
            if (originalRow) {
                //remove the assigned class from the row
                originalRow.classList.remove("assigned");
            }
        }
        draggedElement = null;
        //if we dragged from a static member, remove the static member class
        if (downOnStatic && originalStatic != closestMember) { 
            //cleanup if judge
            if (originalStatic.classList.contains("expandable")) {
                var parentElement = originalStatic.parentElement;
                //get all panelist elements
                var panelists = parentElement.querySelectorAll(".static-member.expandable");
                // loop panelists and remove if they are empty
                panelists.forEach(function(panelist) {
                    if (panelist.innerHTML.trim() === "Panelist") {
                        panelist.remove();
                    }else if (panelist.innerHTML.trim() === "Freier Redner" && !panelist.classList.contains("first-free-speaker")) {
                        panelist.remove();
                    }
                });
            }
            originalStatic.innerHTML = originalStatic.getAttribute("orig");
            originalStatic.classList.remove("assigned-static");
            downOnStatic = false;
            originalStatic.removeAttribute("participant-id");
            originalStatic = null; 
        }
    }
});

document.addEventListener("mousemove", function(event) {
    if (mouseIsDown) {
        //set the position of the dragged element to the mouse position centered
        // Center the dragged element under the cursor
        var rect = draggedElement.getBoundingClientRect();
        draggedElement.style.left = (event.pageX - rect.width / 2) + "px";
        draggedElement.style.top = (event.pageY - rect.height / 2) + "px";
    }
});

/*function startDebate() {
    //get all the rooms
    var rooms = document.querySelectorAll(".debate-card");
    var debateData = [];
    rooms.forEach(function(room) {
        var roomName = room.querySelector("input").value;
        if (roomName.trim() === "") {
            alert("Please enter a room name for all rooms.");
            return;
        }
        var teams = room.querySelectorAll(".team");
        var memberData = [];
        teams.forEach(function(team) {
            var members = team.querySelectorAll(".member");
            members.forEach(function(member) {
                if (member.classList.contains("assigned-static")) {
                    memberData.push({
                        id: member.getAttribute("orig"),
                        name: member.innerHTML
                    });
                }
            });
        });
        var judges = room.querySelectorAll(".judge");
        var judgeData = [];
        var i = 1;
        var setChair = false;
        judges.forEach(function(judge) {
            if (judge.classList.contains("assigned-static") && judge.getAttribute("orig") == "Panelist") {
                judgeData.push({
                    id: "Panelist " + i++,
                    name: judge.innerHTML
                });
            }else if (judge.classList.contains("assigned-static")) {
                judgeData.push({
                    id: "Chair",
                    name: judge.innerHTML
                });
                setChair = true;
            }
        });
        if (!setChair) {
            alert("Please assign a chair for all rooms.");
            return;
        }
        debateData.push({
            roomName: roomName,
            members: memberData,
            judges: judgeData
        });
    });
    //send the data to the server
    fetch("/start", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(debateData)
    }).then(function(response) {
        if (response.ok) {
            //redirect to the debate page
            window.location.href = "/tab";
        } else {
            alert("Error starting the debate. Please try again.");
        }
    }).catch(function(error) {
        console.error("Error:", error);
        alert("Error starting the debate. Please try again.");
    });
}*/
function startDebate() {
    //get innerHTML of room wrapper
    var debateAssign = document.getElementById("debate-assign");
    var debateData = debateAssign.innerHTML;
    //send the data to the server
    var form = document.createElement("form");
    var element = document.createElement("input"); 

    form.method = "POST";
    form.action = "/start/";   

    element.value=debateData;
    element.name="debate";
    form.appendChild(element);

    document.body.appendChild(form);

    form.submit();
}

//reconstruct debate if current exists
if (currentDebate.trim !== "") {
    var debateAssign = document.getElementById("debate-assign");
    if (debateAssign) {
        debateAssign.innerHTML = currentDebate;
        //add onKeyDown event to the text inputs in the new room
        var newInputs = debateAssign.querySelectorAll(".input");
        newInputs.forEach(function(input) {
            makeOnKeyDownEvent(input);
        });
        //add mouseDown event to the static members in the new rooms
        var staticMembers = debateAssign.querySelectorAll(".static-member");
        staticMembers.forEach(function(member) {
            member.addEventListener("mousedown", function(event) {
                if (!this.classList.contains("assigned-static")) {
                    return; // Do not allow dragging if not assigned
                }
                downOnStatic = true;
                originalStatic = this;
                createDraggable(this.getAttribute("participant-id"), this.innerHTML, event);
            });
        });
        //set assign on all the rows
        var smallTabelRows = document.querySelectorAll("#participants-table-body-small tr");
        smallTabelRows.forEach(function(row) {
            var participantID = row.id.replace("participant-", "");
            //check if there is a static member with the same ID
            var staticMember = debateAssign.querySelector("[participant-id='participant-" + participantID + "']");
            if (staticMember) {
                //set small table row as assigned
                row.classList.add("assigned");
            }
        });
    }
    //set the detailed view to false
    changeView();
}

function resetDebate() {
    //reset the debate assign
    var debateAssign = document.getElementById("debate-assign");
    if (debateAssign) {
        debateAssign.innerHTML = "";
    }
    // make all small table rows unassigned
    var smallTabelRows = document.querySelectorAll("#participants-table-body-small tr");
    smallTabelRows.forEach(function(row) {
        row.classList.remove("assigned");
    });
    //reset the maxID
    maxID = 1;
    //save the reset state to the server
    startDebate(); // This will send an empty debate to the server
}

function deleteDebate() {
    //send a request to delete all users and recreate the PDF
    fetch("/reset", {
        method: "POST",
    }).then(function(response) {
        if (response.ok) {
            //reload the page
            window.location.reload();
        } else {
            alert("Error deleting users and recreating PDF. Please try again.");
        }
    }).catch(function(error) {
        console.error("Error:", error);
        alert("Error deleting users and recreating PDF. Please try again.");
    });
}