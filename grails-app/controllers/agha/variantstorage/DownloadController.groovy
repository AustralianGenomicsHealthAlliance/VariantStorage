package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Secured(value=["IS_AUTHENTICATED_FULLY"])
//@Secured(value=["ROLE_ANONYMOUS", "IS_AUTHENTICATED_FULLY", "IS_AUTHENTICATED_ANONYMOUSLY"])
class DownloadController {

    Logger logger = Logger.getLogger(DownloadController.class)

    Ga4ghService ga4ghService

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

    def vcf() {
        String filename = params.filename
        String variantSetId = params.variantSetId

        logger.info('filename='+filename)
        logger.info('variantSetId='+variantSetId)

        VariantSet vs = null
        VariantSet.withTransaction {
            vs = VariantSet.findById(params.variantSetId)
        }

        if (vs) {
            // Find the matching file based on the name
            List<String> filePaths = vs.parseFilePaths()
            File file = null
            filePaths.each { aFilePath ->
                File aFile = new File(aFilePath)
                if (aFile.name.equals(filename)) {
                    file = aFile
                }
            }

            logger.info("file="+file)
            // File found?
            if (file) {
                response.setHeader("Content-Disposition", "Attachment;filename=" + filename)
                //response.setHeader("Accept-Ranges", "bytes");
//                if (filename.endsWith(".gz")) {
//                    response.setHeader("Content-Encoding", "gzip");
//                }

                DownloadHelper.download(params, request, response, file)
            } else {
                render ('file not found')
            }
        }
    }


    def bam() {

        // Parse the id to remove the trailing .bam extension
        String rgsId = params.readGroupSetId
        logger.info("rgsId="+rgsId)
        logger.info("filename="+params.filename)

        ReadGroupSet rgs = null
        ReadGroupSet.withTransaction {
            rgs = ReadGroupSet.findById(rgsId)
        }

        if (rgs) {
            File file = null
            if (params.filename.endsWith('.bam')) {
                file = new File(rgs.dataUrl)
            } else if (params.filename.endsWith('.bai')) {
                file = new File(rgs.indexFile)
            }

            response.setHeader("Content-Disposition", "Attachment;filename=" + file.name)
            //response.setHeader("Accept-Ranges", "bytes");
            //response.setHeader("Content-Encoding", "gzip");
            DownloadHelper.download(params, request, response, file)
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
