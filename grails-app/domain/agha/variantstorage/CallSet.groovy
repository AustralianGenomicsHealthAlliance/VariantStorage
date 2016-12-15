package agha.variantstorage

class CallSet {

    static mapping = {
        datasource 'ga4gh'
        table "CallSet"
        version false

        variantSetId column:'variantSetId'
    }

    static constraints = {
    }


    String id
    String name
    String variantSetId
}
