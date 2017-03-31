package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured

@Secured(value=["IS_AUTHENTICATED_FULLY"])
class HlaController {

    HlaGenotyperService hlaGenotyperService

    def index() {

        File bam = new File('/home/philip/ga4gh-server-env/cpi_datasets/v2.3/APOSLE_cohort88/bam_links/APOSLE_cohort88_sg1_affected1.bam')

        File workingDir = new File('/home/philip/hla/APOSLE_cohort88_sg1_affected1')

        //hlaGenotyperService.unmappedBam(bam, workingDir)
        hlaGenotyperService.execute(bam, HlaGenotyperService.HlaEthnicity.EUR, HlaGenotyperService.HlaCoverage.genome)

        render "Completed"
    }
}
