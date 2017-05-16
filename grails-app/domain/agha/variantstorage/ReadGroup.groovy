package agha.variantstorage

/**
 * ReadGroup ga4gh domain class
 * Created by philip on 25/01/17.
 */
class ReadGroup {

    static mapping = {
        //datasource 'ga4gh'
        table "ReadGroup"
        version false
        readGroupSet column: 'readGroupSetId'
        sampleName column: 'sampleName'
    }

    static belongsTo = [readGroupSet: ReadGroupSet]

    String id
    String name
    ReadGroupSet readGroupSet
    String sampleName
    String stats
    String experiment

    static constraints = {
    }

}
