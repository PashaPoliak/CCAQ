# Domain 1: Agentic Architecture & Orchestration (27%)

---

## Card 1.1: Agentic Loop Control

### Question
What determines when to continue vs stop an agentic loop?

### Answer
**Check the `stop_reason` field:**
- Continue when `stop_reason === "tool_use"`
- Stop when `stop_reason === "end_turn"`

### Key Concept: Anti-patterns to Avoid
> Avoid parsing natural language signals, using arbitrary iteration caps as primary stopping mechanism, or checking assistant text content for completion indicators.

```mermaid
flowchart TD
    A[Call Claude API] --> B{stop_reason?}
    B -->|tool_use| C[Execute Requested Tools]
    C --> D[Append Tool Results]
    D --> A
    B -->|end_turn| E[Present Final Response]
    B -->|max_tokens| F[Error: Incomplete]
```

---

## Card 1.2: Programmatic Prerequisites

### Question
How do you implement a programmatic prerequisite for critical business rules?

### Answer
**Use hooks to enforce deterministic compliance:**
- Block downstream tool calls until prerequisites complete (e.g., block `process_refund` until `get_customer` returns verified ID)
- Implement PostToolUse hooks for data normalization
- Intercept tool calls to block policy violations (e.g., refunds above $500)

### Key Concept
> Prompt-based enforcement has non-zero failure rate. Use hooks when deterministic compliance is required for critical operations.

```mermaid
flowchart LR
    subgraph "Before: Prompt-Based"
        A[Customer Request] --> B[LLM decides]
        B --> C{Call refund?}
        C -->|sometimes| D[process_refund]
        C -->|sometimes| E[get_customer]
    end

    subgraph "After: Hook-Based"
        F[Customer Request] --> G[Hook Intercept]
        G --> H{Verified ID?}
        H -->|No| I[Block & Escalate]
        H -->|Yes| J[process_refund]
    end
```

---

## Card 1.3: Hub-and-Spoke Architecture

### Question
What is the hub-and-spoke architecture in multi-agent systems?

### Answer
**Coordinator manages all communication:**
- Coordinator agent handles all inter-subagent communication, error handling, and information routing
- Subagents operate with isolated context—they don't inherit coordinator's conversation history automatically
- Coordinator decomposes tasks, delegates, aggregates results, and selects which subagents to invoke

### Key Concept: Context Passing
> Include complete findings from prior agents directly in the subagent's prompt. Use structured data formats to separate content from metadata (source URLs, document names, page numbers).

```mermaid
flowchart TD
    C[Coordinator Agent] -->|"Task: Search web<br/>Context: query + constraints"| S1[Web Search Agent]
    C -->|"Task: Analyze doc<br/>Context: query + constraints"| S2[Document Analysis Agent]
    C -->|"Task: Synthesize<br/>Context: search results + analysis"| S3[Synthesis Agent]
    
    S1 -->|"Results: [structured data]"| C
    S2 -->|"Results: [structured data]"| C
    S3 -->|"Final Report"| C
    
    style C fill:#e07b53,stroke:#fff,stroke-width:2px
    style S1 fill:#53a5e0
    style S2 fill:#53a5e0
    style S3 fill:#53a5e0
```

---

## Card 1.4: Parallel Subagent Execution

### Question
How do you spawn parallel subagents?

### Answer
**Emit multiple Task tool calls in a single coordinator response:**
- Include "Task" in allowedTools for the coordinator
- Spawn parallel subagents by emitting multiple Task calls in one response rather than across separate turns
- Each subagent receives explicit context in its prompt

### Example
When researching a topic, spawn web search, document analysis, and data extraction agents simultaneously from a single coordinator turn.

```mermaid
sequenceDiagram
    participant C as Coordinator
    participant S1 as Web Search
    participant S2 as Doc Analysis
    participant S3 as Data Extract

    C->>S1: Task (parallel)
    C->>S2: Task (parallel)
    C->>S3: Task (parallel)
    
    S1-->>C: Results
    S2-->>C: Results
    S3-->>C: Results
    
    Note right of C: Single turn emits<br/>all 3 Task calls
    Note left of S1: Execute in parallel<br/>vs sequential
```

---

## Card 1.5: Task Decomposition Patterns

### Question
What's the difference between prompt chaining and dynamic decomposition?

### Answer
**Use appropriate pattern for the workflow:**
- **Prompt chaining:** Fixed sequential pipelines for predictable multi-aspect reviews (e.g., analyze each file individually, then cross-file integration pass)
- **Dynamic decomposition:** Adaptive plans that generate subtasks based on intermediate findings for open-ended investigation

