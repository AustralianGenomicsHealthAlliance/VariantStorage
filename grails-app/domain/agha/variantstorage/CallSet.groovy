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

    public String getDatasetName() {
        String datasetName = null
        VariantSet.withTransaction {
            VariantSet vs = VariantSet.findById(variantSetId)
            if (vs != null) {
                Dataset ds = Dataset.findById(vs.datasetId)
                datasetName = ds.name
            }
        }

        return datasetName
    }
}
