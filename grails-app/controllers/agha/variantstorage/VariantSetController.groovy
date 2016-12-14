package agha.variantstorage

import grails.converters.JSON
import org.apache.log4j.Logger
import org.grails.web.json.JSONObject

class VariantSetController {

    Logger logger = Logger.getLogger(VariantSetController.class)

    def show() {
        VariantSet vs = VariantSet.findById(params.id)

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
}
