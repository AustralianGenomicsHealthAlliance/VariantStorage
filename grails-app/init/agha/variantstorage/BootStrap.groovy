package agha.variantstorage

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.dataformat.YAMLLibrary

class BootStrap {

    Ga4ghService ga4ghService
    CamelContext camelCtx
    CpiRegistrationService cpiRegistrationService
    def grailsApplication

    def init = { servletContext ->
        System.out.println("init out")

        initSecurityRoles()
        initAdmin()
        //initCamel()
        //testVariantSet()

        //cpiRegistrationService.registerVcfsBams()
    }

    def initAdmin() {
        createUser('admin', 'philip.wu@anu.edu.au', 'agha123', RoleType.ADMIN)
    }

    def initSecurityRoles() {
        // Initialize each user role
        for (RoleType rt : RoleType.values()) {
            new Role(authority: rt.toString()).save(flush: true)
        }
    }

    private void createUser(String username,String email, String password, RoleType roleType){
        Role role = Role.findByAuthority(roleType.toString())
        User user = User.findByUsername(username)
        if (user == null) {
            user = new User(username: username, email: email, displayName: username, enabled: true, password: password)
            boolean saved = user.save(flush: true)
            System.out.println("Saved? "+saved+" Errors saving user [" + username + "]: "+user.errors)
        }

        UserRole userRole = UserRole.findByUserAndRole(user, role)
        if (userRole == null) {
            userRole = new UserRole(user: user, role: role)
            boolean saved = userRole.save(flush: true)
            System.out.println("Saved? "+saved+" Errors saving user role [user:" + username + ", role:" + roleType.toString() + "]: "+userRole.errors)
        }
    }

    def initCamel() {
        System.out.println("Listening to folder: "+grailsApplication.config.camel.import)

        camelCtx = new DefaultCamelContext()

        camelCtx.addRoutes( new RouteBuilder() {
            @Override
            void configure() throws Exception {
                from(grailsApplication.config.camel.import)
                    .choice()
                        .when(header("CamelFileName").endsWith(".yml"))
                            .unmarshal()
                            .yaml(YAMLLibrary.SnakeYAML)
                            .process(new Processor() {

                                @Override
                                void process(Exchange exchange) throws Exception {


                                    String filename = exchange.getIn().getHeader("CamelFileName")
                                    System.out.println("registerDataset: "+filename)
                                    def yamlObj = exchange.getIn().body

                                    ga4ghService.registerDataset(yamlObj)
                                }

                            })
                        .otherwise()
                            .log("Bad registration file: "+header("CamelFileName"))
                    .end()
                        //.to(grailsApplication.config.camel.import+'/archived')
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
