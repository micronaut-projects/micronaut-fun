# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a **Micronaut Documentation Search** application that provides:
- OpenSearch-based documentation indexing and search functionality
- Web interface for searching Micronaut documentation
- MCP (Model Context Protocol) server capabilities for AI integration
- Kamal deployment configuration

## Key Technologies

- **Micronaut Framework 4.5.4** - Java web framework
- **OpenSearch** - Document search and indexing
- **Java 21** - Required JDK version
- **Gradle Kotlin DSL** - Build system
- **Thymeleaf** - Server-side templating
- **MCP Server** - Model Context Protocol integration
- **Kamal** - Deployment tool

## Development Commands

### Local Development Setup

Before running the app, start OpenSearch via Docker:
```bash
docker run -d --name micronautfunopensearch -p 9200:9200 -p 9600:9600 \
  -e "discovery.type=single-node" \
  -e "plugins.security.disabled=true" \
  -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=DummyPassword#1233" \
  opensearchproject/opensearch:2.19.3
```

### Build and Run
```bash
# Build the application
./gradlew build

# Run the application locally
./gradlew run

# Create shadow JAR
./gradlew shadowJar

# Run tests
./gradlew test

# Run specific test
./gradlew test --tests "ClassName"
```

### Docker and Native Image
```bash
# Build Docker image
./gradlew dockerBuild

# Build native image with GraalVM
./gradlew nativeCompile
```

## Application Architecture

### Core Components

1. **Search Engine**
   - `DocumentSearchService` - Core search functionality
   - `DocumentIndexingService` - Document indexing operations  
   - `StartupIndexingService` - Initial data loading

2. **Web Controllers**
   - `SearchController` - Main search interface (`/`)
   - `IndexingController` - Document indexing endpoints
   - `OpenSearchHealthController` - Health checks

3. **MCP Integration** (`src/main/java/io/micronaut/documentation/search/mcp/`)
   - **Tools**: `MicronautDocumentationSearchTool`, `MicronautDocumentationFetchTool`, `MicronautGuidesTools`
   - **Resource Templates**: `MicronautGuidesResourceTemplates` - Dynamic guide resources
   - **Completions**: `MicronautGuidesCompletions` - Auto-completion support
   - **Configuration**: `ConfigurationReferenceFactory` - Configuration reference generation
   - **Prompts**: Configured in `application.properties` (introspection-testing, static-resources-testing, serdeable-testing, dev-default-environment)

4. **Data Processing**
   - `DefaultMarkdownConversion` - HTML to Markdown conversion
   - `DefaultSummaryService` - Content summarization
   - `CoreDocsClient` - External documentation fetching
   - `GuidesFetcher` - Fetches Micronaut guides
   - `GuidesHttpClient` - HTTP client for guides API

### Configuration

- **Main Class**: `fun.micronaut.search.Application`
- **Package**: `micronaut.documentation.search`
- **OpenSearch**: Configured for localhost:9200
- **MCP Server**: HTTP transport enabled
- **HTTP Services**:
  - `mnguides` → https://guides.micronaut.io
  - `micronautdocs` → https://docs.micronaut.io
  - `micronautprojects` → https://micronaut-projects.github.io
- **Cache**: Caffeine cache configured for guides
- **JSON Schema**: Enabled at `/schemas/**` path (base URI: https://micronaut.fun/schemas)

### Dependencies

Key dependencies include:
- `micronaut-opensearch` - OpenSearch integration
- `micronaut-views-thymeleaf` - Template rendering
- `micronaut-mcp-server-java-sdk:0.0.10` - MCP server capabilities
- `micronaut-jsonschema` - JSON Schema support
- `micronaut-cache-caffeine` - Caching
- `jsoup:1.17.2` - HTML parsing
- `flexmark-html2md-converter:0.64.8` - HTML to Markdown conversion

## Deployment

Uses **Kamal** for deployment:
- Service: `micronautfun`
- Host: `micronaut.fun`
- Includes OpenSearch accessory
- SSL enabled with health checks at `/health`
- Configuration: `config/deploy.yml`

## Micronaut Module Configuration

The application includes extensive configuration for Micronaut modules in `application.properties`, covering modules like:
- Data access (MongoDB, Redis, SQL, etc.)
- Cloud providers (AWS, Azure, GCP)
- Messaging (Kafka, RabbitMQ, MQTT)
- Security and validation
- Testing and monitoring

## Testing

- Framework: JUnit 5
- Test resources integration available
- Location: `src/test/java/io/micronaut/documentation/search/`