package agha.variantstorage

import grails.transaction.Transactional

@Transactional
class VcfService {

    Logger logger = Logger.getLogger(VcfService.class)

    def List findUnfilteredVcf(String pipelineVersion,String cohortId, String sampleName) {
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

                            // Filter by summary folder
                            new File(sampleBioRunDir.absolutePath).eachDirMatch(~/vcf/) { vcfDir ->
                                logger.info('vcfDir: ' + vcfDir.name)

                                boolean found = false
                                // Filter by sampleName
                                new File(vcfDir.absolutePath).eachFileMatch(~/(?i)${sampleName}.*\.all\.vcf$/) { vcf ->
                                    logger.info('vcf: ' + vcf.name)
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
