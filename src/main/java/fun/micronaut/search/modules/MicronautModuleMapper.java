package fun.micronaut.search.modules;

import io.micronaut.context.annotation.Mapper;

public interface MicronautModuleMapper {
    @Mapper.Mapping(
            to = "configurationReferenceUri",
            from = "configurationreference://#{module.slug}"
    )
    @Mapper.Mapping(
            to = "documentationUri",
            from = "module://#{module.slug}"
    )
    MicronautModuleListing map(MicronautModule module);
}
