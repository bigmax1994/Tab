# DCM Tab

DCM Tab is a small web application that helps you organize debate rounds:
collect participants, create rooms, and allocate speakers and judges in a clear and visual way.

You do **not** need programming knowledge to use it.

---

## What does this do?

In short:

* Participants sign up via a web page
* You reset and prepare a debate
* A PDF is generated with a join link and QR code
* You create rooms and drag people into place
* Everyone can see the final allocation online

---

## Installation (one command)

If you are running a Linux server with systemd (for example Ubuntu or Debian), installation is a **single command**.

```bash
curl -fsSL https://github.com/bigmax1994/Tab/releases/latest/download/install.sh | sudo bash
```

The installer will:

* Ask you a few simple questions (domain name, username, password, etc.)
* Download the application
* Set it up as a system service
* Install and configure nginx and HTTPS automatically

If the app is already installed, the installer will update it safely.

---

## First steps after installation

Once installation is finished, open your browser and go to:

```
https://your-domain.example
```

Log in using the **username and password** you chose during installation.

You are now on the main control page.

---

## Typical workflow (step by step)

### 1. Reset the debate

Before **every debate**:

1. Go to the main page
2. Click **Create a Debate**
3. Press **Reset Debate**

What this does:

* Removes all previously registered participants
* Generates a new PDF for the current debate

---

### 2. Download and share the debate PDF

After resetting:

* Click **Download Debate PDF**
* The PDF opens in a new browser window

The PDF contains:

* A link to the participant join page
* A QR code pointing to that join page

You can:

* Share the link digitally
* Print the PDF and put it up in the venue

---

### 3. Let participants sign up

Participants open the join link and enter their information.

NOTE: **You must reload your page** to see new signups

---

### 4. Create rooms

Once signups are complete:

1. Create rooms as needed
2. Give them clear names

Recommended naming style:

* Language + building room name
  Example:
  `English – Room A101`

---

### 5. Drag participants into place

Drag participants into their respective roles and rooms.

You can freely move people around until you are happy with the allocation.

---

## Understanding the color scheme

In the collapsed participant view, colors are used to quickly show preferences.

The system always uses the same **color gradient**:

```
Red → Yellow → Green
```

The position on the gradient represents the answer to a question.

### What the colors mean

* **Green**: Yes / Positive / Preferred
* **Red**: No / Negative / Not preferred
* **Yellow**: Both / Mixed / In between

### Applied to specific questions

* **Language** Do you want to speak English?

  * Green: English
  * Red: German
  * Yellow: English and German

* **Role** Do you want to Speak?

  * Green: Wants to speak
  * Red: Wants to judge
  * Yellow: Will do both

* **Debate format** Do you want to speak BP?

  * Green: BP
  * Red: OPD
  * Yellow: Both

* **Experience** Are you experienced?

  * Green: Very experienced
  * Red: Beginner
  * Yellow: Some experience

* **Preferences** Do you not have a preffered teammate or upcoming tournament?

  * Green: No special preferences
  * Red: Has preferences (teammate, tournament, etc.)

* **Last debate** Were you able to speak last time?

  * Green: Was able to speak last time
  * Red: Was not able to speak last time

This makes it easy to spot good matches at a glance.

---

## Saving and publishing the allocation

If someone signs up **after your intended deadline**:

1. Press **Save**
2. The page reloads

Important:

* Pressing **Save** makes the current allocation public
* Everyone else can now reload their page to see the full allocation

This is also the final step of the process. After pressing save tell people to reload their web-pages (**NOT** the page they submitted their name, but the one where they get sent to after pressin "Join") to see the allocation.

---

## Summary of the process

1. Reset debate
2. Download and share the PDF
3. Collect signups
4. Reload page
5. Create rooms
6. Drag participants
7. Press Save
8. Everyone reloads to see the result

---

# Technical Overview

This section explains how DCM Tab works internally.
You do **not** need to understand this to use the software, but it can be helpful if you want to modify or extend it.

---

## High-level architecture (plain English)

DCM Tab is a **single Java application** that:

* Runs a small web server
* Serves HTML, CSS, and JavaScript files to users
* Handles logins, sessions, and debate state
* Generates a PDF with a QR code for participant sign-up

It does **not** require a database server.
All state is kept in memory while the application is running.

For production use, the Java server is meant to run **behind nginx**, which handles HTTPS and forwards requests to the Java process.

---

## Entry point: `App.java`

The application starts in:

```
com.dcm.App
```

This class:

