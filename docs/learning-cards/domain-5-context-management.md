# Domain 5: Context Management & Reliability (15%)

---

## Card 5.1: Progressive Summarization Risks

### Question
What are the risks of progressive summarization?

### Answer
**Fundamental information loss:**
- Condensing numerical values, percentages, dates into vague summaries
- "Discussed promotional pricing" loses the specific 15% discount mentioned
- Cannot reliably "prompt your way out" of information loss

### Solution
> Extract transactional facts (amounts, dates, order numbers) into a persistent "case facts" block included in each prompt, outside summarized history.

```mermaid
flowchart TD
    subgraph "Summarization Problem"
        A["Turn 1: 'Need 15% discount'"]
        B["Turn 20: 'As I mentioned...'"]
        
        A --> C["Summary: 'Discussed promo'"]
        B --> D[Can't find 15%]
    end

    subgraph "Case Facts Solution"
        E["Case Facts Block:
- Discount: 15%
- Order: #12345
- Date: 2024-01-15"]
        
        F["Turn 20 Query:
'Discount amount?'"]
        
        E --> G["Answer: 15%"]
        F --> G
    end
```

---

## Card 5.2: Lost in the Middle Effect

### Question
What's the 'lost in the middle' effect?

### Answer
**Position-based attention degradation:**
- Models reliably process information at beginning and end of long inputs
- May omit findings from middle sections
- Affects long documents and multi-turn conversations

### Mitigation
> Place key findings summaries at beginning of aggregated inputs. Organize detailed results with explicit section headers.

```mermaid
flowchart LR
    subgraph "Input Structure"
        A["1. Key Findings Summary"] --> HIGH["← High Attention"]
        B["2. Detailed Results A"] --> MED["Medium"]
        C["3. Detailed Results B"] --> LOW["← Low Attention"]
        D["4. Detailed Results C"] --> MED2["Medium"]
        E["5. Conclusion & Next Steps"] --> HIGH2["← High Attention"]
    end
```

---

## Card 5.3: Escalation Triggers

### Question
When should you escalate to human agents?

### Answer
**Appropriate escalation triggers:**
- Customer explicitly requests a human (honor immediately)
- Policy exceptions/gaps (not just complex cases)
- Inability to make meaningful progress

### Anti-patterns
> Sentiment-based escalation and self-reported confidence scores are unreliable proxies for case complexity. Multiple customer matches require clarification (request additional identifiers) rather than heuristic selection.

```mermaid
flowchart TD
    A{Escalate?} --> B["Customer asks for human → YES
(Honor immediately)"]
    
    A --> C["Policy gap/silent → YES
(Need human judgment)"]
    
    A --> D["Stuck/no progress → YES"]
    
    A --> E["Complex but clear → NO
(Handle autonomously)"]
    
    A --> F["Low confidence → NO
(Poor proxy - unreliable)"]
    
    A --> G["Negative sentiment → NO
(Different problem)"]
```

---

## Card 5.4: Error Propagation

### Question
How do you propagate errors in multi-agent systems?

### Answer
**Structured error context enables recovery:**
- Include failure type, attempted query, partial results, alternative approaches
- Distinguish access failures (need retry) from valid empty results
- Implement local recovery within subagents before propagating

### Anti-patterns
> Avoid generic error statuses ("search unavailable") that hide context. Don't silently suppress errors (return empty as success) or terminate entire workflows on single failures.

```mermaid
flowchart TD
    A[Subagent Error] --> B{Can resolve locally?}
    
    B -->|Yes| C[Local retry/recovery]
    B -->|No| D["Propagate to coordinator:
- Error type
- Attempted query
- Partial results
- Alternatives tried"]
    
    D --> E[Coordinator decides:
Retry / Escalate / Proceed]
    
    F["❌ Generic 'Failed'"] --> G[No recovery possible]
    H["❌ Return empty"] --> I[Incomplete output]
    J["❌ Terminate all"] --> K[Unnecessary failure]
```

---

## Card 5.5: Large Codebase Context

### Question
How do you manage context in large codebase exploration?

### Answer
**Combat context degradation:**
- Spawn subagents to investigate specific questions while main agent coordinates
- Maintain scratchpad files recording key findings
- Summarize findings before spawning sub-agents for next phase
- Use `/compact` during extended exploration

### Crash Recovery
> Design structured agent state exports (manifests) that coordinator loads on resume and injects into agent prompts.

```mermaid
flowchart TD
    A[Extended Exploration] --> B[Context fills up]
    B --> C[Inconsistent answers]
    
    A --> D["Solutions:
- Subagent delegation
- Scratchpad files
- /compact command"]
    
    D --> E["Crash Recovery:
Export state to manifest"]
    E --> F["Resume:
Load manifest"]
    F --> G[Continue from checkpoint]
```

---

## Card 5.6: Human Review Workflows

### Question
How do you design human review workflows?

### Answer
**Calibrated review routing:**
- Implement stratified random sampling of high-confidence extractions for ongoing error rate measurement
- Analyze accuracy by document type and field before reducing human review
- Output field-level confidence scores and calibrate thresholds using labeled validation sets

### Risk
> Aggregate accuracy metrics (97% overall) may mask poor performance on specific document types or fields.

```mermaid
flowchart LR
    subgraph "Review Routing"
        A[Extraction with confidence] --> B{Confidence > Threshold?}
        
        B -->|High| C["Stratified sampling
5% reviewed for quality"]
        
        B -->|Low| D["Route to human
Priority queue"]
        
        B -->|Medium| E["Automated checks
Then review"]
    end
    
    subgraph "Accuracy Analysis"
        F["By Document Type:
- Invoices: 99%
- Receipts: 85% ⚠️
- Contracts: 98%"]
        
        G["By Field:
- Total: 99%
- Tax: 92%
- Vendor: 78% ⚠️"]
    end
```

---

## Card 5.7: Information Provenance

### Question
How do you preserve information provenance in synthesis?

### Answer
**Maintain claim-source mappings:**
- Require subagents to output structured claim-source mappings (source URLs, document names, excerpts)
- Include publication/collection dates to prevent temporal differences being misinterpreted as contradictions
- Annotate conflicting statistics with source attribution rather than arbitrarily selecting one

### Output Structure
> Distinguish well-established findings from contested ones in reports. Preserve original source characterizations and methodological context.

```mermaid
flowchart TD
    subgraph "Provenance Tracking"
        A[Web Search Agent] -->|"Claim: AI market $100B
Source: techreport.com
Date: 2024-01"| C
        
        B[Document Agent] -->|"Claim: AI market $95B
Source: industry.pdf
Date: 2023-12"| C
        
        C[Synthesis Agent] --> D["Output:
Market size: $95B-$100B
Sources: techreport.com (Jan 24),
industry.pdf (Dec 23)
Note: Temporal difference,
not contradiction"]
    end
```
