package agha.variantstorage

import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 * Register VCF and BAM files with the ga4gh server
 * @author Philip Wu
 */
class CpiRegistrationService {

    static transactional = false

    Logger logger = Logger.getLogger(CpiRegistrationService.class)

    Ga4ghRegistrationService ga4ghRegistrationService

    public void registerVcfsBams() {

        // Recursively search folders
        new File("/home/philip/ga4gh-server-env/cpi_datasets").eachDirRecurse() { dir ->
            if (dir.name.endsWith("_snvcalls")) {
                String datasetName = dir.parentFile.name
                String vcfFolder = dir.absolutePath
                String assembly = "GRCh37"
                String bamFolder = dir.parentFile.absolutePath + "/bam_links"

                logger.info("datasetName="+datasetName)
                logger.info("vcfFolder="+vcfFolder)
                logger.info("bamFolder="+bamFolder)
                logger.info("assembly="+assembly)

                YamlObject yamlObj = new YamlObject()
                yamlObj.datasetName = datasetName
                yamlObj.vcfFolder = vcfFolder
                yamlObj.bamFolder = bamFolder
                yamlObj.assembly = assembly

                SampleNameHandler cpiSampleNameHandler = new SampleNameHandler() {

                    @Override
                    List<String> getSampleNames(File file) {
                        List sampleNames = []

                        sampleNames << file.name.split(("\\."))[0]
                        logger.info("sampleNames: "+sampleNames)
                        return sampleNames
                    }
                }

                ga4ghRegistrationService.registerDataset(yamlObj, cpiSampleNameHandler)
            }

        }

    }

    class YamlObject {
        String datasetName
        String vcfFolder
        String bamFolder
        String assembly
    }
}
