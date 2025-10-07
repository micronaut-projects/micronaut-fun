To configure Micronaut Fun as an [MCP Server for Cline](https://docs.cline.bot/mcp/configuring-mcp-servers), you must **IMPORTANT: use type `streamableHttp`**

```json
{
    "mcpServers": {
        "micronautfun": {
            "type": "streamableHttp",
            "url": "https://micronaut.fun/mcp",
            "disabled": false,
            "autoApprove": []
        }
    }
}
```