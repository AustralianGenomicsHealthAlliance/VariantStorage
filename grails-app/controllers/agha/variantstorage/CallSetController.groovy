package agha.variantstorage

import org.apache.log4j.Logger

class CallSetController {

    Logger logger = Logger.getLogger(CallSetController.class)

    def index() {

        // Map each callset to a readGroupSet
        Map<String, ReadGroupSet> mapCallSetIdToReadGroupSet = [:]
        for (CallSet callSet: CallSet.findAll()) {
            VariantSet vs = VariantSet.findById(callSet.variantSetId)
            if (vs) {
                // Based on the datasetId and the callset name, find the ReadGroupSet
                ReadGroupSet readGroupSet = ReadGroupSet.findByDatasetIdAndName(vs.datasetId, callSet.name)
                if (readGroupSet) {
                    mapCallSetIdToReadGroupSet.put(callSet.id, readGroupSet)
                }
            }
        }
        logger.info("mapCallSetIdToReadGroupSet: "+mapCallSetIdToReadGroupSet)

        [mapCallSetIdToReadGroupSet: mapCallSetIdToReadGroupSet]
    }
}
