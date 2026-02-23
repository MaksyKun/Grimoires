# GitHub Copilot Instructions for Grimoires

## Project Overview

**Grimoires** is a Paper/Spigot Minecraft plugin (targeting API 1.20.1) written in Java 16. It extends Minecraft's built-in book system to its limits by providing:

- **Chiseled Bookshelves as interactive GUIs** – Players can browse and pick books from a shelf through an inventory interface.
- **Book Publishing System** – Players can write, publish, and sell books with configurable pricing tiers. Multiple authors per book are supported.
- **Genre Classification** – Books are organized by custom genres defined in `Features/Genres.yml`.
- **Mystery / Decryption System** – Books can contain encrypted puzzles that players must solve to earn rewards.
- **Database Persistence** – Supports both SQLite and MySQL via HikariCP connection pooling.
- **Economy Integration** – Hooks into Vault for purchase flows, HeadDatabase for custom skull textures, and ProtocolLib for packet-level interactions.

### Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 16 |
| Build | Maven (maven-compiler-plugin, maven-shade-plugin) |
| Server API | Paper 1.20.1 (`io.papermc.paper:paper-api`) |
| Database | HikariCP 5.1.0 (SQLite / MySQL) |
| Utilities | Lombok 1.18.30 |
| Optional hooks | Vault API 1.7, HeadDatabase-API 1.3.1, ProtocolLib 5.x |

### Source Layout

```
src/main/java/net/maksy/grimoires/
├── Grimoires.java                  # Main plugin entry point
├── commands/                       # Command executors
├── configuration/                  # Config loading, YAML, SQL, i18n, permissions
│   ├── sql/                        # SQLManager, BooksSQL, MysteriesSQL
│   └── translation/                # TranslationConfig
├── modules/
│   ├── shelves/                    # Chiseled-bookshelf GUI module
│   ├── book_management/            # Publication workflow, storage, genres
│   │   ├── storage/
│   │   └── publication/
│   ├── mysteries/                  # Encryption/decryption puzzle system
│   └── api/                        # Event API hooks for third-party plugins
├── hooks/                          # Vault, HeadDatabase integrations
└── utils/                          # ChatUT, ItemUT, InventoryUT, FileUT, etc.

src/main/resources/
├── plugin.yml
├── Config.yml
├── GrimoireDesign.yml
├── Translations.yml
└── Features/                       # Per-feature configuration files
```

## Coding Conventions

- Follow the existing package structure under `net.maksy.grimoires`.
- Use **Lombok** annotations (`@Getter`, `@Setter`, `@RequiredArgsConstructor`, etc.) instead of boilerplate getters/setters.
- Interact with players using the **Adventure** text component API (already pulled in via Paper).
- Keep GUI/inventory logic inside the `modules/` subtree; pure data classes belong in `storage/` or `configuration/`.
- SQL access must go through the existing `SQLManager` and typed DAO classes (`BooksSQL`, `MysteriesSQL`).
- New configuration keys should be added to the appropriate YAML file under `src/main/resources/` and read via `YamlParser` or `Config`.
- Keep Vault/HeadDatabase/ProtocolLib usage behind null-checks on the hook objects provided by `HookManager`.
- Match the existing style: 4-space indentation, `camelCase` for variables, `PascalCase` for classes.

## Repository Folders for AI Assistance

This repository uses three dedicated folders under `.github/` to organize AI-generated artefacts. Always respect these locations:

### `.github/skills/`
Contains **skill definitions** — reusable knowledge units that describe how specific parts of Grimoires work or how common tasks should be performed (e.g., "How to add a new module", "How to register a new command"). When you identify a repeatable pattern or best practice specific to this codebase, document it as a skill file here.

### `.github/docs/`
Contains **generated documentation** produced by Copilot. When asked to document a class, module, feature, or API, write the resulting Markdown file into this folder. Use clear, descriptive file names (e.g., `publication-module.md`, `mystery-system.md`).

### `.github/suggestions/`
Contains **suggestion artefacts** — analysis reports, refactoring proposals, architectural recommendations, or any open-ended improvements requested by maintainers. Each suggestion should be its own Markdown file with a short descriptive name (e.g., `improve-sql-layer.md`, `add-economy-expansion.md`).

## Key Guidelines for Copilot

1. **Read before writing** – Before generating new code, check the existing module structure and conventions so the output integrates cleanly.
2. **Minimal changes** – Prefer targeted, surgical edits over large rewrites. Only touch files that are necessary for the requested change.
3. **Backwards compatibility** – Do not change existing config keys or SQL schema without explicitly updating migration logic and documentation.
4. **Document outputs** – Non-trivial suggestions or documentation artefacts must be written to the appropriate `.github/` subfolder listed above.
5. **Test with Paper API in mind** – The plugin runs on a Paper server; avoid relying on Bukkit-only APIs when Paper equivalents exist.
6. **Dependency hygiene** – Do not add Maven dependencies without first checking for security advisories and ensuring compatibility with Java 16 and Paper 1.20.1.
