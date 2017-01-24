package agha.variantstorage

import htsjdk.variant.vcf.VCFFileReader
import htsjdk.variant.vcf.VCFHeader

/**
 * Retrieve the sample name based on the file name
 * Created by philip on 24/01/17.
 */
class FileSampleNameHandler implements SampleNameHandler {


    @Override
    List<String> getSampleNames(File file) {
        List<String> sampleNames = []

        sampleNames << file.name.split(("\\."))[0]

        return sampleNames
    }
}
