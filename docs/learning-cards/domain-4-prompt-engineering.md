# Domain 4: Prompt Engineering & Structured Output (20%)

---

## Card 4.1: Explicit Criteria

### Question
What's the most effective way to improve prompt precision?

### Answer
**Explicit criteria over vague instructions:**
- "Flag comments only when claimed behavior contradicts actual code behavior" vs "check that comments are accurate"
- Avoid confidence-based filtering like "be conservative" or "only report high-confidence findings"

### False Positive Impact
> High false positive categories undermine developer trust in accurate categories. Temporarily disable high false-positive categories to restore trust while improving prompts.

```mermaid
flowchart TD
    subgraph "Vague Instructions"
        A["'Be conservative'"] --> B[Interpreted inconsistently]
        B --> C[False positives]
        C --> D[Lost trust]
    end

    subgraph "Explicit Criteria"
        E["'Flag when comment
≠ code behavior'"] --> F[Clear boundaries]
        F --> G[Consistent output]
        G --> H[Trusted results]
    end
```

---

## Card 4.2: Few-Shot Prompting

### Question
How do few-shot examples improve output consistency?

### Answer
**Most effective technique for consistent formatting:**
- 2-4 targeted examples for ambiguous scenarios showing reasoning for choices
- Demonstrate specific desired output format (location, issue, severity, suggested fix)
- Show acceptable patterns vs genuine issues to reduce false positives while enabling generalization

### Key Benefit
> Few-shot examples enable the model to generalize judgment to novel patterns rather than matching only pre-specified cases.

```mermaid
flowchart TD
    A[Few-Shot Examples] --> B["2-4 examples
Not 10-15"]
    
    B --> C["Focus on:
Ambiguous cases"]
    
    C --> D["Show reasoning:
Why A not B"]
    
    D --> E["Include format:
Location, Issue, Severity"]
    
    E --> F["Result:
Consistent output
Novel pattern handling"]
```

---

## Card 4.3: Structured Output with Tool Use

### Question
What's the most reliable approach for guaranteed structured output?

### Answer
**Tool use with JSON schemas:**
- Eliminates JSON syntax errors entirely
- Strict schemas don't prevent semantic errors (values in wrong fields, line items not summing to total)

### Schema Design
> Use optional/nullable fields when source documents may not contain information (prevents fabrication). Add enum values like "unclear" for ambiguous cases and "other" + detail fields for extensible categorization.

```mermaid
flowchart TD
    A[Need structured output] --> B{Approach?}
    
    B -->|JSON in text| C["❌ Parsing errors
❌ Invalid JSON
❌ Inconsistent"]
    
    B -->|Tool use| D["✅ Guaranteed schema
✅ No syntax errors
✅ Consistent format"]
    
    D --> E["Schema design:
- nullable fields
- 'unclear' enum
- 'other' + detail"]
```

---

## Card 4.4: Validation-Retry Loops

### Question
How do you implement validation-retry loops?

### Answer
**Retry with specific error feedback:**
- Append specific validation errors to the prompt on retry to guide correction
- Send follow-up with original document, failed extraction, and specific validation errors
- Track which errors are resolvable (format mismatches) vs not (information absent)

### When Retries Fail
> Retries are ineffective when required information is simply absent from the source document vs format/structural errors.

```mermaid
flowchart TD
    A[Extraction fails validation] --> B{Error type?}
    
    B -->|Format error| C["Retry with feedback:
'Line items sum to 45,
not stated total 50'"]
    C --> D[Model corrects]
    
    B -->|Info absent| E["No retry
Mark as null
Proceed"]
    
    B -->|Wrong field| F["Retry with feedback:
'Shipping should be
in shipping_cost,
not total'"]
```

---

## Card 4.5: Message Batches API

### Question
When is the Message Batches API appropriate?

### Answer
**50% cost savings for latency-tolerant workloads:**
- **Appropriate:** Overnight reports, weekly audits, nightly test generation
- **Inappropriate:** Blocking workflows, pre-merge checks
- Up to 24-hour processing window, no guaranteed latency SLA

### Limitations
> Batch API does not support multi-turn tool calling within a single request. Use `custom_id` fields to correlate request/response pairs.

```mermaid
flowchart LR
    subgraph "Use Batch API"
        A["Overnight reports
Weekly audits
Nightly jobs"]
    end
    
    subgraph "Don't Use"
        B["Pre-merge checks
Real-time APIs
User-facing requests"]
    end
    
    subgraph "Batch Features"
        C["50% cost savings
24h processing
custom_id correlation"]
    end
    
    subgraph "Limitations"
        D["No guaranteed SLA
No multi-turn tools
Polling required"]
    end
```

---

## Card 4.6: Multi-Pass Review

### Question
How do you design multi-pass review architectures?

### Answer
**Split large reviews into focused passes:**
- Per-file local analysis passes for local issues
- Separate cross-file integration pass for data flow analysis
- Avoids attention dilution and contradictory findings

### Self-Review Limitation
> Independent review instances (without prior reasoning context) are more effective at catching subtle issues than self-review instructions or extended thinking.

```mermaid
flowchart TD
    A[Large PR: 14 files] --> B["❌ Single Pass
Attention diluted
Inconsistent depth
Missed bugs"]
    
    A --> C["✅ Multi-Pass"]
    
    C --> D["Pass 1: Per-file
Local issues only"]
    C --> E["Pass 2: Integration
Cross-file data flow"]
    C --> F["Pass 3: Security
Vulnerability check"]
    
    D --> G[Consistent depth]
    E --> G
    F --> G
```
