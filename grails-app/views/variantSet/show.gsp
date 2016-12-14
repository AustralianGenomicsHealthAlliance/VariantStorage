<%@ page import="agha.variantstorage.*" %>

<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>VariantSet for ${variantSet.name?.encodeAsHTML()} </title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>


<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>Data files for ${variantSet.name?.encodeAsHTML()}</h1>

        <div>Reference Set ID: ${variantSet.referencesetId}</div>

        <div>
            <g:link controller="download" action="variantSet" params="[id: variantSet.id]">
                Download all files
            </g:link>
        </div>

        <fieldset>
            <legend>Files</legend>

                <g:each in="${files}" var="file" >
                    <br/>${file?.encodeAsHTML()}
                </g:each>

        </fieldset>
    </section>
</div>

</body>
</html>
