package agha.variantstorage

class CallSet {

    static mapping = {
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
