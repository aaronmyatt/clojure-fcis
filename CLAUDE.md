# FCIS Project Conventions

This is a Clojure project following the **Functional Core, Imperative Shell** (FCIS) pattern. It is structured as a monorepo with three layers, designed for productive human-LLM collaboration.

## Architecture

```
Core (pure)  →  Adapter (side effects)  →  App (wiring)
```

- **Core** (`modules/core/`): Pure functions only. No IO, no atoms, no side effects. Business logic, validation, data transformation.
- **Adapter** (`modules/adapter/`): Side-effectful code. File I/O, HTTP, databases. Depends on Core. Takes dependencies as function arguments.
- **App** (`modules/app/`): Orchestration layer. Wires Core + Adapter together. Entry points for HTTP servers, CLIs, frontends.
- **Shared** (`shared/`): Cross-cutting utilities. CLI runner and common schemas.

## Key Rules

1. **Every public function** must have a Malli schema via `(m/=> fn-name [:=> ...])` after its definition
2. **Every public function** must have a docstring
3. **Core functions must be pure** — no IO, no atoms, no `def` mutation, no side effects
4. **Adapter functions** take dependencies as arguments (e.g., `store-dir`) — no globals
5. **Prefer maps** over positional arguments for functions with 3+ parameters
6. **Every source file** ends with a `(comment ...)` block containing 2-3 REPL-evaluable examples
7. **Validations** return result maps `{:valid? bool :reason str}` — not exceptions

## Adding a New Function

1. Define the function with a docstring
2. Add `(m/=> fn-name [:=> [:cat <input-schemas>] <output-schema>])` after it
3. Add a `(comment ...)` example at the end of the file
4. Write tests in the corresponding `test/` directory
5. The function automatically appears in the CLI: `bb core:cli list`

## Testing

- Core: unit tests + property-based tests using `test.check` and Malli generators
- Adapter: integration tests with temp directory fixtures
- App: smoke/end-to-end tests
- Run all: `bb test:all`
- Run per-module: `bb test:core`, `bb test:adapter`, `bb test:app`

## CLI Discovery

Each layer has a CLI for discovering and invoking functions:

```bash
bb core:cli list                                    # List all Core functions
bb core:cli describe fcis.core.user/validate-email  # Show schema and docstring
bb core:cli run fcis.core.user/validate-email '"test@example.com"'  # Invoke with EDN args
bb adapter:cli list                                 # List Adapter functions
bb app:cli list                                     # List App functions
```

## Schemas

- **Shared schemas** (used across modules): `shared/src/fcis/schemas/common.clj`
- **Domain schemas** (module-specific): `modules/<module>/src/fcis/<module>/schemas.clj`
- Schemas are plain EDN data — vectors and maps. Reference them in function schemas.

## File Placement

| What | Where |
|------|-------|
| Pure business logic | `modules/core/src/fcis/core/` |
| Domain schemas | `modules/core/src/fcis/core/schemas.clj` |
| Side-effectful code | `modules/adapter/src/fcis/adapter/` |
| Application wiring | `modules/app/src/fcis/app/` |
| Cross-cutting schemas | `shared/src/fcis/schemas/common.clj` |
| CLI runner (don't modify) | `shared/src/fcis/cli/runner.clj` |
| Tests | `modules/<module>/test/fcis/<module>/` |

## Common Tasks

```bash
bb tasks          # Show all available tasks
bb test:all       # Run all tests
bb test:core      # Run Core tests only
bb core:cli list  # Discover Core functions
bb nrepl           # Start nREPL with all modules
bb clean          # Remove build artifacts
bb deps           # Download all dependencies
```
