# Domain 3: Claude Code Configuration & Workflows (20%)

---

## Card 3.1: CLAUDE.md Hierarchy

### Question
What's the CLAUDE.md configuration hierarchy?

### Answer
**Configuration levels (highest priority last):**
- **User-level:** `~/.claude/CLAUDE.md` — personal preferences, not shared
- **Project-level:** `.claude/CLAUDE.md` or root `CLAUDE.md` — team standards via version control
- **Directory-level:** Subdirectory `CLAUDE.md` files — specific conventions

### Diagnostic
> Use `/memory` command to verify which memory files are loaded and diagnose inconsistent behavior.

```mermaid
flowchart TD
    subgraph "CLAUDE.md Hierarchy"
        direction TB
        
        A["User-level
~/.claude/CLAUDE.md
(Personal)"] 
        
        B["Project-level
.claude/CLAUDE.md
(Team Shared)"]
        
        C["Directory-level
subdir/CLAUDE.md
(Specific)"]
        
        A --> D[Lower Priority]
        B --> E[Medium Priority]
        C --> F[Higher Priority]
        
        G[/memory command/] --> H[Check loaded files]
    end
```

---

## Card 3.2: Topic-Specific Rules

### Question
How do you organize topic-specific rules?

### Answer
**Use .claude/rules/ directory:**
- Files with YAML frontmatter containing glob patterns for conditional activation
- `paths: ["terraform/**/*"]` — loads only when editing matching files
- Alternative to monolithic CLAUDE.md

### Benefits
> Path-scoped rules load only when needed, reducing irrelevant context and token usage. Glob patterns apply conventions to files by type regardless of directory location.

```mermaid
flowchart LR
    subgraph ".claude/rules/"
        A[api-conventions.md] -->|"paths: ['**/api/**']"| B[API files only]
        
        C[testing.md] -->|"paths: ['**/*.test.*']"| D[Test files anywhere]
        
        E[terraform.md] -->|"paths: ['terraform/**']"| F[Terraform only]
    end

    G[Edit file] --> H{Matches pattern?}
    H -->|Yes| I[Rule loaded]
    H -->|No| J[Rule skipped]
```

---

## Card 3.3: Slash Command Scope

### Question
Where should custom slash commands be created?

### Answer
**Scope by sharing needs:**
- **Project-scoped:** `.claude/commands/` — shared via version control, available to all developers
- **User-scoped:** `~/.claude/commands/` — personal commands

### Note
> CLAUDE.md is for project instructions and context, not command definitions. There is no .claude/config.json with commands array.

```mermaid
flowchart TD
    A[Create /review command] --> B{Team or Personal?}
    
    B -->|Team| C[".claude/commands/review
.gitignore: don't ignore
Everyone gets it"]
    
    B -->|Personal| D["~/.claude/commands/my-review
Only for you
Private shortcuts"]
    
    E[Wrong locations] --> F["CLAUDE.md ❌
config.json ❌"]
```

---

## Card 3.4: SKILL.md Frontmatter

### Question
What's the SKILL.md frontmatter configuration?

### Answer
**Available frontmatter options:**
- `context: fork` — runs in isolated sub-agent, prevents output pollution
- `allowed-tools` — restricts tool access during execution
- `argument-hint` — prompts for required parameters

### Use Cases
> Use `context: fork` for skills producing verbose output (codebase analysis) or exploratory context (brainstorming).

```mermaid
flowchart TD
    A[SKILL.md Frontmatter] --> B
    B --> C["context: fork
Isolated execution"]
    B --> D["allowed-tools: [Read, Write]
Restricted access"]
    B --> E["argument-hint: 'Enter path'
User prompt"]
    
    C --> F["Use for:
- Codebase analysis
- Verbose exploration"]
    
    D --> G["Use for:
- Safe code review
- Non-destructive ops"]
    
    E --> H["Use for:
- Dynamic input
- Required params"]
```

---

## Card 3.5: Plan Mode vs Direct Execution

### Question
When should you use plan mode vs direct execution?

### Answer
**Match mode to task complexity:**
- **Plan mode:** Complex tasks with large-scale changes, architectural decisions, multiple valid approaches, multi-file modifications
- **Direct execution:** Simple, well-scoped changes (single-file bug fix, clear stack trace)

### Hybrid Approach
> Use plan mode for investigation, then direct execution for implementation (e.g., planning a library migration, then executing the planned approach).

```mermaid
flowchart TD
    A[New Task] --> B{Complexity?}
    
    B -->|Simple| C["Direct Execution
- Single file fix
- Clear scope
- Quick change"]
    
    B -->|Complex| D["Plan Mode
- Multi-file changes
- Architecture decisions
- Unknown dependencies"]
    
    D --> E[Explore subagent]
    E --> F[Gather findings]
    F --> G[Design approach]
    G --> H["Direct Execution
Implementation"]
```

---

## Card 3.6: Explore Subagent

### Question
What's the Explore subagent used for?

### Answer
**Isolate verbose discovery output:**
- Prevents context window exhaustion during multi-phase tasks
- Returns summaries to preserve main conversation context
- Use during discovery phases before implementation

### Workflow
> Plan mode → Explore subagent for discovery → Summary returned → Direct execution for implementation.

```mermaid
sequenceDiagram
    participant M as Main Session
    participant E as Explore Subagent
    
    M->>E: Explore: Map codebase
    Note over E: Verbose analysis
    Note over E: Find patterns
    Note over E: Trace imports
    
    E-->>M: Summary: Key findings
    Note over M: Clean context
    Note over M: Preserved space
    
    M->>M: Plan implementation
    M->>M: Execute changes
```

---

## Card 3.7: Iterative Refinement

### Question
What are effective iterative refinement techniques?

### Answer
**Progressive improvement strategies:**
- Provide 2-3 concrete input/output examples when prose descriptions are interpreted inconsistently
- Test-driven iteration: write test suites first, then iterate by sharing test failures
- Interview pattern: have Claude ask questions to surface considerations before implementing

### Issue Management
> Address multiple interacting issues in a single detailed message. Fix independent problems sequentially.

```mermaid
flowchart TD
    A[Unclear Requirements] --> B["Add 2-3 examples
Input → Output"]
    
    C[Need implementation] --> D["Write tests first
Then iterate"]
    
    E[Unfamiliar domain] --> F["Interview pattern
Ask questions first"]
    
    G[Multiple issues] --> H{Interact?}
    H -->|Yes| I[Single detailed fix]
    H -->|No| J[Sequential fixes]
```

---

## Card 3.8: CI/CD Integration

### Question
How do you integrate Claude Code into CI/CD pipelines?

### Answer
**Non-interactive execution:**
- Use `-p` or `--print` flag to run in non-interactive mode
- Use `--output-format json` with `--json-schema` for machine-parseable structured findings
- CLAUDE.md provides project context (testing standards, review criteria) to CI

### Session Isolation
> Same session that generated code is less effective at reviewing its own changes. Use independent review instance.

```mermaid
flowchart LR
    subgraph "CI Pipeline"
        A[PR Created] --> B["claude -p
--output-format json
--json-schema schema.json
'Review for security'"]
        
        B --> C[Structured output]
        C --> D[Parse findings]
        D --> E[Post PR comments]
    end
    
    subgraph "Key Flags"
        F["-p / --print
No interactive mode"]
        G["--output-format json
Machine readable"]
        H["--json-schema
Enforce structure"]
    end
```
