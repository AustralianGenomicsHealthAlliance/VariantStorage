package agha.variantstorage

import agha.cli.CliExec
import grails.transaction.Transactional
import groovy.io.FileType
import htsjdk.samtools.SAMFileHeader
import htsjdk.samtools.SAMReadGroupRecord
import htsjdk.samtools.SamReader
import htsjdk.samtools.SamReaderFactory
import htsjdk.variant.vcf.VCFFileReader
import htsjdk.variant.vcf.VCFHeader
import org.apache.camel.Exchange
import org.apache.log4j.Logger

class Ga4ghRegistrationService {

    static transactional = false

    Logger logger = Logger.getLogger(Ga4ghRegistrationService.class)

    def grailsApplication
    def sessionFactory

    /**
     * For use in camel integration
     * @param exchange
     * @return
     */
    def registerDataset(def yamlObj, SampleNameHandler sampleNameHandler = new DefaultSampleNameHandler()) {
        logger.info("yaml="+yamlObj)

        //  DATASET
        // Only add the dataset if it doesn't already exist
        String datasetName = yamlObj.datasetName
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
            Map<String, List> mapSampleNameToFiles = mapSampleNameToFiles(yamlObj.vcfFolder, sampleNameHandler)

            // Bgzip the entire folder
            bgzipFolder(yamlObj.vcfFolder)
            // Tabix the entire folder
            tabixFolder(yamlObj.vcfFolder)

            logger.info("vcfFolder: " + yamlObj.vcfFolder)
            logger.info("referencesetName: " + yamlObj.assembly)
            for (Map.Entry entry : mapSampleNameToFiles.entrySet()) {
                String sampleName = entry.key
                List filePaths = entry.value

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

                    // Check that the readgroupset doesn't already exist with the same name
                    ReadGroupSet readGroupSet = null
                    ReadGroupSet.withTransaction {
                        readGroupSet = ReadGroupSet.findByDatasetIdAndName(dataset.id, sampleName)
                    }
                    logger.info("readGroupSet=" + readGroupSet)
                    if (readGroupSet == null) {
                        addReadGroupSet(datasetName, bamPath, yamlObj.assembly)
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
        String command = grailsApplication.config.ga4gh_repo.path + "/ga4gh_repo add-dataset " + getGa4ghRegistryPath() +" "+ name
        logger.info("executing command: "+command)
        String response = CliExec.execCommand(command)
        logger.info("response: "+response)
    }

    /**
     * Segregate the VCFs by assigning each VCF to a sample
     * @param strVcfFolder
     * @return
     */
    public Map<String,List> mapSampleNameToFiles(String strVcfFolder, SampleNameHandler sampleNameHandler = new DefaultSampleNameHandler()) {

        Map<String, List> mapSampleNameToVcfs = [:]

        File vcfFolder = new File(strVcfFolder)

        // Find all the VCFs in the folder
        vcfFolder.eachFileMatch(FileType.ANY, ~/.*\.vcf|.*\.gz/) { file ->
            logger.info("File: "+file.name)

            List<String> sampleNames = sampleNameHandler.getSampleNames(file)
            for (String sampleName : sampleNames) {
                List<String> vcfs = mapSampleNameToVcfs.get(sampleName)
                if (vcfs == null) {
                    vcfs = []
                    mapSampleNameToVcfs.put(sampleName, vcfs)
                }

                vcfs << file.absolutePath
            }
        }

        return mapSampleNameToVcfs
    }

    /**
     * Add a variantset to the GA4GH server
     * @param name
     * @param vcfFolder
     * @param referencesetName
     */
    public void addVariantSet(String name, String datasetName, List vcfs, String referencesetName) {

        String vcfsArgument = vcfs.join(" ")

        String command = grailsApplication.config.ga4gh_repo.path + "/ga4gh_repo add-variantset " + getGa4ghRegistryPath()+" "+datasetName+" "+vcfsArgument+" --name "+name+" --referenceSetName "+referencesetName
        logger.info("executing command: "+command)
        String response = CliExec.execCommand(command)
        logger.info("response: "+response)


    }

    /**
     * Adds a readgroupset to the GA4GH server
     * @param datasetName
     * @param bamPath
     * @param referencesetName
     */
    public void addReadGroupSet(String datasetName, String bamPath, String referencesetName) {
        bamPath = bamPath.replaceAll(" ","\\ ") // Escape spaces
        String command = grailsApplication.config.ga4gh_repo.path + "/ga4gh_repo add-readgroupset "+getGa4ghRegistryPath()+" "+datasetName+" -R "+referencesetName+" "+bamPath
        logger.info("executing command: "+command)
        String response = CliExec.execCommand(command)
        logger.info("response: "+response)
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

    public void tabixFolder(String folder) {
        // Only bgzip folder if GZ files exist. If we don't check, then an error is thrown.
        //String[] lsCommand = ['sh', '-c', 'ls *.gz']
        //String lsResponse = CliExec.execCommand(lsCommand, new File(folder))
        //logger.info("lsResponse: "+lsResponse)

        // Check that each bgzipped file has a matching tabix file
        new File(folder).eachFileMatch(FileType.ANY, ~/.*\.gz/) { file ->
            String strTabixFile = file.absolutePath + '.tbi'
            File tabixFile = new File(strTabixFile)
            // Check if it exists
            if ( ! tabixFile.exists()) {
                // Run command to tabix
                String tabixCommand = 'tabix -f ' + file.absolutePath
                String[] shellTabixCommand = ['sh', '-c', tabixCommand]
                CliExec.execCommand(shellTabixCommand, new File(folder))
            }
        }


//        String tabixCommand = 'for i in *.gz; do tabix -f \$i; done'
//        String[] shellTabixCommand = ['sh', '-c', tabixCommand]
//        CliExec.execCommand(shellTabixCommand, new File(folder))

    }

    /**
     * Map sample name to BAM files
     * @param strBamFolder
     * @return
     */
    public Map<String,String> mapSampleNameToBams(String strBamFolder) {

        Map<String, String> mapSampleNameToBams = [:]

        File bamFolder = new File(strBamFolder)

        // Find all the VCFs in the folder
        bamFolder.eachFileMatch(FileType.ANY, ~/.*\.bam/) { file ->
            logger.info("File: "+file.name)
            SamReader samReader = SamReaderFactory.makeDefault().open(file)
            SAMFileHeader fileHeader = samReader.getFileHeader()

            List<SAMReadGroupRecord> readGroups = fileHeader.getReadGroups()
            if (readGroups) {
                for (SAMReadGroupRecord readGroup: readGroups) {
                    String sample = readGroup.getSample()
                    //logger.info("sample: "+sample)
                    mapSampleNameToBams.put(sample, file.absolutePath)
                }
            }
        }

        return mapSampleNameToBams
    }

    public String getGa4ghRegistryPath() {
        String registryPath = grailsApplication.config.dataSources.ga4gh.url.replaceFirst("jdbc:sqlite:", "")
        logger.info("registryPath: "+registryPath)
        return registryPath
    }

}
