<%@ page import="agha.variantstorage.*" %>

<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>ReadGroupSet for ${readGroupSet.name?.encodeAsHTML()} </title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>


<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>BAM files for ${readGroupSet.name?.encodeAsHTML()}</h1>

        <div>Reference Set ID: ${readGroupSet.referenceSetId}</div>

        <div>Stats: ${readGroupSet.stats?.encodeAsHTML()}</div>

        <div>Program: ${readGroupSet.programs?.encodeAsHTML()}</div>

        <div>
            <g:link controller="download" action="readGroupSet" params="[id: readGroupSet.id]">
                Download all files
            </g:link>
        </div>

        <fieldset>
            <legend>Files</legend>

            <table>
                <tr>
                    <th>Filename</th>
                    <th>Size</th>
                </tr>
                <g:each in="${files}" var="file" >
                    <tr>
                        <td>${file.name?.encodeAsHTML()}</td>
                        <td>
                            <g:if test="file?.length()">
                                ${ Math.round(file.length() / 1024 / 1024)} MB
                            </g:if>
                        </td>
                    </tr>
                </g:each>
            </table>

        </fieldset>
    </section>
</div>

</body>
</html>
