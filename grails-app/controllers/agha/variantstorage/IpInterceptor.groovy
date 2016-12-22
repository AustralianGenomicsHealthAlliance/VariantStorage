package agha.variantstorage

import grails.util.Environment
import org.apache.log4j.Logger

import javax.servlet.http.HttpServletResponse

class IpInterceptor {

    Logger logger = Logger.getLogger(IpInterceptor.class)

    public IpInterceptor() {
        // match all requests for all controllers
        matchAll()
    }

    boolean before() {
        Boolean valid = false

        if (Environment.current == Environment.DEVELOPMENT) {
            valid = true
        } else {
            List ipConfigs = IpConfig.findAll()
            valid = IpAddressHelper.validateIpFromRequest(request, ipConfigs)
            logger.info('valid=' + valid)
            if(!valid){
                render (status: HttpServletResponse.SC_FORBIDDEN, text: 'ERROR: Your IP address is not registered for access')
                return false
            }
        }

        logger.info("ipFilter valid="+valid)
        return valid


    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
