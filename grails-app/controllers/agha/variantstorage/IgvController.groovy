package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import org.apache.log4j.Logger

/**
 * Controller for displaying variants in IGV.js
 * @author Philip Wu
 */
@Secured(value=["IS_AUTHENTICATED_FULLY"])
class IgvController {

    Logger logger = Logger.getLogger(IgvController.class)

    def index() {
        List vcfUrls = params.list('vcf')
        List bamUrls = params.list('bam')
        String locus = params.locus

        logger.info('vcfUrls='+vcfUrls)


        [vcfUrls: vcfUrls, bamUrls: bamUrls, locus:locus]
    }
}
