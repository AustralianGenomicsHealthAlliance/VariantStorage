<!doctype html>
<%@ page import="agha.variantstorage.*" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>IGV Online - Integrated genomics viewer</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>

    <h1>Integrated Genomics Viewer</h1>

    <div id="igvDiv" style="padding-top: 10px;padding-bottom: 10px; border:1px solid lightgray">
    </div>


    <script type="text/javascript">

        var div = document.getElementById('igvDiv');
        var options = {
            reference: {
                id: "hg19",
                fastaURL: "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/seq/1kg_v37/human_g1k_v37_decoy.fasta",
                cytobandURL: "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/seq/b37/b37_cytoband.txt"
            },
            locus: "myc",
            tracks: [
                {
                    name: "Genes",
                    type: "annotation",
                    format: "bed",
                    sourceType: "file",
                    url: "https://s3.amazonaws.com/igv.broadinstitute.org/annotations/hg19/genes/refGene.hg19.bed.gz",
                    indexURL: "https://s3.amazonaws.com/igv.broadinstitute.org/annotations/hg19/genes/refGene.hg19.bed.gz.tbi",
                    order: Number.MAX_VALUE,
                    visibilityWindow: 300000000,
                    displayMode: "EXPANDED"
                },

                {
                    name: "Phase 3 WGS variants",
                    format: "vcf",
                    url: "https://s3.amazonaws.com/1000genomes/release/20130502/ALL.wgs.phase3_shapeit2_mvncall_integrated_v5b.20130502.sites.vcf.gz",
                    indexURL:  "https://s3.amazonaws.com/1000genomes/release/20130502/ALL.wgs.phase3_shapeit2_mvncall_integrated_v5b.20130502.sites.vcf.gz.tbi",
                    type: "variant"
                },

                <g:each in="${ vcfUrls}" var="vcfUrl">
                {
                    name: "${ org.apache.commons.io.FilenameUtils.getName(vcfUrl)}",
                    format: "vcf",
                    url: "${vcfUrl}",
                    indexURL:  "${vcfUrl}.tbi",
                    type: "variant"
                },

                </g:each>

                <g:each in="${ bamUrls}" var="bamUrl">
                {
                    name: "${ org.apache.commons.io.FilenameUtils.getName(bamUrl)}",
                    format: "bam",
                    url: "${bamUrl}",
                    indexURL:  "${bamUrl}.bai",
                    type: "alignment"
                },

                </g:each>

            ]

        };
        var browser = igv.createBrowser(div, options);

    </script>

</body>
</html>
