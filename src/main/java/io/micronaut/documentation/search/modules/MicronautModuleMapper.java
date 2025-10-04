package io.micronaut.documentation.search.modules;

import io.micronaut.context.annotation.Mapper;

public interface MicronautModuleMapper {
    @Mapper.Mapping(
            to = "configurationReferenceUri",
            from = "configurationreference://#{module.slug}"
    )
    MicronautModuleListing map(MicronautModule module);
}
