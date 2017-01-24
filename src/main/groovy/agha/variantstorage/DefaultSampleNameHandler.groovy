package agha.variantstorage

import htsjdk.variant.vcf.VCFFileReader
import htsjdk.variant.vcf.VCFHeader

/**
 * Retrieve the sample name based on the header attributes in the VCF file
 * Created by philip on 22/12/16.
 */
class DefaultSampleNameHandler implements SampleNameHandler {


    @Override
    List<String> getSampleNames(File file) {
        VCFFileReader vcfFileReader = new VCFFileReader(file)
        VCFHeader vcfHeader = vcfFileReader.getFileHeader()
        return vcfHeader.getGenotypeSamples()
    }
}
