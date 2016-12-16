package agha.variantstorage

import org.apache.log4j.Logger

/**
 * Controller for ReadGroupSets (BAMs) in the GA4GH server
 * @author Philip Wu
 */
class ReadGroupSetController {

    Logger logger = Logger.getLogger(VariantSetController.class)

    def show() {
        ReadGroupSet rgs = ReadGroupSet.findById(params.id)

        File bamFile = new File(rgs.dataUrl)
        File indexFile = new File(rgs.indexFile)

        List<File> files = [bamFile, indexFile]


        respond rgs, model:[files: files]
    }

}