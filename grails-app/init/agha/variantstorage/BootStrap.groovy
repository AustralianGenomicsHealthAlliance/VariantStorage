package agha.variantstorage

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext

class BootStrap {

    Ga4ghRegistrationService ga4ghRegistrationService
    CamelContext camelCtx

    def init = { servletContext ->
        System.out.println("init out")

        initCamel()
        testVariantSet()

    }

    def initCamel() {

        camelCtx = new DefaultCamelContext()

        camelCtx.addRoutes( new RouteBuilder() {
            @Override
            void configure() throws Exception {
                from("file:/home/philip/ga4gh_registration?noop=true")
                    .process(new Processor() {

                    @Override
                    void process(Exchange exchange) throws Exception {
                        ga4ghRegistrationService.registerDataset()
                    }

                }).to("file:/home/philip/ga4gh_registration_archive")
            }
        } )

        camelCtx.start()

    }

    def testVariantSet() {
        List variantSets = VariantSet.findAll()
        System.out.println("num of variantSets: "+variantSets?.size())
        for (VariantSet vs : variantSets) {
            System.out.println("name: "+vs.name)
        }
    }

    def destroy = {
        camelCtx.stop()
    }
}
