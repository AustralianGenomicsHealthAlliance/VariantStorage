package agha.variantstorage

import grails.transaction.Transactional
import org.apache.log4j.Logger

@Transactional
class VcfService {

    Logger logger = Logger.getLogger(VcfService.class)

    FileService fileService

    def List findUnfilteredVcfs(String pipelineVersion,String cohortId, String sampleName) {
        // Anonymous closure
        Closure filesClosure = { File sampleBioRunDir, List files ->
            // Filter by summary folder
            new File(sampleBioRunDir.absolutePath).eachDirMatch(~/vcf/) { vcfDir ->
                logger.info('vcfDir: ' + vcfDir.name)

                boolean found = false
                // Filter by sampleName
                // TODO OR bgzip
                new File(vcfDir.absolutePath).eachFileMatch(~/(?i)${sampleName}.*\.all\.vcf$/) { vcf ->
                    logger.info('vcf: ' + vcf.name)
                    files.add(vcf)
                    found = true
                }

                if (found) {
                    throw new Exception("Breaking out of loop")
                }
            }
        } // end closure

        return fileService.find(pipelineVersion, cohortId, sampleName, filesClosure)
    }


    def List findUnfilteredIdx(String pipelineVersion,String cohortId, String sampleName) {

        Closure filesClosure = { File sampleBioRunDir, List files ->
            // Filter by summary folder
            new File(sampleBioRunDir.absolutePath).eachDirMatch(~/vcf/) { vcfDir ->
                logger.info('vcfDir: ' + vcfDir.name)

                boolean found = false
                // Filter by sampleName
                // TODO OR bgzip
                new File(vcfDir.absolutePath).eachFileMatch(~/(?i)${sampleName}.*\.all\.vcf\.idx$/) { idx ->
                    logger.info('idx: ' + idx.name)
                    files.add(idx)
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
