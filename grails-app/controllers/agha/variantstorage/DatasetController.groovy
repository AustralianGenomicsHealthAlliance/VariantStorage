package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured

/**
 * Controller for viewing datasets (cohorts)
 */
@Secured(value=["IS_AUTHENTICATED_FULLY"])
class DatasetController {

    def index() {

        List datasets =[]
        Map<String,List<VariantSet>> variantSetMap = [:]
        Map<String,List<ReadGroupSet>> readGroupSetMap = [:]

        Dataset.withTransaction {
            datasets = Dataset.findAll(sort:'name')


            for (Dataset dataset: datasets) {
                // Map to VariantSets
                List<VariantSet> variantSets = VariantSet.findAllByDatasetId(dataset.id)
                variantSetMap.put(dataset.id, variantSets)

                // Map to ReadGroupSets
                List<ReadGroupSet> readGroupSets = ReadGroupSet.findAllByDatasetId(dataset.id)
                readGroupSetMap.put(dataset.id, readGroupSets)
            }


        }

        [datasets: datasets, variantSetMap: variantSetMap, readGroupSetMap: readGroupSetMap]

    }
}
