package agha.variantstorage

/**
 * ReadGroupSet domain class for GA4GH database
 *
 * @author Philip Wu
 */
class ReadGroupSet {

    static mapping = {
        datasource 'ga4gh'
        table "ReadGroupSet"
        version false
        datasetId column: 'datasetId'
        referenceSetId column: 'referenceSetId'
        dataUrl column: 'dataUrl'
        indexFile column: 'indexFile'
    }

    static constraints = {
    }

    String id
    String name
    String datasetId
    String referenceSetId
    String dataUrl
    String stats
    String programs
    String indexFile
}
