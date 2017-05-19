package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import groovy.io.FileType
import org.apache.log4j.Logger
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Secured(value=["IS_AUTHENTICATED_FULLY"])
class HlaController {

    Logger logger =  Logger.getLogger(HlaController.class)

    HlaGenotyperService hlaGenotyperService
    BamService bamService
    def mailService


    def analyze() {
        String email = params.email
        HlaGenotyperService.HlaCoverage coverage = HlaGenotyperService.HlaCoverage.valueOf(params.coverage)
        HlaGenotyperService.HlaEthnicity ethnicity= HlaGenotyperService.HlaEthnicity.valueOf(params.ethnicity)

        List files = bamService.findBam(params.pipelineVersion, params.cohortId, params.sampleName)

        if (files && files[0]) {
            File bam = files[0]

            String fromEmail = grailsApplication.config.grails.mail.default.from
            logger.info("from: "+fromEmail)

            // Spawn a new thread
            Thread.start {
                File resultsDir = hlaGenotyperService.execute(bam, ethnicity, coverage)

                if (email) {
                    logger.info('Emailing results to: ' + email)
                    // zip files
                    byte[] zippedFile = zip(resultsDir)
                    String zipFilename = "HLA_" + bam.name + ".zip"

                    mailService.sendMail {
                        multipart true
                        subject bam.name + ": HLA genotyping predictions"
                        to email
                        from fromEmail
                        text "HLA results attached"
                        attach zipFilename, "application/zip", zippedFile
                    }
                    logger.info('Results sent')
                }
            }
        } else {
            errMsg = "Could not find ReadGroupSet with ID: " + params.readGroupSetId
        }


        withFormat {
            html {
                render "Results of the analysis will be sent to "+email
            }
            json {
                JSONObject json = new JSONObject()
                if (errMsg) {
                    json.put("error", errMsg)
                } else {
                    json.put("submitted", Boolean.TRUE)
                }

                render json
            }
        }


    }

    private byte[] zip(File workingDir) {
        logger.info('zipping: '+workingDir.absolutePath)
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream zipFile = new ZipOutputStream(baos)

        workingDir.eachFile (FileType.FILES) { File file ->
            if (file.isFile()) {
                logger.info('zipping file: '+file.name)
                zipFile.putNextEntry(new ZipEntry(file.name))
                file.withInputStream { i ->
                    zipFile << i
                }
            }
            zipFile.closeEntry()
        }

        zipFile.finish()

        return baos.toByteArray()
    }

}
