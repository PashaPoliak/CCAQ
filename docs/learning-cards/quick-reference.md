# Quick Reference

---

## Anti-Patterns Summary

```mermaid
mindmap
  root((Anti-Patterns<br/>to Avoid))
    Agentic Loops
      Parsing natural language for termination
      Arbitrary iteration caps
      Checking assistant text content
    Tool Design
      Minimal descriptions
      Too many tools per agent
      Generic error responses
    Multi-Agent
      Subagents inheriting context
      Direct subagent communication
      Silent error suppression
    Context
      Summarizing numerical values
      Keyword-sensitive prompts
      Self-reported confidence for escalation
    Prompting
      Vague instructions
      10+ few-shot examples
      Confidence-based filtering
```

---

## Best Practices Summary

```mermaid
mindmap
  root((Best Practices))
    Agentic Loops
      Check stop_reason field
      Use hooks for determinism
      Batch tool calls per turn
    Multi-Agent
      Hub-and-spoke architecture
      Explicit context passing
      Structured error propagation
    Tools
      Detailed descriptions
      4-5 tools per agent
      Use tool_choice wisely
    Claude Code
      Project-level CLAUDE.md
      Path-specific rules
      Plan mode for complexity
    Prompting
      Explicit criteria
      2-4 targeted few-shots
      Tool use for structured output
    Context
      Case facts block
      Position key info at start/end
      Fork for exploration
```

---

## Domain Weightings Reference

| Domain | Weight | Cards |
|--------|--------|-------|
| Domain 1: Agentic Architecture & Orchestration | 27% | 9 cards |
| Domain 2: Tool Design & MCP Integration | 18% | 7 cards |
| Domain 3: Claude Code Configuration | 20% | 8 cards |
| Domain 4: Prompt Engineering | 20% | 6 cards |
| Domain 5: Context Management | 15% | 7 cards |

---

## Exam Day Checklist

**Before the Exam:**
- [ ] Review all 5 domains
- [ ] Study the 12 sample questions in official guide
- [ ] Complete hands-on exercises
- [ ] Know the anti-patterns vs best practices

**Key Topics to Master:**
- [ ] `stop_reason` handling (`tool_use` vs `end_turn`)
- [ ] Programmatic vs prompt-based enforcement
- [ ] Hub-and-spoke multi-agent architecture
- [ ] Tool description quality
- [ ] MCP server scoping
- [ ] CLAUDE.md hierarchy
- [ ] Plan mode vs direct execution
- [ ] Tool use for structured output
- [ ] Context window management
- [ ] Escalation triggers

---

*Good luck on your Claude Certified Architect - Foundations exam!*
