# Clojure FCIS Template

A Clojure project template following the **Functional Core, Imperative Shell** (FCIS) pattern, designed for productive human-LLM collaboration.

## Architecture

The project is organized into three layers:

- **Core** — Pure functions: business logic, validation, data transformation. No side effects.
- **Adapter** — Side-effectful code: file I/O, HTTP clients, databases. Depends on Core.
- **App** — Orchestration: wires Core and Adapter together. Entry points for your application.

Each layer has a CLI interface auto-generated from [Malli](https://github.com/metosin/malli) schemas, enabling both humans and LLMs to discover and invoke functions without reading source code.

## Prerequisites

- [Clojure CLI](https://clojure.org/guides/install_clojure) (1.12+)
- [Babashka](https://github.com/babashka/babashka#installation)
- Java 11+

## Quick Start

```bash
# Download dependencies
bb deps

# Run all tests
bb test:all

# Discover what functions are available
bb core:cli list
bb adapter:cli list
bb app:cli list

# Describe a specific function
bb core:cli describe fcis.core.user/validate-email

# Invoke a function from the command line
bb core:cli run fcis.core.user/validate-email '"alice@example.com"'

# Start a REPL with all modules loaded
bb nrepl
```

## Project Structure

```
modules/
  core/       Pure functions, schemas, business logic
  adapter/    Side-effectful integrations
  app/        Application wiring, entry points
shared/       CLI runner, cross-cutting schemas
dev/          REPL helpers
```

## Available Tasks

Run `bb tasks` to see all available commands:

| Task | Description |
|------|-------------|
| `bb test:all` | Run all tests |
| `bb test:core` | Run Core tests only |
| `bb test:adapter` | Run Adapter tests only |
| `bb test:app` | Run App tests only |
| `bb core:cli <cmd>` | CLI for Core functions |
| `bb adapter:cli <cmd>` | CLI for Adapter functions |
| `bb app:cli <cmd>` | CLI for App functions |
| `bb nrepl` | Start nREPL |
| `bb deps` | Download dependencies |
| `bb clean` | Remove build artifacts |

## How It Works

Every public function is annotated with a [Malli](https://github.com/metosin/malli) schema:

```clojure
(defn validate-email
  "Checks if an email address is valid."
  [email]
  ...)

(m/=> validate-email [:=> [:cat :string] [:map [:valid? :boolean] [:reason [:maybe :string]]]])
```

The CLI runner introspects these schemas at runtime to provide discovery (`list`), documentation (`describe`), and invocation (`run`) without any manual CLI maintenance.

## Using This Template

1. Clone or fork this repository
2. Rename the `fcis` namespace prefix to your project name (find-and-replace across all files)
3. Replace the example user domain with your actual business logic
4. Keep the three-layer structure and schema conventions

## LLM Collaboration

This template is designed for human-LLM pair programming. See `CLAUDE.md` for conventions that both parties follow. Key principles:

- Small, pure functions with schemas create a clear contract
- The CLI provides a neutral discovery interface for both parties
- Rich `(comment ...)` blocks serve as inline documentation
- Separate test files catch regressions automatically
