package agha.variantstorage

import org.apache.log4j.Logger

/**
 * Register VCF and BAM files with the ga4gh server
 * @author Philip Wu
 */
class CpiRegistrationService {

    static transactional = false

    Logger logger = Logger.getLogger(CpiRegistrationService.class)

    Ga4ghService ga4ghService
    def grailsApplication

    public void registerVcfsBams() {
        String fileRoot = grailsApplication.config.existing.files.root

        // Recursively search folders
        new File(fileRoot).eachDirRecurse() { dir ->
            if (dir.name.endsWith("_snvcalls")) {
                String cohortName = dir.parentFile.name
                if (cohortName.toUpperCase().matches("(APOSLE|MONA|GERMAN|MGRB|CPIC).*")) {
                    String vcfFolder = dir.absolutePath
                    String assembly = "GRCh37"
                    String bamFolder = dir.parentFile.absolutePath + "/bam_links"

                    String pipelineVersion = getPipelineVersion(dir)

                    String datasetName = cohortName
                    if (pipelineVersion) {
                        datasetName += '_' + pipelineVersion
                    }

                    // Uppercase datasetName
                    datasetName = datasetName.toUpperCase()

                    logger.info("pipelineVersion=" + pipelineVersion)
                    logger.info("datasetName=" + datasetName)
                    logger.info("vcfFolder=" + vcfFolder)
                    logger.info("bamFolder=" + bamFolder)
                    logger.info("assembly=" + assembly)

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
                            //logger.info("sampleNames: "+sampleNames)
                            return sampleNames
                        }
                    }

                    // skip errors
                    try {
                        ga4ghService.registerDataset(yamlObj, cpiSampleNameHandler)
                    } catch (Exception ex) {
                        logger.error(ex.getMessage())
                        ex.printStackTrace()
                    }
                }
            }
        }

    }

    /**
     * Recursively move up one folder until the version is found starting with the character 'v'
     * @param file
     * @return
     */
    public String getPipelineVersion(File file) {
        if (file) {
            File parentFile = file.getParentFile()
            logger.info("parentFile="+parentFile)
            if (parentFile && parentFile.name.toLowerCase().startsWith('v')) {
                return parentFile.name
            } else {
                return getPipelineVersion(parentFile)
            }
        }
        return ''
    }

    class YamlObject {
        String datasetName
        String vcfFolder
        String bamFolder
        String assembly
    }
}
