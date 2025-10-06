package fun.micronaut.search.modules;

import fun.micronaut.mappers.MicronautModuleMapper;
import fun.micronaut.model.Modules;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
class DefaultModulesFetcher implements ModulesFetcher {
    private final Modules modules;

    DefaultModulesFetcher(List<MicronautModule> micronautModuleList,
                          MicronautModuleMapper mapper) {
        this.modules = new Modules(micronautModuleList.stream().map(mapper::map).toList());
    }

    @Override
    public Modules modules() {
        return modules;
    }
}
