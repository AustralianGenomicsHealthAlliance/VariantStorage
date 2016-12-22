package agha.variantstorage

import org.apache.log4j.Logger
import grails.plugin.springsecurity.annotation.Secured

@Secured(value=["IS_AUTHENTICATED_FULLY"])
class CallSetController {

    Logger logger = Logger.getLogger(CallSetController.class)

    def index() {

        // Map each callset to a readGroupSet
        Map<String, ReadGroupSet> mapCallSetIdToReadGroupSet = [:]

        List<CallSet> callSets = []

        CallSet.withTransaction{
            for (CallSet callSet: CallSet.findAll([max:50], {})) {
                VariantSet vs = VariantSet.findById(callSet.variantSetId)
                if (vs) {
                    // Based on the datasetId and the callset name, find the ReadGroupSet
                    ReadGroupSet readGroupSet = ReadGroupSet.findByDatasetIdAndName(vs.datasetId, callSet.name)
                    if (readGroupSet) {
                        mapCallSetIdToReadGroupSet.put(callSet.id, readGroupSet)
                    }
                }
            }

            callSets = CallSet.findAll(sort:'name')
        }
        logger.info("num callsets: "+callSets.size())
        logger.info("mapCallSetIdToReadGroupSet: "+mapCallSetIdToReadGroupSet)

        [mapCallSetIdToReadGroupSet: mapCallSetIdToReadGroupSet, callSets: callSets]
    }
}
