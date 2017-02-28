package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured

/**
 * Import sequence data
 *
 * @ Philip Wu
 */
@Secured(["ROLE_ADMIN"])
class ImportController {

    CpiRegistrationService cpiRegistrationService

    def index() { }

    def importCpi() {
        cpiRegistrationService.registerVcfsBams()
        render('Import completed')
    }
}
