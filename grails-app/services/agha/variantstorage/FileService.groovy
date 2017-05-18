package agha.variantstorage

import com.sun.istack.internal.logging.Logger
import grails.transaction.Transactional

@Transactional
class FileService {

    Logger logger = Logger.getLogger(FileService.class)

    def grailsApplication

    def List find(String pipelineVersion,String cohortId, String sampleName, Closure closure) {
        logger.info("pipelineVersion: "+pipelineVersion)
        logger.info("cohortId: "+cohortId)
        logger.info("sampleName: "+sampleName)

        String fileRoot = grailsApplication.config.existing.files.root
        logger.info("fileRoot: "+fileRoot)

        // Example folder structure:
        // \v2.3\human_related_gatk\APOSLE_cohort33\APOSLE_cohort33_sg1_affected1_runs\APOSLE_cohort33_sg1_affected1_198\vcf

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

                            closure(sampleBioRunDir, files)
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
