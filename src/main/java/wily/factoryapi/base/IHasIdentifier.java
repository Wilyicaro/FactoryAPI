package wily.factoryapi.base;

import java.util.List;

public interface IHasIdentifier {

    SlotsIdentifier identifier();

    static List<SlotsIdentifier> getSlotsIdentifiers(List<? extends IHasIdentifier> list){
        return list.stream().map(IHasIdentifier::identifier).toList();
    }
}
