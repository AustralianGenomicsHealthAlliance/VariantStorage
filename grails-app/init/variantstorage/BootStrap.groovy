package variantstorage

import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.log4j.Logger

class BootStrap {

    Logger logger = Logger.getLogger(BootStrap.class.getName())

    CamelContext camelCtx

    def init = { servletContext ->
        System.out.println("init out")
        logger.info("init")

        initCamel()


    }

    def initCamel() {

        camelCtx = new DefaultCamelContext()

        camelCtx.addRoutes( new RouteBuilder() {
            @Override
            void configure() throws Exception {
                from("file:/home/philip/ga4gh_registration?noop=true").to("file:/home/philip/ga4gh_registration_archive")
            }
        } )

        camelCtx.start()

    }

    def destroy = {
        camelCtx.stop()
    }
}
