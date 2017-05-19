package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Secured(value=["IS_AUTHENTICATED_FULLY"])
class BamController {

    BamService bamService

    def bam() {

        List files = bamService.findBam(params.pipelineVersion, params.cohortId, params.sampleName)

        JSONObject jsonObj = new JSONObject()
        long totalSize = 0
        if (files) {
            JSONArray filesJson = new JSONArray()
            for (File file: files) {
                JSONObject jsonFile = new JSONObject()
                jsonFile.put('name', file.name)
                jsonFile.put('size', file.length())
                //jsonFile.put('absolutePath', file.absolutePath)
                filesJson.add(jsonFile)

                totalSize += file.length()
            }
            jsonObj.put('files', filesJson)
            jsonObj.put('totalSize', totalSize)
        }

        withFormat {
            html {
                //respond rgs, model:[files: files]
                render jsonObj
            }
            json {
                render jsonObj
            }
        }
    }
}
