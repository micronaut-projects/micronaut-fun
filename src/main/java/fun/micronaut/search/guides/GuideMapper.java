package fun.micronaut.search.guides;

import io.micronaut.context.annotation.Mapper;

public interface GuideMapper {
    @Mapper.Mapping(
            to = "uri",
            from = "guidemetadata://#{guide.slug}"
    )
    GuideListing map(Guide guide);
}
