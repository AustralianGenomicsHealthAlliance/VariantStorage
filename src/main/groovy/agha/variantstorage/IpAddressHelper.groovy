package agha.variantstorage

import org.apache.log4j.Logger

/**
 * Created by philip on 20/12/16.
 */
class IpAddressHelper {

    static Logger logger = Logger.getLogger(IpAddressHelper.class.getName())

    /**
     * Validate that the requestor has a valid IP address
     * The class definition for validIpConfigs must have fields startIp and endIp as strings
     * @param request
     * @param validIpConfigs
     * @return
     */
    public static boolean validateIpFromRequest(def request, Collection validIpConfigs) {
        boolean valid = false

        String invokerIp = request.getRemoteAddr()	// Requires that the AJP connector be configured in tomcat to work in production
        //String invokerIpForwarded = request.getHeader("X-Forwarded-For")	// WARNING: This can be faked
        String invokerIpClient = request.getHeader("Client-IP")
        logger.info('invokerIp=' + invokerIp + "|invokerIpClient=" + invokerIpClient)
        /*
        if(!isValidIp(invokerIp)){
            invokerIp = invokerIpForwarded
        }*/

        // For localhost return true
        if (invokerIp == '0:0:0:0:0:0:0:1') {
            return false
        }

        if(!isValidIp(invokerIp)){
            invokerIp = invokerIpClient
        }

        if(isValidIp(invokerIp)){
            Long invokerIpLong = convertIpToLong(invokerIp)
            logger.info('invokerIp=' + invokerIp + " long=" + invokerIpLong)
            //List ipRange = IpConfig.findAllByType('WEBSERVICES')
            logger.info('ipRange.size()=' + validIpConfigs.size())

            validIpConfigs?.each{
                if(!valid){
                    Long startIpLong = 	convertIpToLong(it.startIp)
                    Long endIpLong = 	convertIpToLong(it.endIp)
                    logger.info('startIpLong=' + startIpLong + " endIpLong=" + endIpLong)

                    if(invokerIpLong >= startIpLong && invokerIpLong <= endIpLong){
                        valid = true
                    }
                }
            }
        }


        return valid
    }

    public static Boolean isValidIp(String ipAddr){
        Boolean valid = false
        if(ipAddr){
            List ipParts = ipAddr.tokenize('.')
            if(ipParts.size()==4){
                Boolean allPartsValid = true
                ipParts.each{
                    if(it.size()>3 || !it.isLong()){
                        allPartsValid = false
                    }
                }
                if(allPartsValid){
                    valid = true
                }
            }
        }
        return valid
    }

    public static Long convertIpToLong(String ipAddr){
        String ipString = ''
        List ipParts = ipAddr.tokenize('.')
        ipString = zeroPad(ipParts[0].toLong(), 3)
        ipString += zeroPad(ipParts[1].toLong(), 3)
        ipString += zeroPad(ipParts[2].toLong(), 3)
        ipString += zeroPad(ipParts[3].toLong(), 3)

        return ipString.toLong()
    }

    public static String zeroPad( long number, int width )
    {
        StringBuffer result = new StringBuffer("");
        for( int i = 0; i < width-Long.toString(number).length(); i++ )
            result.append( "0" );
        result.append( Long.toString(number) );
        return result.toString();
    }

}
