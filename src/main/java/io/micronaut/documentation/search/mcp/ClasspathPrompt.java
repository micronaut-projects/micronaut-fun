package io.micronaut.documentation.search.mcp;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;

import java.util.List;

@EachProperty("micronaut.mcp.classpath-prompts")
public class ClasspathPrompt {

    private String nameQualifier;
    private String name;
    private String title;
    private String description;
    private String path;
    private List<PromptArgument> arguments;

    public ClasspathPrompt(@Parameter String name) {
        this.nameQualifier = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameQualifier() {
        return nameQualifier;
    }

    public void setNameQualifier(String nameQualifier) {
        this.nameQualifier = nameQualifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PromptArgument> getArguments() {
        return arguments;
    }

    public void setArguments(List<PromptArgument> arguments) {
        this.arguments = arguments;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
