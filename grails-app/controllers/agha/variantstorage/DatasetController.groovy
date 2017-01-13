package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

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

    def search() {
        Dataset dataset = null
        Dataset.withTransaction {
            dataset = Dataset.findByName(params.name)
        }

        JSONObject json = new JSONObject()

        if (dataset) {
            // Dataset details
            json.put("name", dataset.name)
            json.put("id", dataset.id)
        }

        render json
    }
}