* Parses command-line arguments
* Starts an embedded Jetty HTTP server
* Initializes global handlers and helpers

### Required command-line arguments

The application must be started with:

```
java -jar tab.jar <port> <username> <password> <resources path> <dns>
```

Where:

* **port** – internal port Jetty listens on (usually 8080)
* **username / password** – admin login credentials
* **resources path** – path to the `web/` directory
* **dns** – public base URL (used for QR codes and links)

---

## Web server (Jetty)

DCM Tab uses **Jetty** as an embedded web server.

Key points:

* Jetty is started programmatically
* A single `ServerConnector` listens on the configured port
* Requests are routed using Jetty `ContextHandler`s

All routing logic is defined in:

```
com.dcm.Handlers.WebHandler
```

---

## Request routing (`WebHandler`)

`WebHandler.makeWebHandler(...)` builds the entire routing tree.

### Main routes

| Path            | Purpose                               |
| --------------- | ------------------------------------- |
| `/`             | Login page (auth-protected resources) |
| `/public`       | Public static files (CSS, images, JS) |
| `/tab`          | Logged-in user interface              |
| `/admin`        | Admin-only interface                  |
| `/join`         | Participant join page                 |
| `/join/handler` | Join form submission                  |
| `/start`        | Save / publish debate                 |
| `/reset`        | Delete all users and recreate PDF     |
| `/current`      | Fetch current debate state            |
| `/session`      | Session handling                      |
| `/signout`      | Logout                                |

Authentication is enforced using custom filters such as:

* `AuthFilter`
* `LogInCheck`

---

## Sessions and authentication

* Jetty’s `SessionHandler` is used
* Cookies are configured with:

  * `SameSite=LAX`
  * `Secure=true`
* Sessions apply to the entire site (`/`)

Admin-only routes are protected by stricter authentication checks.

---

## Static resources

HTML, CSS, JS, and assets are served from the `web/` directory.

Expected structure:

```
web/
├── public/
├── auth/
├── tab/
├── admin/
├── join/
```

Jetty’s `ResourceHandler` serves these directories directly.

---

## Debate state and allocation

### Participants

* Participants register via `/join`
* Data is collected through an HTML form
* JavaScript (`join.js`) handles UI behavior and validation

Participant data includes:

* Name
* Language
* Format (BP / OPD)
* Role preference (Speaker / Judge)
* Experience
* Comments / preferences
* Whether they could speak last time

---

### Admin interface

The admin interface lives under:

```
/admin/create/create.html
```

It allows:

* Viewing participants in table form
* Switching between detailed and compact views
* Creating BP or OPD rooms
* Drag-and-drop allocation of participants
* Assigning chairs and panelists
* Saving or resetting the debate

Most of the logic is handled in:

```
create.js
```

This file implements:

* Drag & drop behavior
* Visual state tracking
* Room creation and deletion
* Debate serialization (HTML-based)

---

## Debate persistence

When the admin presses **Save**:

* The current allocation HTML is sent to `/start`
* The server stores this HTML as the current debate state
* Other users can reload their page to see the allocation

This design avoids complex data models and keeps state simple.

---

## PDF and QR code generation

When a debate is reset or created:

* A new debate code is generated
* A QR code is created using `QRGenerator`
* A PDF is generated using **Apache PDFBox**

The PDF contains:

* A title
* A QR code
* The join URL in plain text

The PDF is saved to:

```
<resources path>/admin/debate.pdf
```

This file is directly downloadable by admins.

---

## Frontend behavior

### Join page (`join.html` + `join.js`)

* Enforces name input
* Prevents invalid checkbox states
* Shows a confirmation overlay if a participant already joined from the same device
* Smoothly scrolls and highlights errors

### Admin UI (`create.js`)

* Two participant views:

  * Detailed table
  * Compact color-coded table
* Drag participants into roles
* Automatically manages judges and free speakers
* Cleans up assignments when rooms are deleted

---

## Summary

In short:

* Java + Jetty for the backend
* Plain HTML/CSS/JS frontend
* No database
* State kept in memory
* PDF generation via PDFBox
* Designed to be run behind nginx with HTTPS

This makes DCM Tab simple to deploy, easy to reset between debates, and hard to accidentally break.

---

## License

This project is free software and is licensed under the GNU General Public License (GPL).  
You are free to use, modify, and redistribute it under the terms of the GPL.

See the LICENSE file in this repository for the full license text.

---

## Questions or issues?

If something does not work:

* Check the systemd service status
* Check nginx logs
* Open an issue on GitHub

Contributions and feedback are welcome.

---
