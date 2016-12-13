package agha.variantstorage

import agha.cli.CliExec
import grails.transaction.Transactional
import groovy.io.FileType
import htsjdk.variant.vcf.VCFFileReader
import htsjdk.variant.vcf.VCFHeader
import org.apache.camel.Exchange
import org.apache.log4j.Logger

class Ga4ghRegistrationService {

    static transactional = false

    Logger logger = Logger.getLogger(Ga4ghRegistrationService.class)

    def grailsApplication

    /**
     * For use in camel integration
     * @param exchange
     * @return
     */
    def registerDataset(Exchange exchange) {
        String filename = exchange.getIn().getHeader("CamelFileName")

        logger.info("registerDataset: "+filename)

        def yamlObj = exchange.getIn().body
        logger.info("body="+yamlObj)

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
        } else {

        }

        // VARAINT SETs
        // Group VCFs by sample name
        Map<String, List> mapSampleNameToVcfs =  mapSampleNameToVcfs(yamlObj.vcfFolder)

        // Bgzip the entire folder
        bgzipFolder(yamlObj.vcfFolder)
        // Tabix the entire folder
        tabixFolder(yamlObj.vcfFolder)

        logger.info("vcfFolder: "+yamlObj.vcfFolder)
        logger.info("referencesetName: "+yamlObj.assembly)
        for (Map.Entry entry: mapSampleNameToVcfs.entrySet()) {
            String sampleName = entry.key
            List vcfs = entry.value

            List vcfsBgzipped = vcfs.collect { it+".gz"}
            logger.info("vcfsBgzipped: "+vcfsBgzipped)

            addVariantSet(sampleName, datasetName, vcfsBgzipped, yamlObj.assembly)
        }


    }

    /**
     * Add a dataset to the ga4gh server using the command-line
     * @param name
     */
    public void addDataset(String name) {
        String command = grailsApplication.config.ga4gh_repo.path + "/ga4gh_repo add-dataset " + grailsApplication.config.ga4gh_repo.registry +" "+ name
        logger.info("executing command: "+command)
        String response = CliExec.execCommand(command)
        logger.info("response: "+response)
    }

    /**
     * Segregate the VCFs by assigning each VCF to a sample
     * @param strVcfFolder
     * @return
     */
    public Map<String,List> mapSampleNameToVcfs(String strVcfFolder) {

        Map<String, List> mapSampleNameToVcfs = [:]

        File vcfFolder = new File(strVcfFolder)

        // Find all the VCFs in the folder
        vcfFolder.eachFileMatch(FileType.ANY, ~/.*\.vcf/) { file ->
            logger.info("File: "+file.name)
            VCFFileReader vcfFileReader = new VCFFileReader(file)
            VCFHeader vcfHeader = vcfFileReader.getFileHeader()
            logger.info("sample names: "+vcfHeader.getGenotypeSamples())
            for (String sampleName : vcfHeader.getGenotypeSamples()) {
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
     * Add a variantset
     * @param name
     * @param vcfFolder
     * @param referencesetName
     */
    public void addVariantSet(String name, String datasetName, List vcfs, String referencesetName) {

        String vcfsArgument = vcfs.join(" ")

        String command = grailsApplication.config.ga4gh_repo.path + "/ga4gh_repo add-variantset " + grailsApplication.config.ga4gh_repo.registry+" "+datasetName+" "+vcfsArgument+" --name "+name+" --referenceSetName "+referencesetName
        logger.info("executing command: "+command)
        String response = CliExec.execCommand(command)
        logger.info("response: "+response)


    }

    public void bgzipFolder(String folder) {
        // Only bgzip folder if VCF files exist. If we don't check, then an error is thrown.
        String[] lsCommand = ['sh', '-c', 'ls *.vcf']
        String lsResponse = CliExec.execCommand(lsCommand, new File(folder))
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

        //if (lsResponse) {
            String tabixCommand = 'for i in *.gz; do tabix -f \$i; done'
            String[] shellTabixCommand = ['sh', '-c', tabixCommand]
            CliExec.execCommand(shellTabixCommand, new File(folder))
        //}
    }
}
