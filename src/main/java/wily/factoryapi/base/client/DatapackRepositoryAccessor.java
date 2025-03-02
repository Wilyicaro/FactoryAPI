package wily.factoryapi.base.client;

import net.minecraft.server.packs.repository.PackRepository;

public interface DatapackRepositoryAccessor {
    PackRepository getDatapackRepository();
    void tryApplyNewDataPacks(PackRepository repository);
}
