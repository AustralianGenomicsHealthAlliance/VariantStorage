<!doctype html>
<%@ page import="agha.variantstorage.*" %>

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

            <table>
                <tr>
                    <th>Filename</th>
                    <th>Size</th>
                    <th>IGV</th>
                </tr>
                <g:each in="${files}" var="file" >
                    <g:set var="vcfUrl" value="${createLink(uri:'/download/vcf/'+variantSet.id+'/'+file.name?.encodeAsHTML())}" />
                    <tr>
                        <td>
                            <a href="${vcfUrl}">
                                ${file.name?.encodeAsHTML()}
                            </a>
                        </td>
                        <td>
                            <g:if test="file?.length()">
                                ${ Math.round(file.length() / 1024)} bytes
                            </g:if>
                        </td>
                        <td>
                            <g:if test="${file.name?.endsWith('.vcf.gz')}">
                                <g:if test="${readGroupSet}">
                                    <g:set var="bamUrl" value="${createLink(uri:'/download/bam/'+readGroupSet.id+'/'+readGroupSet.name?.encodeAsHTML()+'.bam')}" />
                                    <g:link controller="igv" params="[vcf: vcfUrl, bam: bamUrl, locus: chrMap[file.name] ]">
                                        View in IGV
                                    </g:link>
                                </g:if>
                                <g:else>
                                    <g:link controller="igv" params="[vcf: vcfUrl, locus: chrMap[file.name]]">
                                        View in IGV
                                    </g:link>
                                </g:else>
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
