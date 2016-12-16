package agha.variantstorage

/**
 * restrict access by IP address ranges saved in the database
 * @author Philip
 *
 */
class IpConfig {

    static constraints = {
    }

    String type
    String startIp
    String endIp
}
