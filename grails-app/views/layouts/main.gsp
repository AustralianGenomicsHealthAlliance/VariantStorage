<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Variant Storage"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <asset:javascript src="jquery-2.2.0.min.js"/>
    <asset:javascript src="jquery-ui.min.js"/>

    <asset:stylesheet src="jquery-ui.min.css" />
    <asset:stylesheet src="jquery-ui.theme.min.css" />
    <asset:stylesheet src="application.css"/>

    <asset:javascript src="igv-1.0.6.js" />
    <asset:stylesheet src="igv-1.0.6.css"/>


    <g:layoutHead/>
</head>
<body>

    <div class="navbar navbar-default navbar-static-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="${grailsApplication.config.grails.serverURL}">
                    <i class="fa grails-icon">
                        <asset:image src="agha_logo.png"/>
                    </i> Variant Storage
                </a>
            </div>
            <div class="navbar-collapse collapse" aria-expanded="false" style="height: 0.8px;">
                <ul class="nav navbar-nav navbar-right">
                    <g:pageProperty name="page.nav" />
                </ul>
            </div>
        </div>
    </div>

    <g:layoutBody/>

    <div class="footer" role="contentinfo"></div>

    <div id="spinner" class="spinner" style="display:none;">
        <g:message code="spinner.alt" default="Loading&hellip;"/>
    </div>



</body>
</html>
