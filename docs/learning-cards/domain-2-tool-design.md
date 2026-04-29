# Domain 2: Tool Design & MCP Integration (18%)

---

## Card 2.1: Effective Tool Descriptions

### Question
What makes an effective tool description?

### Answer
**Clear differentiation and boundaries:**
- Expected input formats and example queries
- Edge cases and boundary explanations
- When to use this tool versus similar alternatives

### Common Mistake
> Minimal descriptions like "Retrieves customer information" lead to unreliable selection. Ambiguous descriptions cause misrouting (e.g., analyze_content vs analyze_document with near-identical descriptions).

```mermaid
flowchart TD
    subgraph "Poor Descriptions"
        A["get_customer:
        'Gets customer info'"] 
        B["lookup_order:
        'Gets order info'"]
        C[LLM confused] --> D[Wrong tool selection]
    end

    subgraph "Good Descriptions"
        E["get_customer:
        Input: email/phone/customer_id
        Use when: Need account details
        Not for: Order status/shipping"] 
        F["lookup_order:
        Input: order_id only
        Use when: Tracking orders
        Not for: Account management"]
        G[Clear boundaries] --> H[Correct routing]
    end
```

---

## Card 2.2: Structured Error Responses

### Question
How do you handle structured error responses in MCP tools?

### Answer
**Return structured error metadata:**
- `errorCategory`: transient / validation / permission / business
- `isRetryable`: boolean flag
- Human-readable descriptions
- Partial results and what was attempted

### Why It Matters
> Generic "Operation failed" responses prevent the agent from making appropriate recovery decisions. Distinguish access failures (need retry) from valid empty results (successful query with no matches).

```mermaid
flowchart TD
    A[Tool Error] --> B{Category?}
    
    B -->|transient| C["isRetryable: true
    Action: Retry with backoff"]
    
    B -->|validation| D["isRetryable: false
    Action: Fix input"]
    
    B -->|business| E["isRetryable: false
    Action: Explain to user"]
    
    B -->|permission| F["isRetryable: false
    Action: Escalate"]
    
    B -->|empty result| G["isRetryable: N/A
    Action: Proceed normally"]
```

---

## Card 2.3: Optimal Tool Count

### Question
What's the optimal number of tools per agent?

### Answer
**Limit to 4-5 relevant tools per agent:**
- Too many tools (e.g., 18 instead of 4-5) degrades selection reliability by increasing decision complexity
- Agents with tools outside their specialization tend to misuse them
- Provide scoped cross-role tools for high-frequency needs

### Example
> Synthesis agent shouldn't have web search tools, but can have a scoped verify_fact tool for simple lookups.

```mermaid
flowchart LR
    subgraph "Too Many Tools"
        A[Synthesis Agent] --> B["18 tools:
- web_search
- analyze_pdf
- fetch_url
- summarize
- synthesize
- fact_check
- ...13 more"]
        B --> C[Decision overload]
        C --> D[Misuses web_search]
    end

    subgraph "Scoped Tools"
        E[Synthesis Agent] --> F["4 tools:
- synthesize_findings
- format_report
- verify_fact (scoped)
- check_citations"]
        F --> G[Focused behavior]
    end
```

---

## Card 2.4: Tool Choice Configuration

### Question
What are the tool_choice configuration options?

### Answer
**Control tool selection behavior:**
- `"auto"`: Model may return text instead of calling a tool
- `"any"`: Model must call a tool but can choose which (guarantees structured output)
- `{"type": "tool", "name": "..."}`: Force specific tool selection

### Use Cases
> Use "any" to guarantee structured output. Use forced selection to ensure metadata extraction runs before enrichment steps.

```mermaid
flowchart TD
    A[Configure tool_choice] --> B{Goal?}
    
    B -->|Flexible| C["auto
Model decides tool or text"]
    
    B -->|Guarantee tool| D["any
Must call any tool"]
    
    B -->|Specific first| E["forced: extract_metadata
Enforce sequence"]
    
    C --> F["Use case:
Conversational agent"]
    D --> G["Use case:
Structured extraction"]
    E --> H["Use case:
Metadata before enrichment"]
```

---

## Card 2.5: MCP Server Scoping

### Question
Where should MCP servers be configured?

### Answer
**Scope appropriately:**
- **Project-level (.mcp.json):** Shared team tooling, version-controlled
- **User-level (~/.claude.json):** Personal/experimental servers
- Environment variable expansion for credentials: `${GITHUB_TOKEN}`

### Best Practice
> Choose existing community MCP servers over custom implementations for standard integrations (e.g., Jira). Reserve custom servers for team-specific workflows.

```mermaid
flowchart TD
    subgraph "MCP Server Configuration"
        A[Server Types] --> B["Project-level
.mcp.json"]
        A --> C["User-level
~/.claude.json"]
        
        B --> D["Shared with team
Version controlled
Production tools"]
        
        C --> E["Personal only
Experimental
Custom dev tools"]
        
        F[Credentials] --> G["Environment vars:
${GITHUB_TOKEN}
${API_KEY}"]
    end
```

---

## Card 2.6: MCP Resources vs Tools

### Question
When should you use MCP resources vs tools?

### Answer
**Different purposes:**
- **Resources:** Expose content catalogs (issue summaries, documentation hierarchies, database schemas) to reduce exploratory tool calls
- **Tools:** Actions that perform operations

### Resource Example
> Expose a "documentation_index" resource so the agent can see available docs without calling multiple list/search tools.

```mermaid
flowchart LR
    subgraph "Without Resources"
        A[Need doc info] --> B[list_docs tool]
        B --> C[Returns list]
        C --> D[get_doc for each]
        D --> E[Multiple tool calls]
    end

    subgraph "With Resources"
        F[Need doc info] --> G[Documentation Index Resource]
        G --> H[Available docs visible
in context]
        H --> I[Direct access]
    end
```

---

## Card 2.7: Built-in Tool Selection

### Question
How do you select between built-in tools (Read, Write, Edit, Bash, Grep, Glob)?

### Answer
**Use the right tool for the task:**
- **Grep:** Search file contents for patterns (function names, error messages, imports)
- **Glob:** Find files by name/extension patterns (`**/*.test.tsx`)
- **Edit:** Targeted modifications with unique text matching
- **Read + Write:** Fallback when Edit can't find unique anchor text

### Exploration Pattern
> Start with Grep to find entry points, then Read to follow imports and trace flows—rather than reading all files upfront.

```mermaid
flowchart TD
    A[Explore Codebase] --> B[Grep: find function]
    B --> C[Read: entry point file]
    C --> D{Need to modify?}
    
    D -->|Unique match| E["Edit:
Precise change"]
    D -->|Multiple matches| F["Read + Write:
Fallback approach"]
    
    C --> G[Follow imports]
    G --> H[Read next file]
    H --> I{Understand flow?}
    I -->|No| G
    I -->|Yes| J[Complete]
```
