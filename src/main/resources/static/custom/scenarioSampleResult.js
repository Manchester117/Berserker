var sampleResultDetailData = null;

$.ajax({
    url: "/getSampleResultDetailData",
    type: "post",
    data: {
        resultId: $("#result_id").val(),
        dataType: $("#data_type").val()
    },
    success: function(resultData) {
        sampleResultDetailData = resultData;
        console.log(sampleResultDetailData);
    }
});

$("#container").Highcharts({
    chart: {
        zoomType: 'x'
    },
    boost: {
        enabled: true,
        useGPUTranslations: true
    },
    title: {
        text: $("#chart_title").val()
    },
    subtitle: {
        text: 'Using the Boost module'
    },
    tooltip: {
        valueDecimals: 2
    },
    series: [
        {
            data: sampleResultDetailData,
            lineWidth: 0.5
        }
    ]
});