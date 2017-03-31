package agha.variantstorage

import agha.cli.CliExec
import grails.transaction.Transactional
import org.apache.log4j.Logger

@Transactional
class HlaGenotyperService {

    Logger logger = Logger.getLogger(HlaGenotyperService.class)

    def grailsApplication

    enum HlaCoverage {
        exome,
        genome,
        rnaseq
    }

    enum HlaEthnicity {
        EUR,
        AFA,
        HIS,
        API,
        UNK
    }

    public File unmappedBam(File bam, File workingDir) {
        logger.info('creating unmapped bam for '+bam)
        String samtoolsPath = grailsApplication.config.samtools.path

        String sampleId = bam.name.replaceAll(".bam", "")


        workingDir.mkdirs()
        String unmappedBamFilename = workingDir.absolutePath + "/" +sampleId+".unmapped.bam"
        File unmappedBam = new File(unmappedBamFilename)

        // Only do the conversion if the file doesn't already exist
        if (! unmappedBam.exists()) {

            List command = []
            command << samtoolsPath
            command << 'view'
            command << '-u'
            command << '-f'
            command << '4'
            command << bam.absolutePath
            command << '-o'
            command << unmappedBam.absolutePath

            logger.info("executing command: " + command)
            String response = CliExec.execCommand(command)
            logger.info("response: " + response)
        }

        return unmappedBam
    }

    public File execute(File originalBam,  HlaEthnicity ethnicity, HlaCoverage coverage ) {

        String hlaDir = grailsApplication.config.hla.root

        String sampleId = originalBam.name.replaceAll(".bam", "")

        // create subfolder based on bam file name
        String workingFolderName = hlaDir + "/"+ sampleId
        File workingDir = new File(workingFolderName)
        workingDir.mkdirs()

        // Extract the unmapped reads from the BAM
        File unmappedBam = unmappedBam(originalBam, workingDir)

        File outputDir = new File(workingDir.absolutePath+"/results")
        outputDir.mkdirs()

        // Run HLA-genotyping
        List command = []
        command << 'hla-genotyper'
        command << originalBam.absolutePath
        command << '-u'
        command << unmappedBam.absolutePath
        command << '-e'
        command << ethnicity.name()
        command << '--'+coverage.name()
        command << '-o'
        command << outputDir

        CliExec.execCommand(command)

        return outputDir
    }

}
