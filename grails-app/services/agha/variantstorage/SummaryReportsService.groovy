package agha.variantstorage

import grails.transaction.Transactional
import groovy.io.FileType
import org.apache.log4j.Logger
import org.hibernate.criterion.Example

/**
 * Service for interacting with summary report files
 *
 * @author Philip Wu
 */
@Transactional
class SummaryReportsService {

    Logger logger = Logger.getLogger(SummaryReportsService.class)

    FileService fileService
    def grailsApplication

    /**
     * Returns a list of summary files
     * @param pipelineVersion
     * @param sampleName
     * @return
     */
    def List findFiles(String pipelineVersion,String cohortId, String sampleName) {
        Closure filesClosure = { File sampleBioRunDir, List files ->
            // Filter by summary folder
            new File(sampleBioRunDir.absolutePath).eachDirMatch(~/summary/) { summaryDir ->
                logger.info('summaryDir: ' + summaryDir.name)

                boolean found = false
                // Filter by sampleName
                new File(summaryDir.absolutePath).eachFileMatch(~/(?i)${sampleName}.*\.summary\.tsv$/) { tsv ->
                    logger.info('tsv: ' + tsv.name)
                    files.add(tsv)
                    found = true
                }

                if (found) {
                    throw new Exception("Breaking out of loop")
                }

            }
        } // end closure

        return fileService.find(pipelineVersion, cohortId, sampleName, filesClosure)
    }
}
