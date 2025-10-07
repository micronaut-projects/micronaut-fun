# Add an MCP Server to Claude Code

You can connect [Claude Code to tools via MCP](https://docs.claude.com/en/docs/claude-code/mcp). You can use the `claude mcp add` command to add an MCP server.

```
claude mcp add --transport http micronautfun https://micronaut.fun/mcp
```

The previous command adds an entry in the `mcpServers` of the project:

```json
"mcpServers": {
   "micronautfun": {
     "type": "http",
     "url": "https://micronaut.fun/mcp"
   }
},
```

These commands modify the `vi $HOME/.claude.json` configuration file.

You can list your project MCP servers with the `claude mcp list` command.