### Key Concept
> **Code review:** Use prompt chaining (per-file analysis → cross-file integration). **Legacy codebase testing:** Use dynamic decomposition (map structure → identify high-impact areas → create prioritized adaptive plan).

```mermaid
flowchart TD
    subgraph "Prompt Chaining (Fixed)"
        A[Analyze File 1] --> B[Analyze File 2]
        B --> C[Analyze File 3]
        C --> D[Cross-File Integration Pass]
    end

    subgraph "Dynamic Decomposition (Adaptive)"
        E[Map Codebase] --> F{Discover Areas}
        F -->|High Impact| G[Create Tests]
        F -->|Dependencies| H[Analyze Dependencies]
        F -->|Edge Cases| I[Special Tests]
        G --> J{More to discover?}
        H --> J
        I --> J
        J -->|Yes| F
        J -->|No| K[Final Plan]
    end
```

---

## Card 1.6: Session Management

### Question
How do you manage session state and resumption?

### Answer
**Session management strategies:**
- Use `--resume <session-name>` to continue specific prior conversations
- Use `fork_session` to create independent branches from shared analysis baseline
- Start fresh with injected summaries when prior tool results are stale rather than resuming with stale context

### Best Practice
> Inform a resumed session about specific file changes for targeted re-analysis rather than requiring full re-exploration.

```mermaid
timeline
    title Session Management Options
    
    section Original Session
        Initial Analysis : Read codebase
                         : Findings stored
                         
    section Resume Session
        --resume project1 : Continue from prior
                          : Same context
                          
    section Fork Session
        fork_session branch1 : Same baseline
                             : Different approach
        fork_session branch2 : Same baseline
                             : Another approach
```

---

## Card 1.7: Multi-Concern Requests

### Question
What's the most reliable way to handle multi-concern customer requests?

### Answer
**Decompose and handle in parallel:**
- Decompose multi-concern requests into distinct items
- Investigate each in parallel using shared context
- Synthesize unified resolution

### Key Concept
> Response validation detects incomplete responses and automatically re-prompts the agent to address missed concerns. This is more effective than preprocessing layers or few-shot examples alone.

```mermaid
flowchart TD
    A["Customer: Refund order #1234<br/>+ Update shipping for #5678"] --> B[Parse Concerns]
    
    B --> C[Concern 1: Refund]
    B --> D[Concern 2: Update Shipping]
    
    C -->|Parallel| E[get_customer #1234]
    D -->|Parallel| F[get_customer #5678]
    
    E --> G[process_refund]
    F --> H[update_shipping]
    
    G --> I[Validate Both Complete]
    H --> I
    
    I --> J{All concerns<br/>addressed?}
    J -->|No| K[Re-prompt for missing]
    J -->|Yes| L[Unified Response]
    K --> B
```

---

## Card 1.8: Handoff Summaries

### Question
How should you structure handoff summaries for escalation?

### Answer
**Include structured context for human agents:**
- Customer ID and relevant identifiers
- Root cause analysis
- Refund amounts or relevant values
- Recommended actions
- What has already been attempted

### Why Structured?
> Human agents may lack access to the conversation transcript. Structured handoff summaries enable them to take over effectively.

```mermaid
flowchart LR
    subgraph "Poor Handoff"
        A[Escalate to human] --> B[No context]
        B --> C["Human asks:<br/>'What's this about?'"]
    end

    subgraph "Good Handoff"
        D[Escalate] --> E["Structured Summary:
        Customer ID: C-12345
        Issue: Unauthorized charge
        Amount: $299.99
        Tried: Verified identity
        Recommended: Full refund"]
        E --> F["Human immediately<br/>acts on context"]
    end
```

---

## Card 1.9: Task Decomposition Risks

### Question
What's the risk of overly narrow task decomposition by the coordinator?

### Answer
**Incomplete coverage of broad topics:**
- Coordinator may decompose "creative industries" into only visual arts (digital art, graphic design, photography)
- Completely misses music, writing, and film
- Subagents execute correctly—the problem is what they were assigned

### Solution
> Implement iterative refinement loops where coordinator evaluates synthesis output for gaps and re-delegates with targeted queries until coverage is sufficient.

```mermaid
flowchart TD
    A["Topic: Creative Industries<br/>AI Impact"] --> B[Coordinator Decomposes]
    
    B -->|Narrow| C["Assigns:
    - Digital Art
    - Graphic Design
    - Photography"]
    
    B -->|Proper| D["Assigns:
    - Digital Art
    - Music
    - Writing
    - Film
    - Photography"]
    
    C --> E[Report: Only Visual Arts]
    D --> F[Report: Comprehensive]
    
    E --> G["Gap Analysis:
Missing Music, Writing, Film"]
    G --> H[Re-delegate for gaps]
    H --> I[Complete Report]
```
