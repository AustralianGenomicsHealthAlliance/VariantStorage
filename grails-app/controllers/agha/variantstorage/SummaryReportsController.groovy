package agha.variantstorage

import grails.plugin.springsecurity.annotation.Secured
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

@Secured(value=["IS_AUTHENTICATED_FULLY"])
class SummaryReportsController {

    SummaryReportsService summaryReportsService

    def list() {

        List files = summaryReportsService.findFiles(params.pipelineVersion, params.cohortId, params.sampleName)

        withFormat {
            html {
                //respond rgs, model:[files: files]
                render files
            }
            json {
                JSONObject json = new JSONObject()
                if (files) {
                    JSONArray filesJson = new JSONArray()
                    for (File file: files) {
                        JSONArray jsonFile = new JSONArray()
                        jsonFile.add(file.name)
                        jsonFile.add(file.length())
                        //jsonFile.put('absolutePath', file.absolutePath)
                        filesJson.add(jsonFile)
                    }
                    json.put('data', filesJson)
                }

                render json
            }
        }
    }


}
