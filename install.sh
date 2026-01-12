#!/usr/bin/env bash
set -e

### ===== CONFIG =====
APP_NAME="dcm-tab"
SERVICE_NAME="tab"
DEFAULT_PORT="8080"
DEFAULT_INSTALL_DIR="/opt/tab"
GITHUB_REPO="bigmax1994/Tab"
JAR_NAME="tab.jar"
WEB_NAME="web.zip"
### ==================

if [[ $EUID -ne 0 ]]; then
    echo "❌ Please run as root (use sudo)"
    exit 1
fi

echo "🚀 DCM Tab installer"

### Detect existing install
INSTALLED=false
if systemctl list-unit-files | grep -q "${SERVICE_NAME}.service"; then
    INSTALLED=true
    echo "⚠️ Existing installation detected."
    read -rp "Update existing installation? [Y/n]: " UPDATE
    UPDATE=${UPDATE:-Y}
    if [[ "$UPDATE" =~ ^[Nn]$ ]]; then
        exit 0
    fi
    systemctl stop "${SERVICE_NAME}.service" || true
    # --- Detect existing install directory from systemd ---
    SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"

    echo "🔍 Reading existing service configuration"

    EXEC_LINE=$(grep -E '^ExecStart=' "$SERVICE_FILE" | cut -d= -f2)

    # Expected format:
    # java -jar /opt/tab/tab.jar 8080 user pass /opt/tab/web https://domain
    INSTALL_DIR=$(echo "$EXEC_LINE" | awk '{print $3}' | sed 's|/tab\.jar$||')

    if [[ -z "$INSTALL_DIR" || ! -d "$INSTALL_DIR" ]]; then
        echo "❌ Could not determine install directory from tab.service"
        exit 1
    fi

    echo "📂 Detected install directory: $INSTALL_DIR"

    # --- Download latest release ---
    TMP_DIR=$(mktemp -d)
    echo "⬇️ Downloading latest release"

    curl -L "https://github.com/$GITHUB_REPO/releases/latest/download/tab.jar" -o "$TMP_DIR/tab.jar"
    curl -L "https://github.com/$GITHUB_REPO/releases/latest/download/web.zip" -o "$TMP_DIR/web.zip"

    # --- Replace files ---
    echo "♻️ Updating application files"

    cp "$TMP_DIR/tab.jar" "$INSTALL_DIR/tab.jar"

    rm -rf "$INSTALL_DIR/web"
    mkdir -p "$INSTALL_DIR/web"
    unzip -q "$TMP_DIR/web.zip" -d "$INSTALL_DIR/web"

    # --- Cleanup ---
    rm -rf "$TMP_DIR"

    echo "✅ Update completed"
    exit 0
fi

### Ask questions
read -rp "Install directory [$DEFAULT_INSTALL_DIR]: " INSTALL_DIR
INSTALL_DIR=${INSTALL_DIR:-$DEFAULT_INSTALL_DIR}

read -rp "Port [$DEFAULT_PORT]: " PORT
PORT=${PORT:-$DEFAULT_PORT}

read -rp "DNS domain (e.g. tab.example.com): " DOMAIN
if [[ -z "$DOMAIN" ]]; then
    echo "❌ Domain is required"
    exit 1
fi

read -rp "App username: " APP_USER
read -rsp "App password: " APP_PASS
echo ""

### Dependencies
echo "📦 Installing dependencies"
apt update
apt install -y openjdk-17-jre-headless nginx certbot python3-certbot-nginx unzip curl ufw

### Create dirs
mkdir -p "$INSTALL_DIR"
mkdir -p "$INSTALL_DIR/web"

### Download release artifacts
TMP_DIR=$(mktemp -d)
echo "⬇️ Downloading latest release"

curl -L "https://github.com/$GITHUB_REPO/releases/latest/download/$JAR_NAME" -o "$TMP_DIR/$JAR_NAME"
curl -L "https://github.com/$GITHUB_REPO/releases/latest/download/$WEB_NAME" -o "$TMP_DIR/$WEB_NAME"

### Install files
echo "📂 Installing files"
cp "$TMP_DIR/$JAR_NAME" "$INSTALL_DIR/tab.jar"
rm -rf "$INSTALL_DIR/web/*"
unzip -q "$TMP_DIR/$WEB_NAME" -d "$INSTALL_DIR/web"

### Firewall (UFW)
echo "🔥 Configuring firewall (ufw)"

# Allow SSH so we don't lock user out
if ufw status | grep -q inactive; then
    echo "🔓 Allowing SSH before enabling firewall"
    ufw allow OpenSSH
fi

# Allow HTTP + HTTPS
ufw allow 80/tcp
ufw allow 443/tcp

# Enable ufw if not already enabled
if ufw status | grep -q inactive; then
    echo "🛡️ Enabling firewall"
    ufw --force enable
else
    echo "🛡️ Firewall already enabled"
fi

### Systemd service
echo "⚙️ Creating systemd service"
cat > /etc/systemd/system/${SERVICE_NAME}.service <<EOF
[Unit]
Description=DCM Tab on Port $PORT
After=network.target
StartLimitIntervalSec=0

[Service]
Type=simple
Restart=always
RestartSec=1
ExecStart=/usr/bin/java -jar $INSTALL_DIR/tab.jar $PORT $APP_USER $APP_PASS $INSTALL_DIR/web https://$DOMAIN

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reexec
systemctl daemon-reload
systemctl enable ${SERVICE_NAME}.service

### Nginx HTTPS config
echo "🌐 Configuring nginx"

cat > /etc/nginx/sites-available/$DOMAIN.conf <<EOF
server {
    listen 80;
    server_name $DOMAIN;
    return 301 https://\$host\$request_uri;
}

server {
    listen 443 ssl;
    server_name $DOMAIN;

    location / {
        proxy_pass http://127.0.0.1:$PORT;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
    }
}
EOF

ln -sf /etc/nginx/sites-available/$DOMAIN.conf /etc/nginx/sites-enabled/$DOMAIN.conf
rm -f /etc/nginx/sites-enabled/default

nginx -t
systemctl reload nginx

### Certbot
echo "🔐 Obtaining TLS certificate"
certbot --nginx -d "$DOMAIN"

systemctl enable certbot.timer
systemctl start certbot.timer

### Start services
echo "▶️ Starting services"
systemctl restart nginx
systemctl restart ${SERVICE_NAME}.service

echo ""
echo "✅ Installation complete!"
echo "🌍 https://$DOMAIN"
echo "📦 Install dir: $INSTALL_DIR"
echo "⚙️ Service: ${SERVICE_NAME}.service"