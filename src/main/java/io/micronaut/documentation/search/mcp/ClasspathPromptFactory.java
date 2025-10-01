/*
 * Copyright 2017-2025 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.documentation.search.mcp;

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.ResourceLoader;
import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Factory
final class ClasspathPromptFactory {
    private static final String CLASSPATH_PREFIX = "classpath:";
    private final Map<String, String> nameToPrompt = new ConcurrentHashMap<>();

    ClasspathPromptFactory(ResourceLoader resourceLoader,
                           List<ClasspathPrompt> prompts) {
        for (ClasspathPrompt prompt : prompts) {
            String path = CLASSPATH_PREFIX + prompt.getPath();
            Optional<InputStream> resourceAsStream = resourceLoader.getResourceAsStream(path);
            if (resourceAsStream.isEmpty()) {
               throw new ConfigurationException("classpath resource for prompt " + prompt.getName() + " at path: " + path);
            }
            String text = null;
            try (InputStream inputStream = resourceAsStream.get()) {
                text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ConfigurationException("error reading prompt at path: " + path, e);
            }
            nameToPrompt.put(prompt.getName(), text);
        }
    }

    @EachBean(ClasspathPrompt.class)
    @Singleton
    McpStatelessServerFeatures.SyncPromptSpecification stalessSyncPromptSpecification(ClasspathPrompt classpathPrompt) {
        return new McpStatelessServerFeatures.SyncPromptSpecification(prompt(classpathPrompt), this::result);
    }

    @EachBean(ClasspathPrompt.class)
    @Singleton
    McpStatelessServerFeatures.AsyncPromptSpecification stalessAsyncPromptSpecification(ClasspathPrompt classpathPrompt) {
        return new McpStatelessServerFeatures.AsyncPromptSpecification(prompt(classpathPrompt),
                (mcpTransportContext, getPromptRequest) -> Mono.just(result(mcpTransportContext, getPromptRequest)));
    }

    @EachBean(ClasspathPrompt.class)
    @Singleton
    McpServerFeatures.SyncPromptSpecification syncPromptSpecification(ClasspathPrompt classpathPrompt) {
        return new McpServerFeatures.SyncPromptSpecification(prompt(classpathPrompt),
                (mcpSyncServerExchange, getPromptRequest) -> result(mcpSyncServerExchange.transportContext(), getPromptRequest));
    }

    @EachBean(ClasspathPrompt.class)
    @Singleton
    McpServerFeatures.AsyncPromptSpecification asyncPromptSpecification(ClasspathPrompt classpathPrompt) {
        return new McpServerFeatures.AsyncPromptSpecification(prompt(classpathPrompt),
                (mcpSyncServerExchange, getPromptRequest) -> Mono.just(result(mcpSyncServerExchange.transportContext(), getPromptRequest)));
    }

    private McpSchema.GetPromptResult result(McpTransportContext transportContext, McpSchema.GetPromptRequest getPromptRequest) {
        String text = nameToPrompt.get(getPromptRequest.name());
        return new McpSchema.GetPromptResult(null,
                List.of(new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent(StringInterpolator.interpolate(text, getPromptRequest.arguments())))));
    }

    @NonNull
    private McpSchema.Prompt prompt(@NonNull ClasspathPrompt classpathPrompt) {
        return new McpSchema.Prompt(classpathPrompt.getName(),
                classpathPrompt.getTitle(),
                classpathPrompt.getDescription(),
                classpathPrompt.getArguments()
                        .stream()
                        .map(this::promptArgument)
                        .toList());
    }

    @NonNull
    private McpSchema.PromptArgument promptArgument(@NonNull PromptArgument arg) {
        return new McpSchema.PromptArgument(arg.getName(), arg.getTitle(), arg.getDescription(), arg.isRequired());
    }
}
