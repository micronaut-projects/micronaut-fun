package fun.micronaut.services;

import fun.micronaut.model.Modules;
import io.micronaut.core.annotation.NonNull;

public interface ModulesFetcher {
    @NonNull
    Modules modules();
}
