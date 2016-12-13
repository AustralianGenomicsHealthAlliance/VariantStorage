package agha.variantstorage

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DownloadController {

    def index() { }

    /**
     * Download all the files for a variant set
     */
    def variantSet() {
        VariantSet vs = VariantSet.findById(params.id)

        // Parse out the list of filenames associated with this VariantSet
        List<String> filePaths = vs.parseFilePaths()

        // Compress the files into a single file for download
        ZipOutputStream zipFile = new ZipOutputStream(response.outputStream)
        for (String filePath: filePaths) {
            File file = new File(filePath)
            zipFile.putNextEntry(new ZipEntry(file.getName()))
            def buffer = new byte[1024]
            file.withInputStream { i ->
                def l = i.read(buffer)
                if (l > 0) {
                    zipFile.write(buffer, 0, l)
                }
            }
            zipFile.closeEntry()
        }
        //zipFile.close()

        String filename = vs.name+".zip"
        response.setHeader("Content-disposition", "filename=${fileName}")
        response.outputStream.flush()
    }
}
