package agha.variantstorage

import agha.cli.CliExec
import grails.transaction.Transactional
import grails.util.Environment
import org.apache.log4j.Logger

@Transactional
class DatasetService {

    Logger logger = Logger.getLogger(DatasetService.class)

    def grailsApplication
    def sessionFactory

    def registerDataset(def yamlObj, SampleNameHandler sampleNameHandler = new DefaultSampleNameHandler()) {
        logger.info("yaml="+yamlObj)

        //  DATASET
        // Only add the dataset if it doesn't already exist
        String datasetName = yamlObj.datasetName
        if (grailsApplication.config.uppercase.names) {
            datasetName = datasetName.toUpperCase()
        }
        logger.info("datasetName=" + datasetName)

        Dataset dataset = null
        Dataset.withTransaction  {
            dataset = Dataset.findByName(datasetName)
        }
        if (dataset == null) {
            addDataset(datasetName)

            Dataset.withTransaction  {
                dataset = Dataset.findByName(datasetName)
                logger.info("datasetId: "+dataset.id)
            }
        }

        assert(dataset != null)
        logger.info("datasetId="+dataset.id)

        // VARAINT SETs (vcfs)
        // Group VCFs by sample name
        if (yamlObj.vcfFolder) {
            logger.info("Processing vcfFolder: "+yamlObj.vcfFolder)
            Map<String, List> mapSampleNameToFiles = mapSampleNameToFilesInFolder(yamlObj.vcfFolder, sampleNameHandler)

            if (Environment.current == Environment.DEVELOPMENT) {
                // Bgzip the entire folder
                bgzipFolder(yamlObj.vcfFolder)
                // Tabix the entire folder
                tabixFolder(yamlObj.vcfFolder)
            }


            logger.info("vcfFolder: " + yamlObj.vcfFolder)
            logger.info("referencesetName: " + yamlObj.assembly)
            for (Map.Entry entry : mapSampleNameToFiles.entrySet()) {
                String sampleName = entry.key
                List filePaths = entry.value

                // uppercase sample names based on configuration
                if (grailsApplication.config.uppercase.names) {
                    sampleName = sampleName.toUpperCase()
                }

                List bgzippedFiles = []
                for (String filePath : filePaths) {
                    if (filePath.endsWith(".gz")) {
                        bgzippedFiles << filePath
                    } else {
                        bgzippedFiles << filePath+".gz"
                    }
                }

                logger.info("bgzippedFiles: " + bgzippedFiles)

                // Check that the variant set doesn't already exist with the same name
                VariantSet vs = null
                VariantSet.withTransaction {
                    vs = VariantSet.findByDatasetIdAndName(dataset.id, sampleName)
                }
                if (vs == null) {
                    addVariantSet(sampleName, datasetName, bgzippedFiles, yamlObj.assembly)
                } else {
                    logger.info("VariantSet already exists. No action taken.")
                }
            }
        }

        // Read Group sets (BAMs)
        if (yamlObj.bamFolder) {
            File bamFolderFile = new File(yamlObj.bamFolder)
            if (bamFolderFile.exists()) {
                Map<String, String> mapSampleNameToBams = mapSampleNameToBams(yamlObj.bamFolder)
                logger.info("mapSampleNameToBams: " + mapSampleNameToBams)
                for (Map.Entry entry : mapSampleNameToBams.entrySet()) {
                    String sampleName = entry.key
                    String bamPath = entry.value

                    if (grailsApplication.config.uppercase.names) {
                        sampleName = sampleName.toUpperCase()
                    }

                    // Check that the readgroupset doesn't already exist with the same name
                    ReadGroupSet readGroupSet = null
                    ReadGroupSet.withTransaction {
                        readGroupSet = ReadGroupSet.findByDatasetIdAndName(dataset.id, sampleName)
                    }
                    logger.info("readGroupSet=" + readGroupSet)
                    if (readGroupSet == null) {
                        addReadGroupSet(datasetName, sampleName, bamPath, yamlObj.assembly)
                    } else {
                        logger.warn("ReadGroupSet already exists: " + bamPath + ". No action taken.")
                    }
                }
            } else {
                logger.info("Skipping bam folder that does not exist: "+yamlObj.bamFolder)
            }
        }

        logger.info("dataset registered: "+yamlObj)

    }

    /**
     * Add a dataset to the ga4gh server using the command-line
     * @param name
     */
    public void addDataset(String name) {
        Dataset d = new Dataset()

    }

    /**
     * Add a variantset
     * @param name
     * @param vcfFolder
     * @param referencesetName
     */
    public void addVariantSet(String name, String datasetName, List vcfs, String referencesetName) {

    }

    /**
     * Adds a readgroupset to the GA4GH server
     * @param datasetName
     * @param bamPath
     * @param referencesetName
     */
    public void addReadGroupSet(String datasetName, String name=null, String bamPath, String referencesetName) {

    }

    public void bgzipFolder(String folder) {
        // Only bgzip folder if VCF files exist. If we don't check, then an error is thrown.
        String[] lsCommand = ['sh', '-c', 'ls *.vcf']
        String lsResponse = CliExec.execCommand(lsCommand, new File(folder), null, true, true)
        logger.info("lsResponse: "+lsResponse)

        if (lsResponse) {
            String bgzipCommand = 'for i in *.vcf; do bgzip \$i; done'
            String[] bashBgzipCommand = ['sh', '-c', bgzipCommand]
            // String[] commands = [cdCommand, bgzipCommand]
            //logger.info("Command: "+commands)
            CliExec.execCommand(bashBgzipCommand, new File(folder))
        }

    }

}
