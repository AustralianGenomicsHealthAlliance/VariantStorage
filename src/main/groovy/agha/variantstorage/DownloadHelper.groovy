package agha.variantstorage

import org.apache.log4j.Logger

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse

/**
 * Helper class to handle Range Requests for file downloads
 *
 * Adapted from:
 * https://github.com/rvanderwerf/grails-video/blob/master/grails-app/services/com/cantina/lab/VideoService.groovy
 *
 *
 * Created by philip on 23/01/17.
 */
class DownloadHelper {

    static Logger logger = Logger.getLogger(DownloadHelper.class)

    // buffer size for servlet response buffer
    static int responseBufferSize = 1024*16

    // buffer size for transfer buffer
    static int transferBufferSize = 1024*16

    static final String CONTENT_TYPE = 'APPLICATION/OCTET-STREAM'

    // :TODO: should really be chosen as a random or a UUID or check content to ensure no overlap
    static String mimeSeparation = "gvps-mime-boundary"


    public static void download(Map params, def request, HttpServletResponse response, File file) {

        response.setHeader "Accept-Ranges", "bytes"
        List<Range> ranges = parseRange(request, response, file)

        ServletOutputStream oStream = response.outputStream

        if (!ranges) {
            //Full content response
            response.contentType = CONTENT_TYPE
            response.setHeader "Content-Length", file.length().toString()
            oStream << file.newInputStream()
        }
        else {
            // Partial content response.
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT)

            if (ranges.size() == 1) {

                Range range = ranges[0]
                response.addHeader "Content-Range", "bytes ${range.start}-${range.end}/$range.length"
                long length = range.end - range.start + 1
                if (length < Integer.MAX_VALUE) {
                    response.setContentLength((int) length)
                }
                else {
                    // Set the content-length as String to be able to use a long
                    response.setHeader "content-length", length.toString()
                }

                response.contentType = CONTENT_TYPE

                try {
                    response.setBufferSize(responseBufferSize)
                }
                catch (IllegalStateException e) {
                    logger.warn("Can't set HttpServletResponse buffer size.",e)
                }

                if (oStream) {
                    copy(file.newInputStream(), oStream, range)
                }
            }
            else {
                response.setContentType "multipart/byteranges; boundary=$mimeSeparation"

                try {
                    response.setBufferSize(responseBufferSize)
                }
                catch (IllegalStateException e) {
                    logger.warn("Can't set HttpServletResponse buffer size.",e)
                }
                if (oStream) {
                    copy(file.newInputStream(), oStream, ranges.iterator(), CONTENT_TYPE)
                }
                else {
                    // we should not get here
                    throw new IllegalStateException()
                }
            }
        }


    }

    /**
     * Copy ranges of content of the specified input stream to the specified
     * output stream, and ensure that the input stream is closed before returning
     * (even in the face of an exception).
     *
     * @param istream InputStream to read data from
     * @param ostream ServletOutputStream to write to
     * @param ranges Enumeration of the ranges the client wanted to retrieve
     * @param contentType Content type of the resource
     * @throws IOException if an input/output error occurs
     */
    protected static void copy(InputStream istream, ServletOutputStream ostream, Iterator<Range> ranges, String contentType) throws IOException {

        IOException exception
        while (exception == null && ranges.hasNext()) {

            Range currentRange = ranges.next()

            // Writing MIME header.
            ostream.println()
            ostream.println "--$mimeSeparation"
            if (contentType != null) {
                ostream.println "Content-Type: $contentType"
            }

            ostream.println "Content-Range: bytes ${currentRange.start}-${currentRange.end}/${currentRange.length}"
            ostream.println()

            // Printing content
            exception = copyRange(istream, ostream, currentRange.start, currentRange.end)

            istream.close()
        }

        ostream.println()
        ostream.print "--" + mimeSeparation + "--"

        if (exception) {
            throw exception
        }
    }

    /**
     * Copy a range of content of the specified input stream to the specified
     * output stream, and ensure that the input stream is closed before returning
     * (even in the face of an exception).
     *
     * @param instream InputStream to read from
     * @param ostream ServletOutputStream to write to
     * @param range Range the client wanted to retrieve
     * @throws IOException if an input/output error occurs
     */
    protected static void copy(InputStream istream, ServletOutputStream ostream, Range range) throws IOException {

        IOException exception = copyRange(istream, ostream, range.start, range.end)

        istream.close()

        if (exception) {
            throw exception
        }
    }

    /**
     * Copy a range of contents of the specified input stream to the specified
     * output stream.
     *
     * @param istream The input stream to read from
     * @param ostream The output stream to write to
     * @param start Start of the range which will be copied
     * @param end End of the range which will be copied
     * @return Exception which occurred during processing or null if none encountered
     */
    private static IOException copyRange(InputStream istream, ServletOutputStream ostream, long start, long end) {

        long skipped = 0
        try {
            skipped = istream.skip(start)
        }
        catch (IOException e) {
            return e
        }

        if (skipped < start) {
            return new IOException("start is less than skipped")
        }

        IOException exception
        long bytesToRead = end - start + 1

        byte[] buffer = new byte[transferBufferSize]
        int validBytes = buffer.length
        while ((bytesToRead > 0) && (validBytes >= buffer.length)) {
            try {
                validBytes = istream.read(buffer)
                // if at end of input stream
                if (validBytes<0) {
                    exception = new IOException("Attempt to read past end of input.")
                }
                // if all bytes read should be written
                else if (bytesToRead >= validBytes) {
                    ostream.write(buffer, 0, validBytes)
                    bytesToRead -= validBytes
                }
                // otherwise only write those requested
                else {
                    ostream.write(buffer, 0, (int) bytesToRead)
                    bytesToRead = 0
                }
            }
            catch (IOException e) {
                exception = e
                validBytes = -1
            }
        }

        exception
    }

    /**
     * Parse the range header.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @return Vector of ranges
     */
    private static List<Range> parseRange(def request, HttpServletResponse response, File myFile) throws IOException {

        long fileLength = myFile.length()
        if (fileLength == 0) {
            return null
        }

        String rangeHeader = request.getHeader("Range")
        if (rangeHeader == null) {
            return null
        }

        // bytes is the only range unit supported (and I don't see the point of adding new ones)
        if (!rangeHeader.startsWith("bytes")) {
            response.addHeader "Content-Range", "bytes */$fileLength"
            response.sendError HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE
            return null
        }

        rangeHeader = rangeHeader.substring(6)

        // the ranges which are successfully parsed
        List<Range> result = []
        StringTokenizer commaTokenizer = new StringTokenizer(rangeHeader, ",")

        // Parsing the range list
        while (commaTokenizer.hasMoreTokens()) {
            String rangeDefinition = commaTokenizer.nextToken().trim()

            Range currentRange = new Range()
            currentRange.length = fileLength

            int dashPos = rangeDefinition.indexOf('-')
            if (dashPos == -1) {
                response.addHeader "Content-Range", "bytes */$fileLength"
                response.sendError HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE
                return null
            }

            if (dashPos == 0) {
                try {
                    long offset = Long.parseLong(rangeDefinition)
                    currentRange.start = fileLength + offset
                    currentRange.end = fileLength - 1
                }
                catch (NumberFormatException e) {
                    response.addHeader("Content-Range", "bytes */" + fileLength)
                    response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                    return null
                }
            }
            else {
                try {
                    currentRange.start = Long.parseLong(rangeDefinition.substring(0, dashPos))
                    if (dashPos < rangeDefinition.length() - 1) {
                        currentRange.end = Long.parseLong(rangeDefinition.substring(dashPos + 1, rangeDefinition.length()))
                    }
                    else {
                        currentRange.end = fileLength - 1
                    }
                }
                catch (NumberFormatException e) {
                    response.addHeader "Content-Range", "bytes */$fileLength"
                    response.sendError HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE
                    return null
                }
            }

            if (!currentRange.validate()) {
                response.addHeader("Content-Range", "bytes */" + fileLength)
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                return null
            }

            result.add(currentRange)
        }

        return result
    }

    /**
     * Range of content to serve.
     *
     * These ranges are inclusive, the byte at offset end is part of the range.
     */
    private static class Range {

        long start
        long end
        long length

        boolean validate() {
            if (end >= length) {
                end = length - 1
            }
            return (start >= 0) && (end >= 0) && (start <= end) && (length > 0)
        }
    }

}


