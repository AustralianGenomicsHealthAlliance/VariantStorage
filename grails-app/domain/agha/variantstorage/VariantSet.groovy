package agha.variantstorage

import grails.converters.JSON
import org.grails.web.json.JSONObject

/**
 * VariantSet domain class for GA4GH database
 * @author Philip Wu
 */
class VariantSet {

    //Logger logger = Logger.getLogger(VariantSet.class)

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
            JSONObject filesMap =  JSON.parse(this.dataUrlIndexMap)
            for (Iterator iter = filesMap.iterator(); iter.hasNext(); ) {
                def chrMap = iter.next()
                //logger.info("chrMap="+chrMap)
                def files = chrMap.value
                //logger.info("files="+files)

                for (String filePath: files ) {
                    fileNames << filePath
                }
            }
        }

        return fileNames
    }
}
