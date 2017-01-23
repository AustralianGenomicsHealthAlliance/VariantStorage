package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Secured(value=["IS_AUTHENTICATED_FULLY"])
//@Secured(value=["ROLE_ANONYMOUS"])
class DownloadController {

    Logger logger = Logger.getLogger(DownloadController.class)

    def index() { }

    /**
     * Download all the files for a variant set
     */
    def variantSet() {
        VariantSet vs = null
        VariantSet.withTransaction {
            vs = VariantSet.findById(params.id)
        }

        if (vs) {
            String filename = vs.name + ".vcfs.zip"
            logger.info("filename: " + filename)
            response.setContentType('APPLICATION/OCTET-STREAM')
            response.setHeader("Content-Disposition", "Attachment;Filename=" + filename)

            // Parse out the list of filenames associated with this VariantSet
            List<String> filePaths = vs.parseFilePaths()

            // Compress the files into a single file for download
            zipFiles(filePaths, response.outputStream)


            response.outputStream.flush()
        }
    }

    /**
     * Download all the files for a readgroup set
     */
    def readGroupSet() {
        ReadGroupSet rgs = null
        ReadGroupSet.withTransaction {
            rgs = ReadGroupSet.findById(params.id)
        }

        if (rgs) {
            String filename = rgs.name + ".bam.zip"
            logger.info("filename: " + filename)
            response.setContentType('APPLICATION/OCTET-STREAM')
            response.setHeader("Content-Disposition", "Attachment;filename=" + filename)

            // Parse out the list of filenames associated with this VariantSet
            List<String> filePaths = [rgs.dataUrl, rgs.indexFile]

            zipFiles(filePaths, response.outputStream)


            response.outputStream.flush()
        }
    }

    def bam() {

        // Parse the id to remove the trailing .bam extension
        String rgsId = params.id.replaceFirst('.bam', '')
        logger.info("rgsId="+rgsId)

        ReadGroupSet rgs = null
        ReadGroupSet.withTransaction {
            rgs = ReadGroupSet.findById(rgsId)
        }

        if (rgs) {
            File bam = new File(rgs.dataUrl)
            String filename = params.id
            response.setHeader("Content-Disposition", "Attachment;filename=" + filename)
            response.setHeader("ETag", filename);
            response.setHeader("Accept-Ranges", "bytes");
            DownloadHelper.download(params, request, response, bam)
        }

    }

    def bai() {

        // Parse the id to remove the trailing .bam extension
        String rgsId = params.id.replaceFirst('.bai', '')
        logger.info("rgsId="+rgsId)

        ReadGroupSet rgs = null
        ReadGroupSet.withTransaction {
            rgs = ReadGroupSet.findById(rgsId)
        }

        if (rgs) {
            File bai = new File(rgs.indexFile)
            String filename = params.id
            response.setHeader("Content-Disposition", "Attachment;Filename=" + filename)
            response.setHeader("ETag", filename);
            response.setHeader("Accept-Ranges", "bytes");
            DownloadHelper.download(params, request, response, bai)
        }

    }

    /**
     * Zip files and send to output stream
     * @param filePaths
     * @param os
     */
    private void zipFiles(List<String> filePaths, OutputStream os) {

        // Compress the files into a single file for download
        ZipOutputStream zipFile = new ZipOutputStream(os)
        for (String filePath: filePaths) {
            logger.info("Adding to zip file: "+filePath)
            File file = new File(filePath)
            zipFile.putNextEntry(new ZipEntry(file.getName()))
            file.withInputStream { i ->
                zipFile << i
            }
            zipFile.closeEntry()
        }
        zipFile.close()

    }

}
