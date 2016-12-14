package agha.variantstorage

import org.apache.log4j.Logger

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DownloadController {

    Logger logger = Logger.getLogger(DownloadController.class)

    def index() { }

    /**
     * Download all the files for a variant set
     */
    def variantSet() {
        VariantSet vs = VariantSet.findById(params.id)

        String filename = vs.name+".zip"
        logger.info("filename: "+filename)
        response.setContentType('APPLICATION/OCTET-STREAM')
        response.setHeader("Content-Disposition", "Attachment;Filename="+filename)


        // Parse out the list of filenames associated with this VariantSet
        List<String> filePaths = vs.parseFilePaths()

        // Compress the files into a single file for download
        ZipOutputStream zipFile = new ZipOutputStream(response.outputStream)
        for (String filePath: filePaths) {
            logger.info("Adding to zip file: "+filePath)
            File file = new File(filePath)
            zipFile.putNextEntry(new ZipEntry(file.getName()))
            def buffer = new byte[1024]
            file.withInputStream { i ->
                zipFile << i
            }
            zipFile.closeEntry()
        }
        zipFile.close()


        response.outputStream.flush()
    }
}
