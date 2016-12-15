package agha.variantstorage

class Dataset {

    static mapping = {
        datasource 'ga4gh'
        table "Dataset"
        version false
    }

    static constraints = {
    }


    String id
    String name
}
