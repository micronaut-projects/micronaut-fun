package fun.micronaut.mappers;

import fun.micronaut.model.Guide;
import fun.micronaut.model.GuideListing;
import io.micronaut.context.annotation.Mapper;

public interface GuideMapper {
    @Mapper.Mapping(
            to = "uri",
            from = "guidemetadata://#{guide.slug}"
    )
    GuideListing map(Guide guide);
}
