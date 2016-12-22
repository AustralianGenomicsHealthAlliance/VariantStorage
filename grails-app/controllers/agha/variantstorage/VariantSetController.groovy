package agha.variantstorage

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger
import org.grails.web.json.JSONObject

@Secured(value=["IS_AUTHENTICATED_FULLY"])
class VariantSetController {

    Logger logger = Logger.getLogger(VariantSetController.class)

    def show() {
        VariantSet vs = null
        VariantSet.withTransaction {
            vs = VariantSet.findById(params.id)
        }

        // Parse out the list of filenames associated with this VariantSet
        List<String> filePaths = vs.parseFilePaths()
        List<File> files = []
        if (filePaths) {
            for (String filePath : filePaths) {
                File file = new File(filePath)
                files << file
            }
        }


        respond vs, model: [files: files]
    }

    def index() {

        // Map each callset to a readGroupSet
        Map<String, ReadGroupSet> mapVariantSetIdToReadGroupSet = [:]

        List<VariantSet> variantSets = []

        VariantSet.withTransaction{
            for (VariantSet vs: VariantSet.findAll()) {
                //VariantSet vs = VariantSet.findById(callSet.variantSetId)
                if (vs) {
                    // Based on the datasetId and the variantSet name, find the ReadGroupSet
                    ReadGroupSet readGroupSet = ReadGroupSet.findByDatasetIdAndName(vs.datasetId, vs.name)
                    if (readGroupSet) {
                        mapVariantSetIdToReadGroupSet.put(vs.id, readGroupSet)
                    }
                }
            }

            variantSets = CallSet.findAll(sort:'name')
        }
        logger.info("mapCallSetIdToReadGroupSet: "+mapVariantSetIdToReadGroupSet)

        [mapVariantSetIdToReadGroupSet: mapVariantSetIdToReadGroupSet, variantSets: variantSets]
    }

}
