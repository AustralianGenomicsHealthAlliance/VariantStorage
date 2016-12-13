package agha.variantstorage

import grails.converters.JSON
import org.apache.log4j.Logger
import org.grails.web.json.JSONObject

class VariantSet {

    Logger logger = Logger.getLogger(VariantSet.class)

    static mapping = {
        table "VariantSet"
        version false
        datasetId column: 'datasetId'
        referencesetId column: 'referencesetId'
        dataUrlIndexMap column: 'dataUrlIndexMap'
    }

    static constraints = {
    }


    String id
    String name
    String datasetId
    String referencesetId
    String dataUrlIndexMap

    public List<String> parseFilePaths() {
        List<String> fileNames = []
        if (this.dataUrlIndexMap) {
            JSONObject filesMap =  JSON.parse(vs.dataUrlIndexMap)
            for (Iterator iter = filesMap.iterator(); iter.hasNext(); ) {
                def chrMap = iter.next()
                logger.info("chrMap="+chrMap)
                def files = chrMap.value
                logger.info("files="+files)

                for (String filePath: files ) {
                    fileNames << filePath
                }
            }
        }

        return fileNames
    }
}
