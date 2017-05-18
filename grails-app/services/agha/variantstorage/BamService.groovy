package agha.variantstorage

import grails.transaction.Transactional
import org.apache.log4j.Logger

@Transactional
class BamService {

    Logger logger = Logger.getLogger(BamService.class)

    FileService fileService

    /**
     * Find the bam file
     * @param pipelineVersion
     * @param cohortId
     * @param sampleName
     * @return
     */
    def List findBam(String pipelineVersion,String cohortId, String sampleName) {

        Closure filesClosure = { File samplesBioRunDir, List files ->
            // Filter by summary folder
            new File(sampleBioRunDir.absolutePath).eachDirMatch(~/bam/) { bamDir ->
                logger.info('bamDir: ' + bamDir.name)

                boolean found = false
                // Filter by sampleName

                new File(bamDir.absolutePath).eachFileMatch(~/(?i)${sampleName}.*\.bam$/) { bam ->
                    logger.info('bam: ' + bam.name)
                    files.add(bam)
                    found = true
                }

                if (found) {
                    throw new Exception("Breaking out of loop")
                }
            }
        } // end Closure

        return fileService.find(pipelineVersion, cohortId, sampleName, filesClosure)
    }

    /**
     * Find the index of the bam file
     * @param pipelineVersion
     * @param cohortId
     * @param sampleName
     * @return
     */
    def List findBamIdx(String pipelineVersion,String cohortId, String sampleName) {

        Closure filesClosure = { File samplesBioRunDir, List files ->
            // Filter by summary folder
            new File(sampleBioRunDir.absolutePath).eachDirMatch(~/bam/) { bamDir ->
                logger.info('bamDir: ' + bamDir.name)

                boolean found = false
                // Filter by sampleName

                new File(bamDir.absolutePath).eachFileMatch(~/(?i)${sampleName}.*\.bam\.bai$/) { bam ->
                    logger.info('bam: ' + bam.name)
                    files.add(bam)
                    found = true
                }

                if (found) {
                    throw new Exception("Breaking out of loop")
                }
            }
        } // end Closure

        return fileService.find(pipelineVersion, cohortId, sampleName, filesClosure)
    }

}
