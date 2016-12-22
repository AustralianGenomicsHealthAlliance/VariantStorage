<%@ page import="agha.variantstorage.*" %>

<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Cohorts</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />

    <style>

        table td {
            border-bottom: 1px solid #e4e4e4;
            padding: 10px;
        }

    </style>
</head>
<body>


<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>Cohorts</h1>

        <div >
            <table>
                <tr>
                    <th>Cohort</th>
                    <th>VCFs</th>
                    <th>BAMs</th>
                </tr>
                <g:each in="${datasets}" var="dataset">
                    <tr>
                        <td>
                            ${dataset.name?.encodeAsHTML()}
                        </td>
                        <td>
                            <g:each in="${variantSetMap[dataset.id]}" var="variantSet" >
                                <g:if test="${variantSet}">
                                    <div>
                                        <g:link controller="variantSet" action="show" params="[id: variantSet.id]">
                                            ${variantSet.name?.encodeAsHTML()}
                                        </g:link>
                                    </div>
                                </g:if>
                            </g:each>
                        </td>
                        <td>
                            <g:each in="${readGroupSetMap[dataset.id]}" var="readGroupSet" >
                                <g:if test="${readGroupSet}">
                                    <div>
                                            <g:link controller="readGroupSet" action="show" params="[id: readGroupSet.id]">
                                                ${readGroupSet.name?.encodeAsHTML()}
                                            </g:link>
                                    </div>
                                </g:if>
                            </g:each>
                        </td>
                    </tr>
                </g:each>
            </table>
        </div>
    </section>
</div>

</body>
</html>
