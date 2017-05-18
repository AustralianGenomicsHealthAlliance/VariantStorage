package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Secured(value=["IS_AUTHENTICATED_FULLY"])
//@Secured(value=["ROLE_ANONYMOUS", "IS_AUTHENTICATED_FULLY", "IS_AUTHENTICATED_ANONYMOUSLY"])
class DownloadController {

    Logger logger = Logger.getLogger(DownloadController.class)

    SummaryReportsService summaryReportsService
    VcfService vcfService
    BamService bamService

    def index() { }


    def vcf() {

        List files = vcfService.findUnfilteredVcfs(params.pipelineVersion, params.cohortId, params.sampleName)

        if (files && files[0]) {
            File file = files[0]
                response.setHeader("Content-Disposition", "Attachment;filename=" + file.name)
                DownloadHelper.download(params, request, response, file)
        } else {
            render ('file not found')
        }
    }

    def bam() {

        logger.info("filename="+params.filename)

        File file = null
        if (params.filename.endsWith('.bam')) {
            List files = bamService.findBam(params.pipelineVersion, params.cohortId, params.sampleName)
            if (files && files[0]) {
                file = files[0]
            }
        } else if (params.filename.endsWith('.bai')) {
            List files = bamService.findBamIdx(params.pipelineVersion, params.cohortId, params.sampleName)
            if (files && files[0]) {
                file = files[0]
            }
        }


        if (file) {
            response.setHeader("Content-Disposition", "Attachment;filename=" + file.name)
            DownloadHelper.download(params, request, response, file)
        } else {
            render ("file not found")
        }

    }


    def summary() {

        String sampleName = params.sampleName //"APOSLE_cohort52_sg1_AFFECTED1"

        List files = summaryReportsService.findFiles(params.pipelineVersion, params.cohortId, sampleName)

        List filePaths = files.collect { it.absolutePath }

        String filename = sampleName+".summary.zip"
        response.setContentType('APPLICATION/OCTET-STREAM')
        response.setHeader("Content-Disposition", "Attachment;filename=" + filename)
        OutputStream os = response.outputStream
        zipFiles(filePaths, os)

        os.flush()
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
