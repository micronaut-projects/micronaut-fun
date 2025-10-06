package fun.micronaut.mappers;

import fun.micronaut.model.MicronautModuleListing;
import fun.micronaut.conf.MicronautModule;
import io.micronaut.context.annotation.Mapper;

public interface MicronautModuleMapper {
    @Mapper.Mapping(
            to = "name",
            from = "#{module.title}"
    )
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
