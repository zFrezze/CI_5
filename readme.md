<div align="center">

# ⚡ CyberInfra

**A feature-rich survival economy plugin for Paper — tokens, homes, teleports & full multi-language support.**

[![Paper](https://img.shields.io/badge/Paper-1.21-0099ff?style=for-the-badge&logo=minecraft&logoColor=white)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![SQLite](https://img.shields.io/badge/SQLite-local-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Languages](https://img.shields.io/badge/Languages-8-success?style=for-the-badge)](#-languages)

</div>

---

## ✨ Features

- 🪙 **Token economy** — a craftable, depositable currency with a full admin command suite
- 🏠 **Homes** — set, teleport to, and remove personal homes, priced in tokens, with a slick confirmation GUI
- 🌀 **Teleport requests** — `/tpa` and `/tpahere` with clickable accept/deny/cancel, request timeouts, and safe-landing checks
- 🌍 **Per-player languages** — every message is translated, and each player picks their own language via a flag-head GUI
- 💾 **Local persistence** — everything is stored in an embedded SQLite database (WAL mode), no external server required

---

## 🌐 Languages

Each player chooses their own language with `/language` — a GUI of flag heads, or `/language <code>` directly.

|      Code      | Language |
|:--------------:|:---------|
|   🇬🇧 `en`    | English |
|   🇩🇪 `de`    | German |
|   🇦🇹 `at`    | Austrian German *(dialect)* |
|   🇨🇭 `ch`    | Swiss German *(dialect)* |
|   🇫🇷 `fr`    | French |
|   🇪🇸 `es`    | Spanish |
| 🏴‍☠️ `pirate` | Pirate speak *(for fun)* |
|    🐱 `cat`    | LOLcat *(for fun)* |

> The default language is set in `config.yml` (`language:`). New players start with it until they change it.

---

## 🎮 Commands

### 🪙 Tokens

| Command | Description |
|:--------|:------------|
| `/token info <player>` | View a player's token balance |
| `/token add\|remove\|set <player> <amount>` | Manage a player's tokens |
| `/withdraw <amount>` | Withdraw tokens from your balance into physical items |

*Aliases: `/tokens`, `/tk` — `/wd`*

### 🏠 Homes

| Command | Description |
|:--------|:------------|
| `/sethome <name>` | Set a home at your location |
| `/home <name>` | Teleport to one of your homes |
| `/removehome <name>` | Remove a home (refunds tokens) |

### 🌀 Teleporting

| Command | Description |
|:--------|:------------|
| `/tpa <player>` | Ask to teleport **to** a player |
| `/tpahere <player>` | Ask a player to teleport **to you** |
| `/tpaccept <player>` | Accept an incoming request |
| `/tpdeny <player>` | Deny an incoming request |
| `/tpcancel <player>` | Cancel a request you sent |

### 🌍 Language

| Command | Description |
|:--------|:------------|
| `/language` | Open the language selection GUI |
| `/language <code>` | Set your language directly |

*Alias: `/lang`*

---

## 🔑 Permissions

| Permission | Grants | Default |
|:-----------|:-------|:-------:|
| `ci.admin` | Full admin access | `op` |
| `ci.token.use` | Use `/token` and `/withdraw` | `true` |
| `ci.token.admin` | Manage tokens (set/add/remove) | `op` |
| `ci.token.others` | View other players' balances | `op` |
| `ci.withdraw.use` | Withdraw tokens into items | `true` |
| `ci.home.use` | Teleport to your homes | `true` |
| `ci.sethome.use` | Set homes | `true` |
| `ci.removehome.use` | Remove your homes | `true` |
| `ci.home.vip` | Higher VIP home limit | `false` |
| `ci.language.use` | Change your language | `true` |
| `ci.tpa.use` · `ci.tpaccept.use` · `ci.tpahere.use` · `ci.tpdeny.use` · `ci.tpcancel.use` | Teleport requests | `true` |

> Individual languages are gated behind `ci.language.<code>` (e.g. `ci.language.de`) — all default to `true`, so you can restrict specific languages to ranks if you want.

---

## ⚙️ Configuration

All values live in `config.yml`. The essentials:

```yaml
language: 'en'            # default language for new players

homes:
  max-homes:
    vip: 7                # homes for VIP players (ci.home.vip)
    default: 4            # homes for everyone else
  price-set: 25           # cost to set a home
  price-remove: 10        # tokens refunded on removal
  price-teleport: 5       # cost to teleport home

tpa:
  price: 10               # token cost per teleport
```

> 🎨 Skins for the token item, home GUI, and language flags are configurable via texture URLs in `config.yml`.

---

## 📦 Installation

1. Download the latest `CyberInfra.jar` from [Releases](../../releases)
2. Drop it into your server's `plugins/` folder
3. Restart the server *(a full restart, not `/reload`)*
4. Edit the generated `config.yml` to taste, then restart once more

**Requirements:** Paper `1.21+` · Java `21+`

---

## 🛠️ Building from source

```bash
git clone https://github.com/zFrezze/CI_5.git
cd CI_5
mvn clean package
```

The built jar lands in `target/`. SQLite is shaded in — no extra dependencies to install.

---

<div align="center">

Made with ⚡ by **zFrezze**

</div>