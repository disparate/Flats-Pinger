package kazarovets.flatspinger.usecase

import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.model.Provider


class MergeFlatsStrategy {

    fun mergeFlats(favoriteFlats: List<FlatWithStatus> = emptyList(),
                   onlinerRemoteFlats: List<FlatWithStatus>,
                   iNeedAFlatRemoteFlats: List<FlatWithStatus>,
                   currentShownFlats: List<FlatWithStatus> = emptyList()): List<FlatWithStatus> {

        val allINeedAFlatFlats = iNeedAFlatRemoteFlats
                .addUnique(currentShownFlats.filterProvider(Provider.I_NEED_A_FLAT))

        val allOnlinerFlats = onlinerRemoteFlats
                .addUnique(currentShownFlats.filterProvider(Provider.ONLINER))

        return favoriteFlats.addUnique(allINeedAFlatFlats)
                .addUnique(allOnlinerFlats)
                .removeDuplicates { it.imageUrl }
    }

    private fun List<FlatWithStatus>.filterProvider(provider: Provider): List<FlatWithStatus> {
        return this.filter { it.provider == provider }
    }

    private fun List<FlatWithStatus>.addUnique(listToFilterFrom: List<FlatWithStatus>)
            : List<FlatWithStatus> {
        return listToFilterFrom.filter { flat ->
            this
                    .any { compared ->
                        flat.originalUrl == compared.originalUrl
                    || (flat.imageUrl?.isNotBlank() == true && flat?.imageUrl == compared?.imageUrl)}

                    .not()
        } + this
    }


    private fun List<FlatWithStatus>.removeDuplicates(filterPredicate: (FlatWithStatus) -> String?): List<FlatWithStatus> {
        val set = HashSet<String>()
        return this.filter {
            filterPredicate(it)?.let {
                val setContainsKey = set.contains(it)
                set.add(it)
                setContainsKey.not()
            } ?: true
        }
    }

}