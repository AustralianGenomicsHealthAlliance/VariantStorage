package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Secured(value=["IS_AUTHENTICATED_FULLY"])
class VcfController {

    VcfService vcfService

    def unfiltered() {

        List files = vcfService.findUnfilteredVcfs(params.pipelineVersion, params.cohortId, params.sampleName)


        withFormat {
            html {
                //respond rgs, model:[files: files]
                render files
            }
            json {
                JSONObject json = new JSONObject()
                long totalSize = 0
                if (files) {
                    JSONArray filesJson = new JSONArray()
                    for (File file: files) {
                        JSONObject jsonFile = new JSONObject()
                        jsonFile.put('name': file.name)
                        jsonFile.put('size': file.length())
                        //jsonFile.put('absolutePath', file.absolutePath)
                        filesJson.add(jsonFile)

                        totalSize += file.length()
                    }
                    json.put('files', filesJson)
                    json.put('totalSize': totalSize)
                }

                render json
            }
        }
    }
}
