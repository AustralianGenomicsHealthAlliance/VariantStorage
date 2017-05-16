package agha.variantstorage

class Dataset {

    static mapping = {
        //datasource 'ga4gh'
        table "Dataset"
        version false
    }

    static constraints = {
        name (nullable: false, unique: true)
    }


    //String id
    Long id
    String name
}
