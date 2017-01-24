package agha.variantstorage

class UrlMappings {

    static mappings = {

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/download/vcf/$variantSetId/$filename"(controller:'download', action:'vcf')
        "/download/bam/$readGroupSetId/$filename"(controller:'download', action:'bam')

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
