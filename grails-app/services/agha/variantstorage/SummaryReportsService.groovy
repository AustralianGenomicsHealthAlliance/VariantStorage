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

    def grailsApplication

    /**
     * Returns a list of summary files
     * @param pipelineVersion
     * @param sampleName
     * @return
     */
    def List findFiles(String pipelineVersion,String cohortId = null, String sampleName) {
        logger.info("pipelineVersion: "+pipelineVersion)
        logger.info("cohortId: "+cohortId)
        logger.info("sampleName: "+sampleName)

        String fileRoot = grailsApplication.config.existing.files.root
        logger.info("fileRoot: "+fileRoot)

        // Example folder structure:
        // ../v2.3/human_related_gatk/APOSLE_cohort52/APOSLE_cohort52_sg1_affected1_runs/APOSLE_cohort52_sg1_affected1_180/summary

        List files = []

        try {
            // Filter by pipeline
            new File(fileRoot).eachDirMatch(~/(?i)${pipelineVersion}/) { pipelineDir ->
                logger.info('pipelineDir: ' + pipelineDir.name)

                // Filter by cohortId
                new File(pipelineDir.absolutePath).traverse(maxDepth: 2, nameFilter: ~/(?i)${cohortId}/) { cohortDir ->
                    logger.info('cohortDir: ' + cohortDir.name)

                    // Filter by sample run
                    new File(cohortDir.absolutePath).eachDirMatch(~/(?i)${sampleName}_runs/) { runDir ->
                        logger.info('runDir: ' + runDir.name)

                        // Filter by sample Bio run
                        new File(runDir.absolutePath).eachDirMatch(~/(?i)${sampleName}_.*/) { sampleBioRunDir ->
                            logger.info('sampleBioRunDir: ' + sampleBioRunDir.name)

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
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }

        logger.info("find files completed")
        return files
    }
}
