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

3. **MCP Integration**
   - `MicronautDocumentationSearchTool` - MCP search tool
   - `MicronautDocumentationFetchTool` - MCP fetch tool
   - Location: `src/main/java/io/micronaut/documentation/search/mcp/`

4. **Data Processing**
   - `DefaultMarkdownConversion` - HTML to Markdown conversion
   - `DefaultSummaryService` - Content summarization
   - `CoreDocsClient` - External documentation fetching

### Configuration

- **Main Class**: `io.micronaut.documentation.search.Application`
- **Package**: `micronaut.documentation.search`
- **OpenSearch**: Configured for localhost:9200
- **MCP Server**: HTTP transport enabled

### Dependencies

Key dependencies include:
- `micronaut-opensearch` - OpenSearch integration
- `micronaut-views-thymeleaf` - Template rendering
- `micronaut-mcp-server-java-sdk` - MCP server capabilities
- `jsoup` - HTML parsing
- `flexmark-html2md-converter` - HTML to Markdown conversion

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