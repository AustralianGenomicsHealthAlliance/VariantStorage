package agha.variantstorage

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

/**
 * Controller for ReadGroupSets (BAMs) in the GA4GH server
 * @author Philip Wu
 */
//@Secured(value=["IS_AUTHENTICATED_FULLY"])
@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class ReadGroupSetController {

    Logger logger = Logger.getLogger(VariantSetController.class)

    def show() {
        ReadGroupSet rgs = null
        ReadGroupSet.withTransaction {
            rgs = ReadGroupSet.findById(params.id)
        }

        List<File> files = []
        if(rgs) {
            File bamFile = new File(rgs.dataUrl)
            File indexFile = new File(rgs.indexFile)

            files = [bamFile, indexFile]
        }

        withFormat {
            html {
                respond rgs, model:[files: files]
            }
            json {
                JSONObject json = new JSONObject()
                if (rgs) {
                    JSONObject rgsJson = new JSONObject()
                    rgsJson.put('name', rgs.name)
                    rgsJson.put('id', rgs.id)
                    rgsJson.put('datasetId', rgs.datasetId)
                    json.put('readGroupSet', rgsJson)

                    JSONArray filesJson = new JSONArray()
                    for (File file: files) {
                        JSONObject jsonFile = new JSONObject()
                        jsonFile.put('name', file.name)
                        jsonFile.put('size', file.length())
                        jsonFile.put('absolutePath', file.absolutePath)
                        filesJson.add(jsonFile)
                    }
                    json.put('files', filesJson)
                }

                render json
            }
        }

    }

    def list() {
        logger.info("list datasetId="+params.datasetId)
        // list by datasetId
        if (params.datasetId) {

            List readGroupSets = []
            Dataset dataset = null

            ReadGroupSet.withTransaction {
                readGroupSets = ReadGroupSet.findAllByDatasetId(params.datasetId)
                dataset = Dataset.findById(params.datasetId)
            }

            withFormat {
                json {
                    JSONObject json = new JSONObject()

                    // Dataset details
                    JSONObject datasetJson = new JSONObject()
                    datasetJson.put("name", dataset.name)
                    datasetJson.put("id", dataset.id)
                    json.put('dataset', datasetJson)

                    JSONArray rgsArray = new JSONArray()
                    json.put("readGroupSets",  rgsArray)

                    for (ReadGroupSet rgs: readGroupSets) {
                        JSONObject rgsJson = new JSONObject()
                         rgsJson.put("name", rgs.name )
                         rgsJson.put("id", rgs.id)
                         rgsArray.add( rgsJson)
                    }

                    logger.info("json="+json)
                    render json
                }
            }

        }

    }

}
