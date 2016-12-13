<%@ page import="agha.variantstorage.*" %>

<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>CallSets</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>


<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>CallSets</h1>

        <div >
            <table>
                <tr>
                   <th>Sample name</th>
                    <th>Variant Set ID</th>
                </tr>
                <g:each in="${CallSet.findAll(sort:'name')}" var="callset">
                    <tr>
                        <td>
                            <g:link controller="variantSet" action="show" params="[id: callset.variantSetId]">
                            ${callset.name}
                            </g:link>
                        </td>
                        <td>${callset.variantSetId}</td>
                    </tr>
                </g:each>
            </table>
        </div>
    </section>
</div>

</body>
</html>
