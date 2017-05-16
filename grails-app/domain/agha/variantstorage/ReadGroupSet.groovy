package agha.variantstorage

/**
 * ReadGroupSet domain class for GA4GH database
 *
 * @author Philip Wu
 */
class ReadGroupSet {

    static mapping = {
        //datasource 'ga4gh'
        table "ReadGroupSet"
        version false
        datasetId column: 'datasetId'
        referenceSetId column: 'referenceSetId'
        dataUrl column: 'dataUrl'
        indexFile column: 'indexFile'
    }

    static constraints = {
    }

    static hasMany = [readGroups: ReadGroup]

    String id
    String name
    String datasetId
    String referenceSetId
    String dataUrl
    String stats
    String programs
    String indexFile
    Set<ReadGroup> readGroups

    public static List<ReadGroupSet> findReadGroupSetByDatasetIdAndSampleName(String datasetId, String sampleName) {

        String innerSql = 'SELECT DISTINCT rgs.id FROM '+ReadGroupSet.class.name+' rgs JOIN rgs.readGroups rg WHERE rgs.datasetId=? AND UPPER(rg.sampleName)=?'
        String sql = 'SELECT rgs2 FROM '+ReadGroupSet.class.name+' rgs2 WHERE rgs2.id IN ('+innerSql+')'
        ReadGroupSet.withTransaction {

            return ReadGroupSet.executeQuery(sql, [datasetId, sampleName])
        }

        //String innerSql = 'SELECT DISTINCT rgs.id FROM '+ReadGroupSet.class.name+' rgs WHERE rgs.datasetId=?'
        //String sql = 'SELECT rg FROM 'ReadGroup.class.name+' rg WHERE rg.readGroupSetI'
    }
}
