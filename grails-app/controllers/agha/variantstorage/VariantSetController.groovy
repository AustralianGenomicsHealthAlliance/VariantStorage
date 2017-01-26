package agha.variantstorage

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Secured(value=["IS_AUTHENTICATED_FULLY"])
//@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class VariantSetController {

    Logger logger = Logger.getLogger(VariantSetController.class)

    Ga4ghService ga4ghService

    def show() {
        VariantSet vs = null
        VariantSet.withTransaction {
            vs = VariantSet.findById(params.id)
        }

        // Find the associated BAM
        logger.info("datasetId: "+vs.datasetId+' sampleName: '+vs.name)

        ReadGroupSet.withTransaction {
            List<ReadGroupSet> rgs = ReadGroupSet.findAllByDatasetId(vs.datasetId)
            logger.info('rgs='+rgs)
            for (ReadGroupSet readGrpSet: rgs) {
                logger.info('readGrpSet name: '+readGrpSet.name)
            }
            logger.info('rgs readGroups = '+rgs.readGroups)
            rgs.readGroups.each { rg ->
                logger.info('sampleName:'+rg.sampleName)
            }
        }
        ReadGroupSet readGroupSet = ga4ghService.findReadGroupSetByDatasetIdAndName(vs.datasetId, vs.name.toUpperCase())
        logger.info('readGroupSet='+readGroupSet)

        // Parse out the list of filenames associated with this VariantSet
        List<String> filePaths = vs.parseFilePaths()
        Map<String,String> chrMap = [:]
        List<File> files = []
        Long totalSize = 0
        if (filePaths) {
            for (String filePath : filePaths) {
                File file = new File(filePath)
                files << file
                totalSize += file.length()

                //Map each filename to a chromosome using the vcf file
                logger.info('file.name='+file.name)
                if (file.name.endsWith('.vcf.gz')) {
                    String firstChr = ga4ghService.getFirstChr(file)
                    chrMap[file.name] = firstChr
                    logger.info('firstChr=' + firstChr)
                }
            }
        }

        logger.info('chrMap: '+chrMap)

        withFormat {
            html { respond vs, model: [files: files, readGroupSet: readGroupSet, chrMap: chrMap]  }
            json {
                logger.info("Creating json response")
                // Collect fields of interest into a Map for a JSON response
                JSONObject json = new JSONObject()

                JSONObject variantSetJson = new JSONObject()
                variantSetJson.put('id', vs.id)
                variantSetJson.put('name', vs.name)
                variantSetJson.put('datasetId', vs.datasetId)
                variantSetJson.put('totalSize', totalSize)
                json.put('variantSet', variantSetJson)

                JSONArray filesJson = new JSONArray()

                for (File file: files ) {
                    JSONObject fileJson = new JSONObject()
                    fileJson.put('name', file.name)
                    fileJson.put('size', file.length())
                    fileJson.put('absolutePath', file.absolutePath)

                    filesJson.add(fileJson)
                }

                json.put('files', filesJson)

                render json
            }
        }
    }

    def list() {

        // list by datasetId
        if (params.datasetId) {

            List variantSets = []
            Dataset dataset = null

            VariantSet.withTransaction {
                variantSets = VariantSet.findAllByDatasetId(params.datasetId)
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

                    JSONArray vsArray = new JSONArray()
                    json.put("variantSets", vsArray)

                    for (VariantSet vs: variantSets) {
                        JSONObject vsJson = new JSONObject()
                        vsJson.put("name", vs.name )
                        vsJson.put("id", vs.id)
                        vsArray.add(vsJson)
                    }

                    render json
                }
            }

        }

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
