1.1 API Request Structure
The Claude API follows a request–response model. Each request to the Claude Messages API includes the following key fields:

model — model selection (claude-opus-4-6, claude-sonnet-4-6, claude-haiku-4-5)
max_tokens — maximum number of tokens in the response
system — the system prompt (defines model behavior)
messages — conversation history (you must send the full history to maintain coherence)
tools — definitions of available tools
tool_choice — tool selection strategy

{
  "model": "claude-sonnet-4-6",
  "max_tokens": 1024,
  "system": "You are a helpful assistant.",
  "messages": [
    {"role": "user", "content": "Hi!"},
    {"role": "assistant", "content": "Hello!"},
    {"role": "user", "content": "How are you?"}
  ],
  "tools": [...],
  "tool_choice": {"type": "auto"}
}

1.2 Message Roles
The messages array uses three roles:

user — user messages
assistant — model responses (included when sending history)
tool — tool call results (appears as a tool_result content block)

Critically important: on every API request you must send the full conversation history. The model does not persist state between requests—each call is independent.
1.3 The stop_reason Field
The Claude API response includes stop_reason, which indicates why the model stopped generating. For agentic systems, tool_use and end_turn are the most important—they control the agent loop.
Value	Description	Action
"end_turn"	The model finished its response	Show the result to the user
"tool_use"	The model wants to call a tool	Execute the tool and return the result
"max_tokens"	Token limit reached	Response is truncated; increase the limit
"stop_sequence"	A stop sequence was encountered	Handle based on your application logic
1.4 System Prompt
The system prompt is a special instruction that defines context and behavioral rules. It:
• Is not part of the messages array; it is passed separately in the system field
• Has priority over user messages
• Is loaded once and applies throughout the conversation
• Is used to define role, constraints, and output format

Important for the exam: system prompt wording can create unintended tool associations. For example, an instruction like "always verify the customer" can cause the model to overuse get_customer, even when it is unnecessary.
1.5 Context Window
The context window is the total amount of text (in tokens) the model can process at once. It includes: the system prompt, the full message history, tool definitions, and tool results.

Key context-window problems:

1. Lost-in-the-middle effect: models reliably process information at the start and end of a long input but can miss details in the middle. Mitigation: place key information near the beginning or end.

2. Accumulation of tool results: every tool call adds output to the context. If a tool returns 40+ fields but only 5 matter, most of the context is wasted.

3. Progressive summarization: when compressing history, numeric values, percentages, and dates often get lost and become vague ("about", "roughly", "a few").
🔧
Tools and tool_use
2.1 What is tool_use
tool_use is a mechanism that allows Claude to call external functions. The model does not run code directly—it generates a structured tool call request; your code executes it and returns the result.

The flow:
1. You define tools in the API request
2. Claude decides to call a tool and returns a tool_use block
3. Your code executes the tool
4. You return the result as a tool_result block
5. Claude continues based on the result
2.2 Tool Definition
Each tool is defined using a JSON schema. Critically important aspects of a tool description:

1. The description is the primary selection mechanism. An LLM chooses tools based on their descriptions. Minimal descriptions lead to mistakes when tools overlap.

2. Include in the description: what the tool does and returns, input formats and example values, edge cases and constraints, when to use this tool vs similar alternatives.

3. Avoid identical or overlapping descriptions across tools. If analyze_content and analyze_document have nearly identical descriptions, the model will confuse them.

4. Built-in tools vs MCP tools: agents may prefer built-in tools (Read, Grep) over MCP tools with similar functionality. Strengthen MCP tool descriptions to highlight concrete advantages.

{
  "name": "get_customer",
  "description": "Finds a customer by email or ID. Returns the customer
  profile, including name, email, order history, and account status.
  Use this tool BEFORE lookup_order to verify the customer's identity.
  Accepts an email (format: user@domain.com) or a numeric customer_id.",
  "input_schema": {
    "type": "object",
    "properties": {
      "email": {
        "type": "string",
        "description": "Customer email"
      },
      "customer_id": {
        "type": "integer",
        "description": "Numeric customer ID"
      }
    },
    "required": []
  }
}
2.3 The tool_choice Parameter
tool_choice controls how the model selects tools.

Important scenarios:
• tool_choice: "any" + multiple extraction tools → the model picks the best one, but you still get structured output
• Forced selection → when you must guarantee a specific first action (e.g., extract_metadata before enrichment)
Value	Behavior	When to use
{"type": "auto"}	The model decides whether to call a tool or answer in text	Default for most cases
{"type": "any"}	The model must call some tool	When you need guaranteed structured output
{"type": "tool", "name": "extract_metadata"}	The model must call a specific tool	When you need a forced first step
2.4 JSON Schemas for Structured Output
Using tool_use with JSON schemas is the most reliable way to obtain structured output from Claude. It:
• Guarantees syntactically valid JSON (no missing braces, no trailing commas)
• Enforces the required structure (required fields are present)
• Does not guarantee semantic correctness (values can still be wrong)

Schema design rules:
1. Required vs optional: mark fields as required only if the information is always available. Required fields push the model to fabricate values when data is missing.
2. Nullable fields: use "type": ["string", "null"] for information that may be absent. The model can return null instead of hallucinating.
3. Enums with "other": add "other" + a detail string to avoid losing data outside your predefined categories.
4. Enum "unclear": for cases where the model cannot confidently pick a category—honest "unclear" is better than a wrong category.

{
  "type": "object",
  "properties": {
    "category": {
      "type": "string",
      "enum": ["bug", "feature", "docs", "unclear", "other"]
    },
    "category_detail": {
      "type": ["string", "null"],
      "description": "Details if category = 'other' or 'unclear'"
    },
    "severity": {
      "type": "string",
      "enum": ["critical", "high", "medium", "low"]
    },
    "confidence": {
      "type": "number",
      "minimum": 0,
      "maximum": 1
    }
  },
  "required": ["category", "severity"]
}
2.5 Syntax vs Semantic Errors
Understanding the difference between error types is critical for designing robust systems.
Error Type	Example	Mitigation
Syntax	Invalid JSON, wrong field type	tool_use with a JSON schema (eliminates)
Semantic	Totals don't add up, value in wrong field, hallucination	Validation checks, retry with feedback, self-correction
