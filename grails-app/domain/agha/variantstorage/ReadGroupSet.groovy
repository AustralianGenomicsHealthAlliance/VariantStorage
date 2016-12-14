package agha.variantstorage

/**
 * ReadGroupSet domain class for GA4GH database
 *
 * @author Philip Wu
 */
class ReadGroupSet {

    static mapping = {
        table "ReadGroupSet"
        version false
        datasetId column: 'datasetId'
        referenceSetId column: 'referenceSetId'
        dataUrl column: 'dataUrl'
    }

    static constraints = {
    }

    String id
    String name
    String datasetId
    String referenceSetId
    String dataUrl
}
